/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.RulesTimer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionsCounter;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextImpl;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory.Engine;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines for concurrently computing the saturation of class
 * expressions. This is the class that implements the application of inference
 * rules.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * 
 */
public class RuleApplicationFactory implements
		InputProcessorFactory<IndexedClassExpression, Engine> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(RuleApplicationFactory.class);

	/**
	 * Cached constants
	 */
	private final IndexedClassExpression owlThing_, owlNothing_;

	/**
	 * The set of reflexive properties of the ontology
	 */
	private final Set<IndexedObjectProperty> reflexiveProperties_;

	/**
	 * The queue containing all activated contexts. Every activated context
	 * occurs exactly once.
	 */
	private final Queue<Context> activeContexts_;

	/**
	 * The approximate number of contexts ever created by this engine. This
	 * number is used not only for statistical purposes, but also by saturation
	 * engine callers to control when to submit new saturation tasks: if there
	 * are too many unprocessed contexts, new saturation tasks are not
	 * submitted.
	 */
	protected final AtomicInteger approximateContextNumber_ = new AtomicInteger(
			0);

	/**
	 * To reduce thread congestion, {@link #approximateContextNumber_} is not
	 * updated immediately when contexts are created, but after a worker creates
	 * {@link #contextUpdateInterval_} new contexts.
	 */
	private final int contextUpdateInterval_ = 32;

	/**
	 * The {@link ConclusionsCounter} aggregated for all workers
	 */
	private final ConclusionsCounter aggregatedConclusionsCounter_;

	/**
	 * The {@link RulesTimer} aggregated for all workers
	 */
	private final RulesTimer aggregatedRulesTimer_;

	public RuleApplicationFactory(OntologyIndex ontologyIndex) {
		this.activeContexts_ = new ConcurrentLinkedQueue<Context>();
		this.aggregatedConclusionsCounter_ = new ConclusionsCounter();
		this.aggregatedRulesTimer_ = new RulesTimer();
		this.reflexiveProperties_ = new ArrayHashSet<IndexedObjectProperty>();
		Collection<IndexedObjectProperty> reflexiveObjectProperties = ontologyIndex
				.getReflexiveObjectProperties();
		if (reflexiveObjectProperties != null)
			reflexiveProperties_.addAll(reflexiveObjectProperties);

		owlThing_ = ontologyIndex.getIndexed(PredefinedElkClass.OWL_THING);
		owlNothing_ = ontologyIndex.getIndexed(PredefinedElkClass.OWL_NOTHING);

	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		checkStatistics();
	}

	/**
	 * Returns the approximate number of contexts created. The real number of
	 * contexts is larger, but to ensure good concurrency performance it is not
	 * updated often. It is exact when all engines created by this factory
	 * finish.
	 * 
	 * @return the approximate number of created contexts
	 */
	public int getApproximateContextNumber() {
		return approximateContextNumber_.get();
	}

	/**
	 * Prints statistic of rule applications
	 */
	public void printStatistics() {
		if (LOGGER_.isDebugEnabled()) {
			checkStatistics();
			if (approximateContextNumber_.get() > 0)
				LOGGER_.debug("Contexts created:" + approximateContextNumber_);
			if (aggregatedConclusionsCounter_.getSuperClassExpressionInfNo() > 0)
				LOGGER_.debug("Derived Produced/Unique:"
						+ aggregatedConclusionsCounter_
								.getSuperClassExpressionInfNo()
						+ "/"
						+ aggregatedConclusionsCounter_
								.getSuperClassExpressionNo());
			if (aggregatedConclusionsCounter_.getBackLinkInfNo() > 0)
				LOGGER_.debug("Backward Links Produced/Unique:"
						+ aggregatedConclusionsCounter_.getBackLinkInfNo()
						+ "/" + aggregatedConclusionsCounter_.getBackLinkNo());
			if (aggregatedConclusionsCounter_.getForwLinkInfNo() > 0)
				LOGGER_.debug("Forward Links Produced/Unique:"
						+ aggregatedConclusionsCounter_.getForwLinkInfNo()
						+ "/" + aggregatedConclusionsCounter_.getForwLinkNo());
			if (aggregatedRulesTimer_
					.getObjectSomeValuesFromCompositionRuleTime() > 0)
				LOGGER_.debug("ObjectSomeValuesFrom composition time: "
						+ aggregatedRulesTimer_
								.getObjectSomeValuesFromCompositionRuleTime()
						+ " ms.");
			if (aggregatedRulesTimer_
					.getObjectSomeValuesFromDecompositionRuleTime() > 0)
				LOGGER_.debug("ObjectSomeValuesFrom decomposition time: "
						+ aggregatedRulesTimer_
								.getObjectSomeValuesFromDecompositionRuleTime()
						+ " ms.");
			if (aggregatedRulesTimer_
					.getObjectSomeValuesFromBackwardLinkRuleTime() > 0)
				LOGGER_.debug("ObjectSomeValuesFrom backward link time: "
						+ aggregatedRulesTimer_
								.getObjectSomeValuesFromBackwardLinkRuleTime()
						+ " ms.");
			if (aggregatedRulesTimer_
					.getObjectIntersectionOfCompositionRuleTime() > 0)
				LOGGER_.debug("ObjectIntersectionOf composition time: "
						+ aggregatedRulesTimer_
								.getObjectIntersectionOfCompositionRuleTime()
						+ " ms.");
			if (aggregatedRulesTimer_
					.getObjectIntersectionOfDecompositionRuleTime() > 0)
				LOGGER_.debug("ObjectIntersectionOf decomposition time: "
						+ aggregatedRulesTimer_
								.getObjectIntersectionOfDecompositionRuleTime()
						+ " ms.");
			if (aggregatedRulesTimer_.getForwardLinkBackwardLinkRuleTime() > 0)
				LOGGER_.debug("ForwardLink backward link time: "
						+ aggregatedRulesTimer_
								.getForwardLinkBackwardLinkRuleTime() + " ms.");
			if (aggregatedRulesTimer_.getClassDecompositionRuleTime() > 0)
				LOGGER_.debug("Class decomposition time: "
						+ aggregatedRulesTimer_.getClassDecompositionRuleTime()
						+ " ms.");
			if (aggregatedRulesTimer_.getClassBottomBackwardLinkRuleTime() > 0)
				LOGGER_.debug("owl:Nothing backward link time: "
						+ aggregatedRulesTimer_
								.getClassBottomBackwardLinkRuleTime() + " ms.");
			if (aggregatedRulesTimer_.getSubClassOfRuleTime() > 0)
				LOGGER_.debug("SubClassOf expansion time: "
						+ aggregatedRulesTimer_.getSubClassOfRuleTime()
						+ " ms.");
		}
	}

	private void checkStatistics() {
		if (aggregatedConclusionsCounter_.getSuperClassExpressionInfNo() < aggregatedConclusionsCounter_
				.getSuperClassExpressionNo())
			LOGGER_.error("More unique derived superclasses than produced!");
		if (aggregatedConclusionsCounter_.getBackLinkInfNo() < aggregatedConclusionsCounter_
				.getBackLinkNo())
			LOGGER_.error("More unique backward links than produced!");
		if (aggregatedConclusionsCounter_.getForwLinkInfNo() < aggregatedConclusionsCounter_
				.getForwLinkNo())
			LOGGER_.error("More unique forward links than produced!");
	}

	public class Engine implements InputProcessor<IndexedClassExpression>,
			RuleEngine {

		/**
		 * Local {@link ConclusionsCounter} created for every worker
		 */
		private final ConclusionsCounter conclusionsCounter_ = new ConclusionsCounter();
		/**
		 * Local {@link RulesTimer} created for every worker
		 */
		private final RulesTimer rulesTimer_ = new RulesTimer();
		/**
		 * Worker-local counter for the number of created contexts
		 */
		private int localContextNumber = 0;

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public void submit(IndexedClassExpression job) {
			getCreateContext(job);
		}

		@Override
		public void process() {
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					break;
				Context nextContext = activeContexts_.poll();
				if (nextContext == null)
					break;
				process(nextContext);
			}
		}

		@Override
		public void finish() {
			approximateContextNumber_.addAndGet(localContextNumber);
			localContextNumber = 0;
			aggregatedConclusionsCounter_.merge(conclusionsCounter_);
			aggregatedRulesTimer_.merge(rulesTimer_);
		}

		/**
		 * @return the {@code owl:Thing} object in this ontology
		 */
		@Override
		public IndexedClassExpression getOwlNothing() {
			return owlNothing_;
		}

		/**
		 * @return the {@code owl:Nothing} object in this ontology
		 */
		public IndexedClassExpression getOwlThing() {
			return owlThing_;
		}

		/**
		 * @return the reflexive object properties in this ontology
		 */
		public Set<IndexedObjectProperty> getReflexiveObjectProperties() {
			return reflexiveProperties_;
		}

		/**
		 * @return the object collecting statistics of rule applications
		 */
/*<<<<<<< .working
		@Override
		public RuleStatistics getRuleStatistics() {
			return this.statistics;
=======*/
		@Override
		public ConclusionsCounter getConclusionsCounter() {
			return this.conclusionsCounter_;
		}

		@Override
		public RulesTimer getRulesTimer() {
			return this.rulesTimer_;
//>>>>>>> .merge-right.r1141
		}

		@Override
		public Context getCreateContext(IndexedClassExpression root) {
			if (root.getContext() == null) {
				Context context = new ContextImpl(root);
				if (root.setContext(context)) {
					if (++localContextNumber == contextUpdateInterval_) {
						approximateContextNumber_.addAndGet(localContextNumber);
						localContextNumber = 0;
					}
					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace(root + ": context created");
					}
					initContext(context);
				}
			}
			return root.getContext();
		}

		/**
		 * Schedule the given item to be processed in the given context
		 * 
		 * @param context
		 *            the context in which the item should be processed
		 * @param item
		 *            the item to be processed in the given context
		 */
		@Override
		public void produce(Context context, Conclusion item) {
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace(context.getRoot() + ": new conclusion " + item);
			if (context.addToDo(item))
				// context was activated
				activeContexts_.add(context);
		}

		/**
		 * Process all scheduled items in the given context
		 * 
		 * @param context
		 *            the context in which to process the scheduled items
		 */
		protected void process(Context context) {
			for (;;) {
				Conclusion item = context.takeToDo();
				if (item == null)
					break;
				item.apply(this, context);
			}
			if (context.deactivate())
				// context was re-activated
				activeContexts_.add(context);
		}

		private void initContext(Context context) {
			produce(context,
					new PositiveSuperClassExpression(context.getRoot()));
			// TODO: register this as a ContextRule when owlThing occurs
			// negative and apply all such context initialization rules here
			IndexedClassExpression owlThing = getOwlThing();
			if (owlThing.occursNegatively())
				produce(context, new PositiveSuperClassExpression(owlThing));
		}

	}

}

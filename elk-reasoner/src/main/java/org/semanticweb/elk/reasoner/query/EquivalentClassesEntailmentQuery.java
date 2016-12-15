/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.entailments.impl.DerivedClassInclusionCycleEntailsEquivalentClassesAxiomImpl;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInferenceSet;
import org.semanticweb.elk.reasoner.entailments.model.EquivalentClassesAxiomEntailment;
import org.semanticweb.elk.reasoner.entailments.model.SubClassOfAxiomEntailment;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;

/**
 * Query whether an
 * {@link org.semanticweb.elk.owl.interfaces.ElkEquivalentClassesAxiom
 * ElkEquivalentClassesAxiom} is entailed.
 * 
 * @author Peter Skocovsky
 */
public class EquivalentClassesEntailmentQuery extends
		AbstractIndexedEntailmentQuery<EquivalentClassesAxiomEntailment> {

	/**
	 * {@link SubClassOfEntailmentQuery}-s over all the classes from
	 * {@link #equivalentClasses_}, such that superclass of one is the subclass
	 * of the next one and superclass of the last one is the subclass of the
	 * first one.
	 */
	private final List<SubClassOfEntailmentQuery> subsumptionCycle_;

	/**
	 * @param query
	 *            What entailment is queried.
	 * @param equivalentClasses
	 *            Indexed classes that should be equivalent to each other.
	 * @param elkFactory
	 *            Factory of {@link ElkObject}-s
	 */
	public EquivalentClassesEntailmentQuery(
			final EquivalentClassesAxiomEntailment query,
			final List<SubClassOfEntailmentQuery> subsumptionCycle) {
		super(query);
		this.subsumptionCycle_ = subsumptionCycle;
	}

	@Override
	public Collection<? extends IndexedContextRoot> getPositivelyIndexed() {

		final Collection<IndexedContextRoot> result = new ArrayList<IndexedContextRoot>(
				subsumptionCycle_.size());

		for (final SubClassOfEntailmentQuery subsumption : subsumptionCycle_) {
			result.addAll(subsumption.getPositivelyIndexed());
		}

		return result;
	}

	@Override
	public <C extends Context> EntailmentInferenceSet getEvidence(
			final boolean atMostOne, final SaturationState<C> saturationState,
			final SaturationConclusion.Factory conclusionFactory)
			throws ElkQueryException {

		final Map<Entailment, Collection<? extends EntailmentInference>> premiseEvidence = new ArrayHashMap<Entailment, Collection<? extends EntailmentInference>>();
		final List<SubClassOfAxiomEntailment> premises = new ArrayList<SubClassOfAxiomEntailment>();

		for (final SubClassOfEntailmentQuery subsumption : subsumptionCycle_) {
			final SubClassOfAxiomEntailment entailment = subsumption.getQuery();
			final Collection<? extends EntailmentInference> infs = subsumption
					.getEvidence(atMostOne, saturationState, conclusionFactory)
					.getInferences(entailment);
			if (infs == null || infs.isEmpty()) {
				// Not entailed!
				return new EntailmentInferenceSet() {

					@Override
					public Collection<? extends EntailmentInference> getInferences(
							final Entailment conclusion) {
						return Collections.emptySet();
					}

				};
			}
			premiseEvidence.put(entailment, infs);
			premises.add(entailment);
		}

		return new EntailmentInferenceSet() {

			@Override
			public Collection<? extends EntailmentInference> getInferences(
					final Entailment conclusion) {

				final Collection<? extends EntailmentInference> result = premiseEvidence
						.get(conclusion);
				if (result != null) {
					return result;
				}
				// else

				if (!getQuery().equals(conclusion)) {
					return Collections.emptyList();
				}
				// else

				return Collections.singleton(
						new DerivedClassInclusionCycleEntailsEquivalentClassesAxiomImpl(
								getQuery(), premises));
			}

		};
	}

}

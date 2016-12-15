/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.query;

import java.util.Arrays;

import org.junit.runner.RunWith;
import org.semanticweb.elk.owlapi.OwlApiIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

@RunWith(PolySuite.class)
public class OwlApiIncrementalClassExpressionInstancesQueryTest extends
		OwlApiIncrementalClassExpressionQueryTest<RelatedEntitiesTestOutput<OWLNamedIndividual>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			"Inconsistent.owl",// Throwing InconsistentOntologyException
			"InconsistentInstances.owl",// Throwing InconsistentOntologyException
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignoreInputFile(final String fileName) {
		return Arrays.binarySearch(IGNORE_LIST, fileName) >= 0;
	}

	public OwlApiIncrementalClassExpressionInstancesQueryTest(
			final TestManifest<QueryTestInput<OWLClassExpression>> manifest) {
		super(manifest,
				new OwlApiIncrementalReasoningTestDelegate<RelatedEntitiesTestOutput<OWLNamedIndividual>, RelatedEntitiesTestOutput<OWLNamedIndividual>>(
						manifest) {

					@Override
					public RelatedEntitiesTestOutput<OWLNamedIndividual> getExpectedOutput()
							throws Exception {
						final NodeSet<OWLNamedIndividual> subNodes = getStandardReasoner()
								.getInstances(manifest.getInput().getQuery(),
										true);
						return new OwlApiRelatedEntitiesTestOutput<OWLNamedIndividual>(
								subNodes);
					}

					@Override
					public RelatedEntitiesTestOutput<OWLNamedIndividual> getActualOutput()
							throws Exception {
						final NodeSet<OWLNamedIndividual> subNodes = getIncrementalReasoner()
								.getInstances(manifest.getInput().getQuery(),
										true);
						return new OwlApiRelatedEntitiesTestOutput<OWLNamedIndividual>(
								subNodes);
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ReasonerInterruptedException.class;
					}

				});
	}

}

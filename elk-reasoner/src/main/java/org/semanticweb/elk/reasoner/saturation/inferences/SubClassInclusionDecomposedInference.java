package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
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

public interface SubClassInclusionDecomposedInference
		extends
			SubClassInclusionInference {

	public <O> O accept(Visitor<O> visitor);

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O>
			extends
				SubClassInclusionDecomposedFirstConjunct.Visitor<O>,
				SubClassInclusionDecomposedSecondConjunct.Visitor<O>,
				SubClassInclusionExpandedDefinition.Visitor<O>,
				SubClassInclusionExpandedSubClassOf.Visitor<O>,
				SubClassInclusionObjectHasSelfPropertyRange.Visitor<O>,
				SubClassInclusionOwlThing.Visitor<O>,
				SubClassInclusionRange.Visitor<O>,
				SubClassInclusionTautology.Visitor<O> {

		// combined interface

	}

}

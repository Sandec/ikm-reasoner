/*
 * This product is dual-licensed under Apache 2.0 License for two organizations due to forking.
 *
 * Copyright © 2023 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ======================================================================
 *
 * Copyright © 2011 - 2023 Department of Computer Science, University of Oxford
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.semanticweb.elk.owl.inferences;



import java.util.Collections;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *  
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  ObjectUnionOf() ⊑ ⊥
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionEmptyObjectUnionOfOwlNothing
		extends AbstractElkInference {

	public final static String NAME = "Empty Disjunction";

	ElkClassInclusionEmptyObjectUnionOfOwlNothing() {
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getPremiseCount() {
		return 0;
	}

	@Override
	public ElkSubClassOfAxiom getPremise(int index, ElkObject.Factory factory) {
		return failGetPremise(index);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(
				factory.getObjectUnionOf(
						Collections.<ElkClassExpression> emptyList()),
				factory.getOwlNothing());
	}

	@Override
	public <O> O accept(ElkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkClassInclusionEmptyObjectUnionOfOwlNothing getElkClassInclusionEmptyObjectUnionOfOwlNothing();

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ElkClassInclusionEmptyObjectUnionOfOwlNothing inference);

	}

}

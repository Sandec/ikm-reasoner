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



import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Represents the inference:
 * 
 * <pre>
 *  DisjointUnion(D C0 C1 ... Cn)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  DisjointClasses(C0 C1 ... Cn)
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkDisjointClassesOfDisjointUnion extends AbstractElkInference {

	public final static String NAME = "Disjoint Union to Disjoint Classes";

	private final ElkClass defined_;

	private final List<? extends ElkClassExpression> disjoint_;

	ElkDisjointClassesOfDisjointUnion(ElkClass defined,
			List<? extends ElkClassExpression> disjoint) {
		this.defined_ = defined;
		this.disjoint_ = disjoint;
	}

	public ElkClass getDefined() {
		return defined_;
	}

	public List<? extends ElkClassExpression> getDisjoint() {
		return disjoint_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public ElkAxiom getPremise(int index, ElkObject.Factory factory) {
		if (index == 0) {
			return getPremise(factory);
		}
		// else
		return failGetPremise(index);
	}

	public ElkDisjointUnionAxiom getPremise(ElkObject.Factory factory) {
		return factory.getDisjointUnionAxiom(defined_, disjoint_);
	}

	@Override
	public ElkDisjointClassesAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getDisjointClassesAxiom(disjoint_);
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

		ElkDisjointClassesOfDisjointUnion getElkDisjointClassesOfDisjointUnion(
				ElkClass defined, List<? extends ElkClassExpression> disjoint);

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

		O visit(ElkDisjointClassesOfDisjointUnion inference);

	}

}

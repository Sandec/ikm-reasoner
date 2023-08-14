/*
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
 */
package org.semanticweb.elk.reasoner;



import org.semanticweb.elk.owl.implementation.ElkObjectBaseFactory;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.testing.DiffableOutput.Listener;

public class ElkObjectPropertyTaxonomyEntailmentAdapter
		implements TaxonomyEntailment.Listener<ElkObjectProperty> {

	final static ElkAxiom.Factory ELK_FACTORY = new ElkObjectBaseFactory();

	private final Listener<ElkAxiom> listener_;

	public ElkObjectPropertyTaxonomyEntailmentAdapter(
			Listener<ElkAxiom> listener) {
		this.listener_ = listener;
	}

	Listener<ElkAxiom> getListener() {
		return listener_;
	}

	@Override
	public void reportMissingSubsumption(ElkObjectProperty sub,
			ElkObjectProperty sup) {
		listener_.missing(ELK_FACTORY.getSubObjectPropertyOfAxiom(sub, sup));
	}

	@Override
	public void reportMissingEquivalence(ElkObjectProperty first,
			ElkObjectProperty second) {
		listener_.missing(
				ELK_FACTORY.getEquivalentObjectPropertiesAxiom(first, second));
	}

	@Override
	public void reportMissingEntity(ElkObjectProperty entity) {
		listener_.missing(ELK_FACTORY.getDeclarationAxiom(entity));
	}

}

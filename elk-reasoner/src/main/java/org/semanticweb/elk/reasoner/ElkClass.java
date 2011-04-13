/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, Apr 8, 2011
 */
package org.semanticweb.elk.reasoner;


/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Classes">Class<a> in the OWL 2
 * specification.
 * 
 * @author Yevgeny Kazakov
 */
public class ElkClass extends ElkClassExpression {

	private final String classIri_;

	private ElkClass(String classIri) {
		this.classIri_ = classIri;
	}

	/**
	 * Get the IRI of this class.
	 * 
	 * @return The IRI of this class.
	 */
	public String getIri() {
		return classIri_;
	}

	public boolean equals(ElkClass elkClass) {
		return elkClass.getIri().equals(classIri_);
	}

	static ElkClass create(String classIri) {
		return (ElkClass) intern(new ElkClass(classIri));		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.ELKClassExpression#accept(org.semanticweb
	 * .elk.reasoner.ELKClassExpressionVisitor)
	 */
	@Override
	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {

		return visitor.visit(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#hashCode()
	 */
	@Override
	public int hashCode() {
		return classIri_.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.semanticweb.elk.reasoner.ElkObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != getClass())
			return false;
		return equals((ElkClass) obj);
	}

}

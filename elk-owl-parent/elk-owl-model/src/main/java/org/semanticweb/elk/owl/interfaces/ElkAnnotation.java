/*
 * #%L
 * ELK OWL Object Interfaces
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
 * 
 */
package org.semanticweb.elk.owl.interfaces;

/**
 * The basic interface for annotation objects in OWL 2 as defined in <a
 * href="http://www.w3.org/TR/owl2-syntax/#Annotations">Section 10</a> of the
 * specification
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 */

/*
 * TODO: Annotations are not included in our model. We should extend the model
 * so that every ontology, axiom, and annotation can link to a list of
 * annotations.
 */

public interface ElkAnnotation extends ElkObject {

	public ElkAnnotationProperty getProperty();

	public ElkAnnotationValue getValue();
}
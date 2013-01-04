/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.Collections;

/**
 * An extension of the base interface to specify stages to be executed after
 * processing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface PostProcessingReasonerStage extends ReasonerStage {

	public Collection<ReasonerStage> getPostProcessingStages();
}

/**
 * An empty base class for stages used for post processing
 * 
 */
abstract class BaseReasonerStage implements ReasonerStage {

	@Override
	public boolean done() {
		return false;
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public boolean isInterrupted() {
		return false;
	}

	@Override
	public void clearInterrupt() {
	}

	@Override
	public void printInfo() {
	}	
}
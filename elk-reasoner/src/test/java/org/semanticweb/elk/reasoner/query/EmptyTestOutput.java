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
package org.semanticweb.elk.reasoner.query;



import org.semanticweb.elk.testing.DiffableOutput;

/**
 * A {@link DiffableOutput} that does not contain any test results
 * 
 * @author Yevgeny Kazakov
 *
 */
public class EmptyTestOutput implements DiffableOutput<Void, EmptyTestOutput> {

	@Override
	public boolean containsAllElementsOf(EmptyTestOutput other) {
		return true;
	}

	@Override
	public void reportMissingElementsOf(EmptyTestOutput other,
			Listener<Void> listener) {
		// nothing to report
	}

}
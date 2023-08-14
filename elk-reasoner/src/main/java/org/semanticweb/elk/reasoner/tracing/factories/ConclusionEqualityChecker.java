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
package org.semanticweb.elk.reasoner.tracing.factories;



import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.DummyConclusionVisitor;

/**
 * A convenience class for checking equality of several {@link Conclusion}s to a
 * given {@link Conclusion}.
 * 
 * @author Yevgeny Kazakov
 */
class ConclusionEqualityChecker extends DummyConclusionVisitor<Void> {

	private final Conclusion conclusion_;

	/**
	 * {@code true} if some of the visited conclusions are equal to
	 * {@link #conclusion_}
	 */
	private boolean result_ = false;

	ConclusionEqualityChecker(Conclusion conclusion) {
		this.conclusion_ = conclusion;
	}

	public boolean getResult() {
		return result_;
	}

	@Override
	protected Void defaultVisit(Conclusion candidate) {
		result_ |= conclusion_.equals(candidate);
		return null;
	}

}
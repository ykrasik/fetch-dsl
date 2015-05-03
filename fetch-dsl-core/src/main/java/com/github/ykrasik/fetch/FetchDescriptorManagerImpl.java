/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.fetch;

import com.github.ykrasik.fetch.node.FetchDescriptor;
import com.github.ykrasik.fetch.node.FetchNode;

import java.util.Objects;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class FetchDescriptorManagerImpl implements FetchDescriptorManager {
    private final DescriptorRepository repository;

    public FetchDescriptorManagerImpl(DescriptorRepository repository) {
        // Don't want anyone changing our repository after we start using it.
        this.repository = Objects.requireNonNull(repository, "DescriptorRepository is null!").clone();

        // There must not be any unresolved references to descriptors.
        resolveReferences();
    }

    private void resolveReferences() {
        for (FetchDescriptor fetchDescriptor : repository.getAllDescriptors()) {
            resolveFetchNode(fetchDescriptor);
        }
    }

    private void resolveFetchNode(FetchNode fetchNode) {
        // Simply trying to access all children of a fetchDescriptor is enough to cause it to be resolved.
        for (FetchNode node : fetchNode.getChildren()) {
            resolveFetchNode(node);
        }
    }

    @Override
    public FetchDescriptor getFetchDescriptor(String id) {
        return repository.getDescriptor(id);
    }
}

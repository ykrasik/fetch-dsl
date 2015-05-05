/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *
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

package com.github.ykrasik.fetch

import com.github.ykrasik.fetch.node.FetchDescriptor
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Entry point for creating a single FetchDescriptor.
 *
 * @author Yevgeny Krasik
 */
@CompileStatic
@TypeChecked
class DescriptorFactory extends AbstractFactory {
    private final DescriptorRepository repository;

    DescriptorFactory(DescriptorRepository repository) {
        this.repository = repository;
    }

    @Override
    boolean isHandlesNodeChildren() {
        return true
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new DescriptorBuilder((String) value, repository);
    }

    @Override
    boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        ((DescriptorBuilder) node).process(childContent)

        // Stop the factorySupport from processing this closure.
        return false
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        final FetchDescriptor fetchDescriptor = (FetchDescriptor) ((DescriptorBuilder) node).build();
        repository.addFetchDescriptor(fetchDescriptor);
    }
}

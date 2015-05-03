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
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@CompileStatic
@TypeChecked
class DescriptorRepository {
    private final Map<String, FetchDescriptor> descriptors

    DescriptorRepository() {
        this([:])
    }

    private DescriptorRepository(Map<String, FetchDescriptor> descriptors) {
        this.descriptors = descriptors
    }

    void addFetchDescriptor(FetchDescriptor descriptor) {
        if (descriptors.containsKey(descriptor.id)) {
            throw new IllegalArgumentException("FetchDescriptor is already defined: '${descriptor.id}'")
        }
        descriptors.put(descriptor.id, descriptor)
    }

    FetchDescriptor getDescriptor(String id) {
        final FetchDescriptor descriptor = descriptors.get(id)
        if (descriptor == null) {
            throw new IllegalArgumentException("FetchDescriptor not found: '$id'")
        }
        return descriptor
    }

    Iterable<FetchDescriptor> getAllDescriptors() {
        Collections.unmodifiableCollection(descriptors.values())
    }

    DescriptorRepository clone() {
        new DescriptorRepository(new HashMap<String, FetchDescriptor>(descriptors))
    }

    @Override
    public String toString() {
        return descriptors.toString()
    }
}

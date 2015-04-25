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

package com.github.ykrasik.fetch
import com.github.ykrasik.fetch.dsl.FetchDslDialect
import com.github.ykrasik.fetch.node.FetchDescriptorImpl
import com.github.ykrasik.fetch.node.FetchNode
import com.github.ykrasik.fetch.node.LazyResolveFetchDescriptorReference
/**
 * @author Yevgeny Krasik
 */
class FetchBuilder {
    private final FactoryBuilderSupport builder
    private final FetchDescriptorManager manager

    private final String column
    private Closure descriptorClosure
    private String descriptorReference

    FetchBuilder(String column, FactoryBuilderSupport builder, FetchDescriptorManager manager) {
        this.column = column
        this.builder = builder
        this.manager = manager
    }

    String getColumn() {
        return column
    }

    void descriptor(Closure descriptorClosure) {
        this.descriptorClosure = descriptorClosure
    }

    void descriptor(String descriptorReference) {
        if (!descriptorReference.startsWith('#')) {
            throw new IllegalArgumentException("FetchDescriptorReferences must start with a '#'! Invalid fetchDescriptorRef: $descriptorReference")
        }

        // Skip the initial '#' in the reference name
        this.descriptorReference = descriptorReference.substring(1)
    }

    FetchNode build() {
        if (descriptorClosure != null) {
            final FetchDescriptorBuilder fetchDescriptorBuilder = builder."$FetchDslDialect.INLINE_FETCH_DESCRIPTOR"(column, descriptorClosure)
            return fetchDescriptorBuilder.build()
        }

        if (descriptorReference != null) {
            // Skip the initial '#' in the reference name
            return new LazyResolveFetchDescriptorReference(descriptorReference, column, manager)
        }

        return new FetchDescriptorImpl(column, [])
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        final FetchBuilder that = (FetchBuilder) o

        if (column != that.column) return false

        return true
    }

    @Override
    int hashCode() {
        return column.hashCode()
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FetchBuilder{");
        sb.append("column='").append(column).append('\'');
        sb.append(", descriptorClosure=").append(descriptorClosure);
        sb.append(", descriptorReference='").append(descriptorReference).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

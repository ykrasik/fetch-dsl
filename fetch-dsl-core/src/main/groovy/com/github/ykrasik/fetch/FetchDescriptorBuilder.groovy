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

import com.github.ykrasik.fetch.node.FetchDescriptor
import com.github.ykrasik.fetch.node.FetchDescriptorImpl
import com.github.ykrasik.fetch.node.FetchNode
/**
 * @author Yevgeny Krasik
 */
class FetchDescriptorBuilder {
    private final String id;
    private final List<FetchBuilder> children

    FetchDescriptorBuilder(String id) {
        this.id = id
        this.children = []
    }

    void addChild(FetchBuilder fetchBuilder) {
        if (children.contains(fetchBuilder)) {
            throw new IllegalArgumentException("FetchDescriptor '$id' already contains a child for column '${fetchBuilder.column}'!")
        }
        children.add(fetchBuilder)
    }

    FetchDescriptor build() {
        final List<FetchNode> builtChildren = children*.build()
        new FetchDescriptorImpl(id, builtChildren)
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FetchDescriptorBuilder{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

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

package com.github.ykrasik.fetch.node;

import java.util.Collections;
import java.util.List;

/**
 * @author Yevgeny Krasik
 */
public class FetchDescriptorImpl implements FetchDescriptor {
    private final String id;
    private final List<FetchNode> children;

    public FetchDescriptorImpl(String id, List<FetchNode> children) {
        this.id = id;
        this.children = Collections.unmodifiableList(children);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getColumn() {
        return id;
    }

    @Override
    public List<FetchNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FetchDescriptorImpl{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

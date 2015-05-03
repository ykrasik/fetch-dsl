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
// TODO: JavaDoc
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FetchDescriptorImpl that = (FetchDescriptorImpl) o;

        if (!id.equals(that.id)) {
            return false;
        }
        return children.equals(that.children);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + children.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FetchDescriptorImpl{id = '" + id + "'}";
    }
}

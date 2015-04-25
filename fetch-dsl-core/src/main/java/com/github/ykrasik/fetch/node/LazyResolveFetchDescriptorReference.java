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

import com.github.ykrasik.fetch.FetchDescriptorManager;

import java.util.List;

/**
 * @author Yevgeny Krasik
 */
public class LazyResolveFetchDescriptorReference implements FetchNode {
    private final String id;
    private final String column;
    private final FetchDescriptorManager manager;

    private volatile FetchDescriptor fetchDescriptor;

    public LazyResolveFetchDescriptorReference(String id, String column, FetchDescriptorManager manager) {
        this.id = id;
        this.column = column;
        this.manager = manager;
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public List<FetchNode> getChildren() {
        return getFetchDescriptor().getChildren();
    }

    private FetchDescriptor getFetchDescriptor() {
        // Double locking
        if (fetchDescriptor == null) {
            synchronized (this) {
                if (fetchDescriptor == null) {
                    fetchDescriptor = manager.getFetchDescriptorById(id);
                    if (fetchDescriptor == null) {
                        throw new IllegalArgumentException("Invalid fetchDescriptorId: " + id);
                    }
                }
            }
        }
        return fetchDescriptor;
    }
}

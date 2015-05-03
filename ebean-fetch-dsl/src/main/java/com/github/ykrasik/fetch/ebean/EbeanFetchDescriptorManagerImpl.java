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

package com.github.ykrasik.fetch.ebean;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import com.github.ykrasik.fetch.FetchDescriptorManager;
import com.github.ykrasik.fetch.node.FetchDescriptor;
import com.github.ykrasik.fetch.node.FetchNode;

import java.util.List;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class EbeanFetchDescriptorManagerImpl implements EbeanFetchDescriptorManager {
    private final FetchDescriptorManager manager;
    private final int fetchSize;

    public EbeanFetchDescriptorManagerImpl(FetchDescriptorManager manager, int fetchSize) {
        this.manager = manager;
        this.fetchSize = fetchSize;
    }

    @Override
    public <T> void apply(Query<T> query, String fetchDescriptorId) {
        final FetchDescriptor fetchDescriptor = manager.getFetchDescriptor(fetchDescriptorId);
        doApply(query, fetchDescriptor, null);
    }

    private <T> void doApply(Query<T> query, FetchNode fetchNode, String fetchPath) {
        final List<FetchNode> children = fetchNode.getChildren();
        if (children.isEmpty()) {
            // Node has no children, parent should have taken care of everything.
            return;
        }

        final String properties = join(children);
        final FetchConfig fetchConfig = new FetchConfig().query(fetchSize);
        query.fetch(fetchPath, properties, fetchConfig);

        // Apply to all children
        for (FetchNode child : children) {
            final String column = child.getColumn();
            final String newFetchPath = fetchPath != null ? fetchPath + '.' + column : column;
            doApply(query, child, newFetchPath);
        }
    }

    private String join(List<FetchNode> fetchNodes) {
        final StringBuilder sb = new StringBuilder();
        for (FetchNode fetchNode : fetchNodes) {
            sb.append(fetchNode.getColumn());
            sb.append(',');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}

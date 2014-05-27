package com.avaje.ebean.fetch;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import fetch.FetchDescriptorManager;
import fetch.node.FetchDescriptor;
import fetch.node.FetchNode;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class EbeanFetchDescriptorManagerImpl implements EbeanFetchDescriptorManager {
    private final FetchDescriptorManager manager;
    private final int fetchSize;

    public EbeanFetchDescriptorManagerImpl(FetchDescriptorManager manager, int fetchSize) {
        this.manager = manager;
        this.fetchSize = fetchSize;
    }

    @Override
    public <T> void apply(Query<T> query, String fetchDescriptorId) {
        final FetchDescriptor fetchDescriptor = manager.getFetchDescriptorById(fetchDescriptorId);
        if (fetchDescriptor == null) {
            // TODO: Throw a better exception
            throw new RuntimeException("Invalid fetchDescriptorId: " + fetchDescriptorId);
        }

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

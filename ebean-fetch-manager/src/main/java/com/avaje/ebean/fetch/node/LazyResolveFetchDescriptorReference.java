package com.avaje.ebean.fetch.node;

import com.avaje.ebean.fetch.FetchDescriptorManager;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class LazyResolveFetchDescriptorReference implements FetchNode {
    private final String id;
    private final String column;
    private final FetchDescriptorManager manager;

    // TODO: Eagerly resolve all descriptors on construction to avoid this
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
                        // TODO: Throw a different exception
                        throw new RuntimeException("Invalid fetchDescriptorId: " + id);
                    }
                }
            }
        }
        return fetchDescriptor;
    }
}

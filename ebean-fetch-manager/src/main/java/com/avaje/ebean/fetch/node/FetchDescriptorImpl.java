package com.avaje.ebean.fetch.node;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class FetchDescriptorImpl implements FetchDescriptor {
    private final String id;
    private final List<FetchNode> children;

    public FetchDescriptorImpl(String id) {
        this.id = id;
        this.children = new ArrayList<>();
    }

    public void addChild(FetchNode fetchNode) {
        children.add(fetchNode);
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

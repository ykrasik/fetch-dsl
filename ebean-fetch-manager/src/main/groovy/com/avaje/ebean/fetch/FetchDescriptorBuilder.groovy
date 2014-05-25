package com.avaje.ebean.fetch

import com.avaje.ebean.fetch.node.FetchDescriptor
import com.avaje.ebean.fetch.node.FetchDescriptorImpl
import com.avaje.ebean.fetch.node.FetchNode

/**
 * User: ykrasik
 * Date: 25/05/14
 */
class FetchDescriptorBuilder {
    private final String id;
    private final List<FetchBuilder> children

    FetchDescriptorBuilder(String id) {
        this.id = id
        this.children = []
    }

    void addChild(FetchBuilder fetchBuilder) {
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

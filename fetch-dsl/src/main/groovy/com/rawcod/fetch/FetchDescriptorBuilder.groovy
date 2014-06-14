package com.rawcod.fetch

import com.rawcod.fetch.exception.DslException
import com.rawcod.fetch.node.FetchDescriptor
import com.rawcod.fetch.node.FetchDescriptorImpl
import com.rawcod.fetch.node.FetchNode
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
        if (children.contains(fetchBuilder)) {
            throw new DslException("FetchDescriptor '$id' already contains a child for column '$fetchBuilder.column'!")
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

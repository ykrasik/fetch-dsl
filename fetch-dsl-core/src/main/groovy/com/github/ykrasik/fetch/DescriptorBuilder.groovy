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

import com.github.ykrasik.fetch.node.FetchDescriptorImpl
import com.github.ykrasik.fetch.node.FetchNode
import com.github.ykrasik.fetch.node.FetchDescriptorRef
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * @author Yevgeny Krasik
 */
@CompileStatic
@TypeChecked
class DescriptorBuilder extends BuilderSupport {
    private final String id;
    private final DescriptorRepository repository

    private final List<DescriptorBuilder> children = []
    private String descriptorRef

    DescriptorBuilder(String id, DescriptorRepository repository) {
        this.id = id
        this.repository = repository
    }

    void process(Closure closure) {
        setClosureDelegate(closure, this)
        closure.call()
    }

    @Override
    protected void setClosureDelegate(Closure closure, Object node) {
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.delegate = node
    }

    @Override
    protected Object createNode(Object name) {
        newBuilder(name)
    }

    Object propertyMissing(String name) {
        final DescriptorBuilder builder = newBuilder(name)
        addChild(builder)
        // Suppress the PropertyNotFound exception.
        builder
    }

    @Override
    protected void setParent(Object parent, Object child) {
        super.setParent(parent, child)
    }

    @Override
    protected Object createNode(Object name, Object rawValue) {
        if (!rawValue instanceof String) {
            throw new UnsupportedOperationException(
                """Only the following are legal after a column name:
                     1. A block {} with more column names.
                     2. A single column name, surrounded by single or double quotes. Can also be '*' (quotes necessary)."""
            )
        }

        final DescriptorBuilder builder = newBuilder(name)

        final String value = (String) rawValue
        if (value.startsWith('#')) {
            // Reference to another descriptor.
            builder.descriptorRef = value.substring(1)
        } else {
            // Single column name.
            builder.addChild(newBuilder(value))
        }
        builder
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        throw new UnsupportedOperationException("Cannot pass attributes to column: name=$name, attributes=$attributes")
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        throw new UnsupportedOperationException("Cannot pass attributes to column: name=$name, attributes=$attributes, value=$value")
    }

    @Override
    protected void nodeCompleted(Object parent, Object node) {
        addChild((DescriptorBuilder) node)
    }

    private void addChild(DescriptorBuilder child) {
        if (children.contains(child)) {
            throw new IllegalArgumentException("FetchDescriptor '$id' already contains a child for column '${child.id}'!")
        }
        children.add(child)
    }

    private DescriptorBuilder newBuilder(Object name) {
        new DescriptorBuilder(removeUnderscore((String) name), repository)
    }

    private String removeUnderscore(String name) {
        return name.startsWith("_") ? name.substring(1) : name
    }

    FetchNode build() {
        if (descriptorRef == null) {
            buildSimpleFetchDescriptor()
        } else {
            buildFetchDescriptorReference()
        }
    }

    private FetchNode buildSimpleFetchDescriptor() {
        final List<FetchNode> builtChildren = children*.build()
        new FetchDescriptorImpl(id, builtChildren)
    }

    private FetchNode buildFetchDescriptorReference() {
        new FetchDescriptorRef(id, descriptorRef, repository)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        final DescriptorBuilder that = (DescriptorBuilder) o

        if (id != that.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

    @Override
    public String toString() {
        return "DescriptorBuilder{id = '$id'}"
    }
}

package com.avaje.ebean.fetch
import com.avaje.ebean.fetch.dsl.EbeanFetchDslDialect
import com.avaje.ebean.fetch.node.ColumnFetchNode
import com.avaje.ebean.fetch.node.FetchNode
import com.avaje.ebean.fetch.node.LazyResolveFetchDescriptorReference

/**
 * User: ykrasik
 * Date: 25/05/14
 */
class FetchBuilder {
    private final FactoryBuilderSupport builder
    private final FetchDescriptorManager manager

    private final String column
    private Closure descriptorClosure
    private String descriptorReference

    FetchBuilder(String column, FactoryBuilderSupport builder, FetchDescriptorManager manager) {
        this.column = column
        this.builder = builder
        this.manager = manager
    }

    void descriptor(Closure descriptorClosure) {
        this.descriptorClosure = descriptorClosure
    }

    void descriptor(String descriptorReference) {
        if (!descriptorReference.startsWith('#')) {
            throw new RuntimeException("FetchDescriptorReferences must start with a '#'! Invalid fetchDescriptorRef: " + descriptorReference)
        }

        // Skip the initial '#' in the reference name
        this.descriptorReference = descriptorReference.substring(1)
    }

    FetchNode build() {
        if (descriptorClosure != null) {
            final FetchDescriptorBuilder fetchDescriptorBuilder = builder."$EbeanFetchDslDialect.INLINE_FETCH_DESCRIPTOR"(column, descriptorClosure)
            return fetchDescriptorBuilder.build()
        }

        if (descriptorReference != null) {
            // Skip the initial '#' in the reference name
            return new LazyResolveFetchDescriptorReference(descriptorReference, column, manager)
        }

        return new ColumnFetchNode(column)
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FetchBuilder{");
        sb.append("column='").append(column).append('\'');
        sb.append(", descriptorClosure=").append(descriptorClosure);
        sb.append(", descriptorReference='").append(descriptorReference).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

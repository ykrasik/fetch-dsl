package com.avaje.ebean.fetch
import com.avaje.ebean.fetch.dsl.EbeanFetchDslDialect
import com.avaje.ebean.fetch.node.ColumnFetchNode
import com.avaje.ebean.fetch.node.FetchNode
import com.avaje.ebean.fetch.node.LazyResolveFetchDescriptorReference
/**
 * User: Yevgeny
 * Date: 18/05/14
 * Time: 22:02
 */
class FetchBuilder {
    private final FactoryBuilderSupport builder
    private final FetchDescriptorManager manager

    private final String col
    private Closure descriptorClosure
    private String descriptorReference

    FetchBuilder(String col, FactoryBuilderSupport builder, FetchDescriptorManager manager) {
        this.col = col
        this.builder = builder
        this.manager = manager
    }

    void descriptor(Closure descriptorClosure) {
        this.descriptorClosure = descriptorClosure
    }

    void descriptor(String descriptorReference) {
        this.descriptorReference = descriptorReference
    }

    FetchNode build() {
        if (descriptorClosure != null) {
            final FetchDescriptorBuilder fetchDescriptorBuilder = builder."$EbeanFetchDslDialect.INLINE_FETCH_DESCRIPTOR"(col, descriptorClosure)
            return fetchDescriptorBuilder.build()
        }

        if (descriptorReference != null) {
            // Skip the initial '#' in the reference name
            final String referenceId = descriptorReference.substring(1)
            return new LazyResolveFetchDescriptorReference(referenceId, col, manager)
        }

        return new ColumnFetchNode(col)
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FetchBuilder{");
        sb.append("col='").append(col).append('\'');
        sb.append(", descriptorClosure=").append(descriptorClosure);
        sb.append(", descriptorReference='").append(descriptorReference).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

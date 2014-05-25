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

    private String col
    private Closure descriptorClosure
    private String descriptorReference

    FetchBuilder(String col, FactoryBuilderSupport builder, FetchDescriptorManager manager) {
        this.col = col
        this.builder = builder
        this.manager = manager
    }

    void setCol(String col) {
        this.col = col
    }

    void setDescriptor(Object descriptor) {
        if (descriptor instanceof Closure) {
            this.descriptorClosure = descriptor as Closure;
        } else if (descriptor instanceof String && descriptor.startsWith('#')) {
            this.descriptorReference = descriptor as String
        } else {
            throw new RuntimeException("Invalid descriptor: " + descriptor)
        }
    }

    FetchNode build() {
        if (descriptorClosure != null) {
            final FetchNode inlineFetchDescriptor = builder."$EbeanFetchDslDialect.INLINE_FETCH_DESCRIPTOR"(col, descriptorClosure)
            return inlineFetchDescriptor
//            return new PrefixAppenderFetchNode(col, inlineFetchDescriptor)
        }

        if (descriptorReference != null) {
            // Skip the initial '#' in the reference name
            final String referenceId = descriptorReference.substring(1)
            final FetchNode lazyFetchDescriptorReference = new LazyResolveFetchDescriptorReference(referenceId, col, manager)
            return lazyFetchDescriptorReference
//            return new PrefixAppenderFetchNode(col, lazyFetchDescriptorReference)
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

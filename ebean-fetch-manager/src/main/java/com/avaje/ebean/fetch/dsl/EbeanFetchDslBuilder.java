package com.avaje.ebean.fetch.dsl;

import com.avaje.ebean.fetch.FetchDescriptorManagerImpl;
import groovy.util.FactoryBuilderSupport;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class EbeanFetchDslBuilder extends FactoryBuilderSupport {
    private final FetchDescriptorManagerImpl manager;

    public EbeanFetchDslBuilder(FetchDescriptorManagerImpl manager) {
        super(false);
        this.manager = manager;
        registerFactories();
    }

    private void registerFactories() {
        registerFactory(EbeanFetchDslDialect.FETCH_DESCRIPTOR, new FetchDescriptorClauseFactory(manager));
        registerFactory(EbeanFetchDslDialect.INLINE_FETCH_DESCRIPTOR, new InlineFetchDescriptorClauseFactory(manager));
        registerFactory(EbeanFetchDslDialect.FETCH, new FetchClauseFactory(manager));
    }
}
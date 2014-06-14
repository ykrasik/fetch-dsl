package com.rawcod.fetch.dsl;

import com.rawcod.fetch.FetchDescriptorManagerImpl;
import groovy.util.FactoryBuilderSupport;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class FetchDslBuilder extends FactoryBuilderSupport {
    private final FetchDescriptorManagerImpl manager;

    public FetchDslBuilder(FetchDescriptorManagerImpl manager) {
        super(false);
        this.manager = manager;
        registerFactories();
    }

    private void registerFactories() {
        registerFactory(FetchDslDialect.FETCH_DESCRIPTOR, new FetchDescriptorClauseFactory(manager));
        registerFactory(FetchDslDialect.INLINE_FETCH_DESCRIPTOR, new InlineFetchDescriptorClauseFactory(manager));
        registerFactory(FetchDslDialect.FETCH, new FetchClauseFactory(manager));
    }
}
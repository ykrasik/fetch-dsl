package com.rawcod.fetch.dsl;

import com.rawcod.fetch.FetchDescriptorBuilder;
import com.rawcod.fetch.FetchDescriptorManagerImpl;
import com.rawcod.fetch.node.FetchDescriptor;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

import java.util.Map;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
@SuppressWarnings("rawtypes")
public class FetchDescriptorClauseFactory extends AbstractFactory {
    private final FetchDescriptorManagerImpl manager;

    public FetchDescriptorClauseFactory(FetchDescriptorManagerImpl manager) {
        this.manager = manager;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new FetchDescriptorBuilder((String) value);
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        final FetchDescriptor fetchDescriptor = ((FetchDescriptorBuilder) node).build();
        manager.addFetchDescriptor(fetchDescriptor);
    }
}

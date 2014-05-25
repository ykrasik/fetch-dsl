package com.avaje.ebean.fetch.dsl;

import com.avaje.ebean.fetch.FetchBuilder;
import com.avaje.ebean.fetch.FetchDescriptorManager;
import com.avaje.ebean.fetch.node.FetchDescriptorImpl;
import com.avaje.ebean.fetch.node.FetchNode;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

import java.util.Map;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
@SuppressWarnings({"rawtypes", "CastToConcreteClass"})
public class FetchClauseFactory extends AbstractFactory {
    private final FetchDescriptorManager manager;

    public FetchClauseFactory(FetchDescriptorManager manager) {
        this.manager = manager;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new FetchBuilder((String) value, builder, manager);
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        final FetchNode fetchNode = ((FetchBuilder) node).build();
        ((FetchDescriptorImpl) parent).addChild(fetchNode);
    }

//    @Override
//    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
//        ((FetchBuilder) node).setParent(((FetchDescriptorImpl) parent));
//    }


}

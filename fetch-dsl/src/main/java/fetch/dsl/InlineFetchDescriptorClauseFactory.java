package fetch.dsl;

import fetch.FetchDescriptorManagerImpl;
import groovy.util.FactoryBuilderSupport;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class InlineFetchDescriptorClauseFactory extends FetchDescriptorClauseFactory {
    public InlineFetchDescriptorClauseFactory(FetchDescriptorManagerImpl manager) {
        super(manager);
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        // Do not add descriptor to manager like super does.
    }
}

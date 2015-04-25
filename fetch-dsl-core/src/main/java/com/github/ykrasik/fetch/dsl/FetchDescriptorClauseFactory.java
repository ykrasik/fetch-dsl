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

package com.github.ykrasik.fetch.dsl;

import com.github.ykrasik.fetch.FetchDescriptorBuilder;
import com.github.ykrasik.fetch.FetchDescriptorManagerImpl;
import com.github.ykrasik.fetch.node.FetchDescriptor;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

import java.util.Map;

/**
 * @author Yevgeny Krasik
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

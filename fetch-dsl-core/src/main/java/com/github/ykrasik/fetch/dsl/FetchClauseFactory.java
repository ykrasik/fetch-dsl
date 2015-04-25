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

import com.github.ykrasik.fetch.FetchBuilder;
import com.github.ykrasik.fetch.FetchDescriptorBuilder;
import com.github.ykrasik.fetch.FetchDescriptorManager;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;

import java.util.Map;

/**
 * @author Yevgeny Krasik
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
    public void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        ((FetchDescriptorBuilder) parent).addChild(((FetchBuilder) child));
    }
}

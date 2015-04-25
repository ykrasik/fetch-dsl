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

import com.github.ykrasik.fetch.FetchDescriptorManagerImpl;
import groovy.util.FactoryBuilderSupport;

/**
 * @author Yevgeny Krasik
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
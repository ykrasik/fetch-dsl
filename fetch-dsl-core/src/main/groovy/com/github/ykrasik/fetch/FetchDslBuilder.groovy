/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *
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

package com.github.ykrasik.fetch

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
@CompileStatic
@TypeChecked
class FetchDslBuilder extends FactoryBuilderSupport {
    private final DescriptorRepository repository;

    public FetchDslBuilder(DescriptorRepository repository) {
        super(false);
        this.repository = repository;
        registerFactories();
    }

    private void registerFactories() {
        registerFactory(FetchDsl.DESCRIPTOR, new DescriptorFactory(repository));
    }
}

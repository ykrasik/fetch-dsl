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

package com.github.ykrasik.fetch;

import com.github.ykrasik.fetch.node.FetchDescriptor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Yevgeny Krasik
 */
public interface FetchDescriptorManager {
    FetchDescriptor getFetchDescriptorById(String id);

    void load(File file) throws IOException;
    void load(URL url) throws IOException;

    void evaluate(String script);

    /**
     * Optional method that tells the manager to attempt to resolve all foreign references eagerly now
     * (instead of waiting for them to be resolved on first access).
     * If any reference are unresolvable, a {@link java.lang.IllegalArgumentException} will be thrown.
     *
     * There is a slight overhead incurred for resolving lazy references on first access (due to thread safety)
     * so it is recommended to call this method once you've finished loading all your scripts.
     */
    void resolveReferences();
}

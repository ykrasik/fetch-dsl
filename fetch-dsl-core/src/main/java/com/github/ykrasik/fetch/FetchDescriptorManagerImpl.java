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

import com.github.ykrasik.fetch.dsl.FetchDslBuilder;
import com.github.ykrasik.fetch.node.FetchDescriptor;
import com.github.ykrasik.fetch.node.FetchNode;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Yevgeny Krasik
 */
public class FetchDescriptorManagerImpl implements FetchDescriptorManager {
    private final Map<String, FetchDescriptor> fetchDescriptorMap;

    public FetchDescriptorManagerImpl() {
        this.fetchDescriptorMap = new HashMap<>();
    }

    @Override
    public FetchDescriptor getFetchDescriptorById(String id) {
        return fetchDescriptorMap.get(id);
    }

    @Override
    public void load(File file) throws IOException {
        Objects.requireNonNull(file, "File is null!");
        final GroovyShell groovyShell = createGroovyShell();
        groovyShell.evaluate(file);
    }

    @Override
    public void load(URL url) throws IOException {
        Objects.requireNonNull(url, "URL is null!");
        final GroovyShell groovyShell = createGroovyShell();
        final InputStreamReader reader = new InputStreamReader(url.openStream());
        groovyShell.evaluate(reader);
    }

    @Override
    public void evaluate(String script) {
        Objects.requireNonNull(script, "Script is null!");
        final GroovyShell groovyShell = createGroovyShell();
        groovyShell.evaluate(script);
    }

    @Override
    public void resolveReferences() {
        for (FetchDescriptor fetchDescriptor : fetchDescriptorMap.values()) {
            resolveFetchNode(fetchDescriptor);
        }
    }

    private void resolveFetchNode(FetchNode fetchNode) {
        // Simply trying to access all children of a fetchDescriptor is enough to cause it to be resolved.
        for (FetchNode node : fetchNode.getChildren()) {
            resolveFetchNode(node);
        }
    }

    public void addFetchDescriptor(FetchDescriptor fetchDescriptor) {
        final String id = fetchDescriptor.getId();
        if (fetchDescriptorMap.containsKey(id)) {
            throw new IllegalArgumentException("Already have a fetchDescriptor with id: " + id);
        }
        fetchDescriptorMap.put(id, fetchDescriptor);
    }

    private GroovyShell createGroovyShell() {
        final FetchDslBuilder builder = new FetchDslBuilder(this);
        final FetchDslBinding binding = new FetchDslBinding(builder);
        return new GroovyShell(binding);
    }
}

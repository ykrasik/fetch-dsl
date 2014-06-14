package com.rawcod.fetch;

import com.rawcod.fetch.dsl.FetchDslBuilder;
import com.rawcod.fetch.node.FetchDescriptor;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * User: ykrasik
 * Date: 25/05/14
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
    public void evaluate(String script) throws IOException {
        Objects.requireNonNull(script, "Script is null!");
        final GroovyShell groovyShell = createGroovyShell();
        groovyShell.evaluate(script);
    }

    public void addFetchDescriptor(FetchDescriptor fetchDescriptor) {
        fetchDescriptorMap.put(fetchDescriptor.getId(), fetchDescriptor);
    }

    private GroovyShell createGroovyShell() {
        final FetchDslBuilder builder = new FetchDslBuilder(this);
        final FetchDslBinding binding = new FetchDslBinding(builder);
        return new GroovyShell(binding);
    }
}

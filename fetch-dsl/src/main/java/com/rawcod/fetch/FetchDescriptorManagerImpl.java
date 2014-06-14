package com.rawcod.fetch;

import com.rawcod.fetch.dsl.FetchDslBuilder;
import com.rawcod.fetch.exception.DslException;
import com.rawcod.fetch.node.FetchDescriptor;
import com.rawcod.fetch.node.FetchNode;
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
            throw new DslException("Already have a fetchDescriptor with id: " + id);
        }
        fetchDescriptorMap.put(id, fetchDescriptor);
    }

    private GroovyShell createGroovyShell() {
        final FetchDslBuilder builder = new FetchDslBuilder(this);
        final FetchDslBinding binding = new FetchDslBinding(builder);
        return new GroovyShell(binding);
    }
}

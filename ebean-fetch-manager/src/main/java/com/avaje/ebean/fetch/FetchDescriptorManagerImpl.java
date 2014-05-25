package com.avaje.ebean.fetch;

import com.avaje.ebean.fetch.dsl.EbeanFetchDslBuilder;
import com.avaje.ebean.fetch.node.FetchDescriptor;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    public void scan(String basePathStr) throws IOException {
        final Binding binding = new Binding();
        binding.setProperty("manager", this);

        final GroovyShell groovyShell = new GroovyShell(binding);

        try {
            final File basePath = new File(getClass().getResource(basePathStr).toURI());

        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void loadFile(String path) throws IOException {
        final EbeanFetchDslBuilder builder = new EbeanFetchDslBuilder(this);
        final FetchDescriptorBinding binding = new FetchDescriptorBinding(builder);

        final GroovyShell groovyShell = new GroovyShell(binding);
        groovyShell.evaluate(new File(path));
    }

    public void addFetchDescriptor(FetchDescriptor fetchDescriptor) {
        fetchDescriptorMap.put(fetchDescriptor.getId(), fetchDescriptor);
    }
}

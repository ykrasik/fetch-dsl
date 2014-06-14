package fetch;

import fetch.dsl.FetchDslBuilder;
import fetch.node.FetchDescriptor;
import fetch.util.ResourceUtils;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
        try {
            final List<String> resources = ResourceUtils.getResources(basePathStr, "\\.groovy");
            for (String resource : resources) {
                loadFile(resource);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void loadFile(String path) throws IOException {
        evaluate(new File(path));
    }

    @Override
    public void loadFile(File file) throws IOException {
        evaluate(file);
    }

    @Override
    public void evaluate(String script) throws IOException {
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

    private void evaluate(File file) throws IOException {
        final GroovyShell groovyShell = createGroovyShell();
        groovyShell.evaluate(file);
    }
}

package com.rawcod.fetch;

import com.rawcod.fetch.node.FetchDescriptor;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public interface FetchDescriptorManager {
    FetchDescriptor getFetchDescriptorById(String id);

    void load(File file) throws IOException;
    void load(URL url) throws IOException;

    void evaluate(String script);

    /**
     * Optional method that tells the manager to attempt to resolve all foreign references eagerly now
     * (instead of waiting for them to be resolved on first access).
     * If any reference are unresolvable, a DslException will be thrown.
     *
     * There is a slight overhead incurred for resolving lazy references on first access (due to thread safety)
     * so it is recommended to call this method once you've finished loading all your scripts.
     */
    void resolveReferences();
}

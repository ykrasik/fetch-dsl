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

    void evaluate(String script) throws IOException;
}

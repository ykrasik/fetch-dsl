package com.rawcod.fetch;

import com.rawcod.fetch.node.FetchDescriptor;

import java.io.File;
import java.io.IOException;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public interface FetchDescriptorManager {
    FetchDescriptor getFetchDescriptorById(String id);

    void scan(String basePathStr) throws IOException;

    void loadFile(String path) throws IOException;
    void loadFile(File file) throws IOException;

    void evaluate(String script) throws IOException;
}

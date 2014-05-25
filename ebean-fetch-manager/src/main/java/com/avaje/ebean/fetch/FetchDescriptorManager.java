package com.avaje.ebean.fetch;

import com.avaje.ebean.fetch.node.FetchDescriptor;

import java.io.IOException;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public interface FetchDescriptorManager {
    FetchDescriptor getFetchDescriptorById(String id);

    void scan(String basePathStr) throws IOException;
}

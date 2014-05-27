package com.avaje.ebean.fetch;

import com.avaje.ebean.Query;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public interface EbeanFetchDescriptorManager {
    <T> void apply(Query<T> query, String fetchDescriptorId);
}

package com.avaje.ebean.fetch;

import com.avaje.ebean.Query;
import fetch.FetchDescriptorManager;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public interface EbeanFetchDescriptorManager extends FetchDescriptorManager {
    <T> void apply(Query<T> query, String fetchDescriptorId);
}

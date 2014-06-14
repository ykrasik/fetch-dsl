package com.rawcod.fetch.ebean;

import com.avaje.ebean.Query;
import com.rawcod.fetch.FetchDescriptorManager;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public interface EbeanFetchDescriptorManager extends FetchDescriptorManager {
    <T> void apply(Query<T> query, String fetchDescriptorId);
}

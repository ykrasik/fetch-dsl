package com.avaje.ebean.fetch

import com.avaje.ebean.fetch.dsl.EbeanFetchDslBuilder
import com.avaje.ebean.fetch.dsl.EbeanFetchDslDialect

/**
 * User: ykrasik
 * Date: 25/05/14
 */
class EbeanFetchDslBinding extends Binding {
    EbeanFetchDslBinding(EbeanFetchDslBuilder builder) {
        this.setProperty(EbeanFetchDslDialect.FETCH_DESCRIPTOR, { name, closure ->
            closure.delegate = delegate
            builder."$EbeanFetchDslDialect.FETCH_DESCRIPTOR"(name, closure)
        })
    }
}

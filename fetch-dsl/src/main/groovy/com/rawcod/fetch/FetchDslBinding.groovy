package com.rawcod.fetch

import com.rawcod.fetch.dsl.FetchDslBuilder
import com.rawcod.fetch.dsl.FetchDslDialect


/**
 * User: ykrasik
 * Date: 25/05/14
 */
class FetchDslBinding extends Binding {
    FetchDslBinding(FetchDslBuilder builder) {
        this.setProperty(FetchDslDialect.FETCH_DESCRIPTOR, { name, closure ->
            closure.delegate = delegate
            builder."$FetchDslDialect.FETCH_DESCRIPTOR"(name, closure)
        })
    }
}

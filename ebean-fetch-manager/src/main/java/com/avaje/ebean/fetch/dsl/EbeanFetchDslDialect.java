package com.avaje.ebean.fetch.dsl;

/**
 * User: ykrasik
 * Date: 25/05/14
 *
 * Example:
 *
 * fetchDescriptor("thisIsMyName") {
 *     fetch "columnName"
 *     fetch "thisColumnIsAForeignKeyThatWillBeFetchedWithAnInlineFetchDescriptor" descriptor {
 *         fetch "*"
 *     }
 *     fetch "thisColumnIsAForeignKeyThatWillBeFetchedWithAnotherNamedFetchDescriptor" descriptor "#anotherDescriptor"
 * }
 *
 * fetchDescriptor("anotherDescriptor") {
 *     fetch "*"
 * }
 */
public final class EbeanFetchDslDialect {
    private EbeanFetchDslDialect() {
    }

    /**
     * Describes a new fetch descriptor.
     * Must be followed by a name and a closure with 'fetch' clauses.
     */
    public static final String FETCH_DESCRIPTOR = "fetchDescriptor";

    /**
     * Describes an inline fetch descriptor.
     * Does not require a name, but requires a closure with 'fetch' clauses.
     * For internal use.
     */
    public static final String INLINE_FETCH_DESCRIPTOR = "inlineFetchDescriptor";

    /**
     * Individual fetch clause.
     * Describes how a certain column should be fetched. Possible options:
     * 1. Simple column fetch
     * 2. Foreign key with inline descriptor
     * 3. Foreign key with reference to another named descriptor
     */
    public static final String FETCH = "fetch";
}

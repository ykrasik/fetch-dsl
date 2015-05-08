/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 * http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package com.github.ykrasik.fetch;

import com.github.ykrasik.fetch.node.FetchDescriptor;

/**
 * <pre>
 * A DSL for instructing ORM queries which columns to fetch eagerly.
 * This is admittedly geared towards JPA-style ORMs, and this implementation is completely ORM-independent...
 * it merely translates the DSL into a parent-child tree.
 * ORM-specific implementations are expected to know how to translate this relationship to instructions their queries understand.
 *
 * Most ORM implementations operate with partial objects - the same object can be re-used in multiple queries, but it
 * is fetched partially - every query specifies which fields of the object (mapped to columns in the table) to fetch.
 * Access to any field that wasn't explicitly pre-fetched will cause the ORM to load it lazily from the DB.
 * To prevent this lazy loading, the ORM has to be instructed up front which columns to fetch for every query.
 * The eager-fetch configuration can become very deep (for tables with lots of joins) and cumbersome to maintain,
 * and this tries to alleviate the strain a bit.
 *
 * The basic unit of work is a FetchDescriptor.
 * A FetchDescriptor, which from now will be referred to simply as 'descriptor', is simply a fetch configuration with a name.
 * A descriptor describes the columns that should be eagerly fetched, and can have a nested parent-child structure that
 * signifies joins and the columns to be fetched from the joined table.
 *
 * Example:
 *   descriptor('desc') {
 *       column_1
 *       column_2 'child_2'
 *       column_3 {
 *           child_3 {
 *               another_child
 *           }
 *       }
 *       column_4 '#desc2'
 *   }
 *
 *   descriptor('desc2') {
 *       column_5
 *   }
 *
 * The above creates 2 descriptors.
 * The first is called 'desc', and when it is applied to a query it will do the following (Each query is applied to a 'base table'):
 *
 *   1. Instruct the query to fetch 'column_1', 'column_2', 'column_3', 'column4' from the base table (the table may contain
 *      other columns that will not be fetched eagerly).
 *
 *   2. Instruct the query to join the base table with the table to which 'column2' is a foreign key,
 *      and from that joined table fetch a single column 'child_2'.
 *      This is a shorthand syntax for when only a single columns needs to be fetched from a joined table.
 *      The name of the column <b>must</b> be enclosed in single or double quotes.
 *
 *   3. Instruct the query to join the base table with the table to which 'column3' is a foreign key,
 *      and from that joined table fetch 'child_3', and then join with the table to which 'child_3' is a foreign key
 *      and from that joined table fetch 'another_child'.
 *      Each opened bracket block may contain as many columns as desired.
 *
 *   4. Instruct the query to join the base table with the table to which 'column4' is a foreign key,
 *      and fetch that joined table with the descriptor with id 'desc2'.
 *      The descriptor name <b>must</b> start with a '#' and be enclosed in single or double quotes.
 *      This descriptor can be declared later in the file, or in a completely different file, as long
 *      as that file is loaded by this manager. In this case, it means fetch 'column5'.
 * </pre>
 *
 * @author Yevgeny Krasik
 */
public interface FetchDescriptorManager {
    /**
     * Return a descriptor by it's id.
     *
     * @param id Descriptor id to look-up.
     * @return Descriptor matching the requested id.
     * @throws IllegalArgumentException If this repository doesn't contain a descriptor with the requested {@link FetchDescriptor#getId() id}.
     */
    FetchDescriptor getFetchDescriptor(String id);
}

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

package com.github.ykrasik.fetch.ebean;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Yevgeny Krasik
 */
public class EbeanFetchDescriptorManagerTest {
    private EbeanFetchDescriptorManagerBuilder builder;
    private EbeanFetchDescriptorManager manager;

    private Map<String, String> queryMap;
    private Map<String, String> expectedMap;

    private Query<EbeanFetchDescriptorManagerTest> query;

    @Before
    public void setUp() {
        builder = new EbeanFetchDescriptorManagerBuilder();

        queryMap = new HashMap<>();
        expectedMap = new HashMap<>();

        query = mock(Query.class);
        when(query.fetch(any(String.class), any(String.class), any(FetchConfig.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                queryMap.put((String) args[0], (String) args[1]);
                return null;
            }
        });
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void fullTest() throws IOException {
        load("FullTest.groovy");
        build();

        addExpected(null, "d1_c1", "d1_c2", "d1_c3", "d1_c4", "d1_c5", "public", "static", "final", "forwardReference");

        addExpected("d1_c2", "*");
        addExpected("d1_c3", "d1_c31");
        addExpected("d1_c4", "d1_c41");

        addExpected("d1_c5", "d1_c51", "d1_c52", "d1_c53");
        addExpected("d1_c5.d1_c52", "d1_c521");
        addExpected("d1_c5.d1_c53", "d1_c531");
        addExpected("d1_c5.d1_c53.d1_c531", "d1_c5311");
        addExpected("d1_c5.d1_c53.d1_c531.d1_c5311", "d1_c53111");
        addExpected("d1_c5.d1_c53.d1_c531.d1_c5311.d1_c53111", "*");

        addExpected("public", "void");
        addExpected("static", "private", "col");

        addExpected("forwardReference", "backwardReference", "and", "with");
        addExpected("forwardReference.backwardReference", "simple");
        addExpected("forwardReference.backwardReference.simple", "but");
        addExpected("forwardReference.backwardReference.simple.but", "nested");
        addExpected("forwardReference.and", "of");
        addExpected("forwardReference.and.of", "course", "and", "even", "more", "columns");
        addExpected("forwardReference.and.of.course", "more");
        addExpected("forwardReference.and.of.course.more", "columns");
        addExpected("forwardReference.with", "*");

        assertExpected("desc1");
    }

    private void load(String resource) throws IOException {
        builder.load(getResource(resource));
    }

    private URL getResource(String name) {
        return getClass().getResource(name);
    }

    private void build() {
        manager = builder.build();
    }

    private void addExpected(String path, String... properties) {
        expectedMap.put(path, join(properties));
    }

    private String join(String... properties) {
        final StringBuilder sb = new StringBuilder();
        for (String property : properties) {
            sb.append(property);
            sb.append(',');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private void assertExpected(String descriptorId) {
        manager.apply(query, descriptorId);
        assertEquals("Expected and actual query maps are different size!", expectedMap.size(), queryMap.size());
        for (String path : expectedMap.keySet()) {
            final String properties = queryMap.get(path);
            assertNotNull("Expected path not found: " + path, properties);

            final String expectedProperties = expectedMap.get(path);
            assertEquals("Expected and actual properties mismatch!", expectedProperties, properties);
        }
    }
}

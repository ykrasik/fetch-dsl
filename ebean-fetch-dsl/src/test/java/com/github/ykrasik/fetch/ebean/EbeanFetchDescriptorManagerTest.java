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
    private EbeanFetchDescriptorManager manager;

    private Map<String, String> queryMap;
    private Map<String, String> expectedMap;

    private Query<EbeanFetchDescriptorManagerTest> query;

    @Before
    public void setUp() {
        manager = new EbeanFetchDescriptorManagerImpl(1000);

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

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFetchDescriptor() {
        apply("desc1");
    }

    @Test
    public void fullTest() throws IOException {
        load("FullTest.groovy");

        addExpected(null, "d1_c1", "d1_c2", "d1_c3", "d1_c4");
        addExpected("d1_c2", "*");
        addExpected("d1_c3", "d1_c31", "d1_c32");
        addExpected("d1_c3.d1_c32", "d1_c321");
        addExpected("d1_c4", "d2_c1", "d2_c2", "final", "public");
        addExpected("d1_c4.d2_c2", "d2_c21");
        addExpected("d1_c4.d2_c2.d2_c21", "*");

        apply("desc1");

        assertExpected();
    }

    private void load(String resource) throws IOException {
        manager.load(getResource(resource));
    }

    private URL getResource(String name) {
        return getClass().getResource(name);
    }

    private void apply(String fetchDescriptorId) {
        manager.apply(query, fetchDescriptorId);
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

    private void assertExpected() {
        assertEquals("Expected and actual query maps are different size!", expectedMap.size(), queryMap.size());
        for (String path : expectedMap.keySet()) {
            final String properties = queryMap.get(path);
            assertNotNull("Expected path not found: " + path, properties);

            final String expectedProperties = expectedMap.get(path);
            assertEquals("Expected and actual properties mismatch!", expectedProperties, properties);
        }
    }
}

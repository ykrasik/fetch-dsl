package com.rawcod.fetch.ebean;

import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import com.rawcod.fetch.exception.DslException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class EbeanFetchDescriptorManagerTest {
    private Map<String, String> queryMap;
    private Map<String, String> expectedMap;

    private Query<EbeanFetchDescriptorManagerTest> query;

    @Before
    public void setUp() {
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

    @Test(expected = DslException.class)
    public void testInvalidFetchDescriptor() {
        final EbeanFetchDescriptorManager manager = new EbeanFetchDescriptorManagerImpl(1000);
        manager.apply(query, "desc1");
    }

    @Test
    public void testLoadUrl() throws IOException {
        addExpected(null, "d1_c1", "d1_c2", "d1_c3", "d1_c4");
        addExpected("d1_c2", "*");
        addExpected("d1_c3", "d1_c31", "d1_c31");
        addExpected("d1_c3.d1_c31", "d1_c311");
        addExpected("d1_c4", "d2_c1", "d2_c2", "final", "public");
        addExpected("d1_c4.d2_c2", "d2_c21");
        addExpected("d1_c4.d2_c2.d2_c21", "*");

        final EbeanFetchDescriptorManager manager = new EbeanFetchDescriptorManagerImpl(1000);
        manager.load(getClass().getResource("UrlTest.groovy"));
        manager.apply(query, "desc1");

        assertExpected();
    }

    @Test
    public void testLoadFile() throws Exception {
        addExpected(null, "d1_c1", "d1_c2", "d1_c3", "d1_c4");
        addExpected("d1_c2", "*");
        addExpected("d1_c3", "d1_c31", "d1_c31");
        addExpected("d1_c3.d1_c31", "d1_c311");
        addExpected("d1_c4", "d2_c1", "d2_c2", "final", "public");
        addExpected("d1_c4.d2_c2", "d2_c21");
        addExpected("d1_c4.d2_c2.d2_c21", "*");

        final EbeanFetchDescriptorManager manager = new EbeanFetchDescriptorManagerImpl(1000);
        manager.load(new File(getClass().getResource("UrlTest.groovy").toURI()));
        manager.apply(query, "desc1");

        assertExpected();
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

package com.rawcod.fetch;

import com.rawcod.fetch.node.FetchDescriptor;
import com.rawcod.fetch.node.FetchDescriptorImpl;
import com.rawcod.fetch.node.FetchNode;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * User: ykrasik
 * Date: 14/06/2014
 */
public class FetchDescriptorManagerTest {
    private FetchDescriptorManager manager;
    private FetchNode expected;

    @Before
    public void setUp() {
        manager = new FetchDescriptorManagerImpl();
    }

    @Test
    public void testInvalidFetchDescriptor() {
        assertNull(manager.getFetchDescriptorById("desc1"));
    }

    @Test
    public void testLoadUrl() throws IOException {
        manager.load(getClass().getResource("UrlTest.groovy"));

        final String expectedFetchDescriptorId = "desc1";

        setExpected(
            fetch(expectedFetchDescriptorId,
                fetch("d1_c1"),
                fetch("d1_c2",
                    fetch("*")
                ),
                fetch("d1_c3",
                    fetch("d1_c31"),
                    fetch("d1_c31",
                        fetch("d1_c311")
                    )
                ),
                fetch("d1_c4",
                    fetch("d2_c1"),
                    fetch("d2_c2",
                        fetch("d2_c21",
                            fetch("*")
                        )
                    ),
                    fetch("final"),
                    fetch("public")
                )
            )
        );

        assertExpected(expectedFetchDescriptorId);
    }

    private void setExpected(FetchNode fetchNode) {
        this.expected = fetchNode;
    }

    private FetchDescriptor fetch(String name, FetchNode... children) {
        return new FetchDescriptorImpl(name, Arrays.asList(children));
    }

    private void assertExpected(String id) {
        final FetchDescriptor fetchDescriptor = manager.getFetchDescriptorById(id);
        assertNotNull("Can't find fetchDescriptor: " + id, fetchDescriptor);
        assertFetchNode(expected, fetchDescriptor);
    }

    private void assertFetchNode(FetchNode expected, FetchNode actual) {
        assertEquals("Column name mismatch!", expected.getColumn(), actual.getColumn());
        assertEquals("Children count mismatch!", expected.getChildren().size(), actual.getChildren().size());

        for (int i = 0; i < expected.getChildren().size(); i++) {
            final FetchNode expectedChild = expected.getChildren().get(i);
            final FetchNode actualChild = actual.getChildren().get(i);
            assertFetchNode(expectedChild, actualChild);
        }
    }
}

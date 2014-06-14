package com.rawcod.fetch;

import com.rawcod.fetch.exception.DslException;
import com.rawcod.fetch.node.FetchDescriptor;
import com.rawcod.fetch.node.FetchDescriptorImpl;
import com.rawcod.fetch.node.FetchNode;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        load("FullTest.groovy");

        setExpectedFetchDescriptorFullTest();
        assertExpected("desc1");
    }

    @Test
    public void testLoadFile() throws Exception {
        final File file = new File(getResource("FullTest.groovy").toURI());
        manager.load(file);

        setExpectedFetchDescriptorFullTest();
        assertExpected("desc1");
    }

    @Test
    public void testEvalScript() throws Exception {
        final String script = new String(Files.readAllBytes(Paths.get(getResource("FullTest.groovy").toURI())));
        manager.evaluate(script);

        setExpectedFetchDescriptorFullTest();
        assertExpected("desc1");
    }

    @Test
    public void testFetchDescriptorRef() throws IOException {
        // Assert that the descriptor used as a ref is also accessible on it's own
        load("FullTest.groovy");

        final String expectedFetchDescriptorId = "desc2";
        setExpected(
            fetch(expectedFetchDescriptorId,
                fetch("d2_c1"),
                fetch("d2_c2",
                    fetch("d2_c21",
                        fetch("*")
                    )
                ),
                fetch("final"),
                fetch("public")
            )
        );
        assertExpected(expectedFetchDescriptorId);
    }

    @Test
    public void testDeeplyNestedFetchDescriptor() throws IOException {
        load("DeeplyNestedFetchDescriptor.groovy");

        final String expectedFetchDescriptorId = "desc1";
        setExpected(
            fetch(expectedFetchDescriptorId,
                fetch("c1",
                    fetch("c2",
                        fetch("c3",
                            fetch("c4")
                        )
                    )
                )
            )
        );
        assertExpected(expectedFetchDescriptorId);
    }

    @Test(expected = DslException.class)
    public void testDuplicateColumnName() throws IOException {
        load("DuplicateColumn.groovy");
    }

    @Test
    public void testFatherChildSameName() throws IOException {
        load("FatherChildSameName.groovy");

        final String expectedFetchDescriptorId = "desc1";
        setExpected(
            fetch(expectedFetchDescriptorId,
                fetch("col",
                    fetch("col")
                )
            )
        );
        assertExpected(expectedFetchDescriptorId);
    }

    @Test(expected = DslException.class)
    public void testDuplicateFetchDescriptor() throws IOException {
        // Load the same file twice, creating a duplicate fetchDescriptor
        load("DuplicateFetchDescriptor.groovy");
        load("DuplicateFetchDescriptor.groovy");
    }

    @Test(expected = DslException.class)
    public void testInvalidFetchDescriptorRefLazy() throws IOException {
        load("InvalidFetchDescriptorRef.groovy");

        // References are resolved lazily,
        // so we will only get the exception when we try to do something with the fetchDescriptor.
        manager.getFetchDescriptorById("desc1").getChildren().get(0).getChildren();
    }

    @Test(expected = DslException.class)
    public void testInvalidFetchDescriptorRefEager() throws IOException {
        load("InvalidFetchDescriptorRef.groovy");

        // Try to eagerly resolve the fetchDescriptor reference
        manager.resolveReferences();
    }

    private void load(String resource) throws IOException {
        manager.load(getResource(resource));
    }

    private URL getResource(String name) {
        return getClass().getResource(name);
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

    private void setExpectedFetchDescriptorFullTest() {
        setExpected(
            fetch("desc1",
                fetch("d1_c1"),
                fetch("d1_c2",
                    fetch("*")
                ),
                fetch("d1_c3",
                    fetch("d1_c31"),
                    fetch("d1_c32",
                        fetch("d1_c321")
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
    }
}

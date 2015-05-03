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
import com.github.ykrasik.fetch.node.FetchDescriptorImpl;
import com.github.ykrasik.fetch.node.FetchNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Yevgeny Krasik
 */
public class FetchDescriptorManagerTest {
    private FetchDescriptorManagerBuilder builder;
    private FetchDescriptorManager manager;

    @Before
    public void setUp() {
        builder = new FetchDescriptorManagerBuilder();
    }

    @After
    public void tearDown() {
        manager = null;
    }

    @Test
    public void testLoadUrl() throws IOException {
        load("FullTest.groovy");
        build();

        assertFullTest();
    }

    @Test
    public void testLoadFile() throws Exception {
        final File file = new File(getResource("FullTest.groovy").toURI());
        builder.load(file);
        build();

        assertFullTest();
    }

    @Test
    public void testEvalScript() throws Exception {
        final String script = new String(Files.readAllBytes(Paths.get(getResource("FullTest.groovy").toURI())));
        evaluate(script);
        build();

        assertFullTest();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyManager() {
        build();

        manager.getFetchDescriptor("desc1");
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFetchDescriptor() {
        evaluate("descriptor('notDesc1') { stuff }");
        build();

        manager.getFetchDescriptor("desc1");
        fail();
    }

    @Test
    public void testCircularDependency() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyScript() {
        evaluate("");
        build();

        manager.getFetchDescriptor("desc1");
        fail();
    }

    @Test
    public void testEmptyDescriptor() {
        evaluate("descriptor('empty') {}");
        build();

        final FetchDescriptor empty = fetch("empty");
        assertFetchDescriptor(empty, "empty");
    }

    @Test
    public void testDeeplyNestedFetchDescriptor() throws IOException {
        load("DeeplyNestedDescriptor.groovy");
        build();

        final FetchDescriptor desc5 = fetch("desc5", fetch("c5"));
        final FetchDescriptor desc4 = fetch("desc4", fetch("c4", flatten(desc5)));
        final FetchDescriptor desc3 = fetch("desc3", fetch("c3", flatten(desc4)));
        final FetchDescriptor desc2 = fetch("desc2", fetch("c2", flatten(desc3)));
        final FetchDescriptor desc1 = fetch("desc1", fetch("c1", flatten(desc2)));

        assertFetchDescriptor(desc5, "desc5");
        assertFetchDescriptor(desc4, "desc4");
        assertFetchDescriptor(desc3, "desc3");
        assertFetchDescriptor(desc2, "desc2");
        assertFetchDescriptor(desc1, "desc1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateColumnName() throws IOException {
        load("DuplicateColumn.groovy");
        build();
        fail();
    }

    @Test
    public void testFatherChildSameName() throws IOException {
        load("FatherChildSameName.groovy");
        build();

        final FetchDescriptor desc = fetch("desc", fetch("col", fetch("col")));
        assertFetchDescriptor(desc, "desc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateFetchDescriptor() throws IOException {
        // Load the same file twice, creating a duplicate fetchDescriptor
        final String script = "descriptor('duplicate') {}";
        evaluate(script);
        evaluate(script);
        build();
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDescriptorRef() throws IOException {
        load("InvalidFetchDescriptorRef.groovy");
        build();
        fail();
    }

    private void load(String resource) throws IOException {
        builder.load(getResource(resource));
    }

    private void evaluate(String script) {
        builder.evaluate(script);
    }

    private URL getResource(String name) {
        return getClass().getResource(name);
    }

    private void build() {
        this.manager = builder.build();
    }

    private FetchDescriptor fetch(String name, FetchNode... children) {
        return new FetchDescriptorImpl(name, Arrays.asList(children));
    }

    private FetchNode[] flatten(FetchDescriptor descriptor) {
        return (FetchNode[]) descriptor.getChildren().toArray();
    }

    private void assertFetchDescriptor(FetchDescriptor expected, String id) {
        final FetchDescriptor fetchDescriptor = manager.getFetchDescriptor(id);
        assertFetchNode(expected, fetchDescriptor);
    }

    private void assertFetchNode(FetchNode expected, FetchNode actual) {
        assertEquals("Column name mismatch!", expected.getColumn(), actual.getColumn());
        assertEquals("Children count mismatch for node: " + expected.getColumn(), expected.getChildren().size(), actual.getChildren().size());

        for (int i = 0; i < expected.getChildren().size(); i++) {
            final FetchNode expectedChild = expected.getChildren().get(i);
            final FetchNode actualChild = actual.getChildren().get(i);
            assertFetchNode(expectedChild, actualChild);
        }
    }

    private void assertFullTest() {
        final FetchDescriptor desc2 = getFullTestDesc2();
        final FetchDescriptor desc3 = getFullTestDesc3(desc2);
        final FetchDescriptor desc1 = getFullTestDesc1(desc3);

        assertFetchDescriptor(desc1, "desc1");
        assertFetchDescriptor(desc2, "desc2");
        assertFetchDescriptor(desc3, "desc3");
    }

    private FetchDescriptor getFullTestDesc2() {
        return fetch("desc2",
            fetch("simple",
                fetch("but",
                    fetch("nested")
                )
            )
        );
    }

    private FetchDescriptor getFullTestDesc3(FetchDescriptor desc2) {
        return fetch("desc3",
            fetch("backwardReference", flatten(desc2)),
            fetch("and",
                fetch("of",
                    fetch("course",
                        fetch("more",
                            fetch("columns")
                        )
                    ),
                    fetch("and"),
                    fetch("even"),
                    fetch("more"),
                    fetch("columns")
                )
            ),
            fetch("with",
                fetch("*")
            )
        );
    }

    private FetchDescriptor getFullTestDesc1(FetchDescriptor desc3) {
        return fetch("desc1",
            fetch("d1_c1"),
            fetch("d1_c2",
                fetch("*")
            ),
            fetch("d1_c3",
                fetch("d1_c31")
            ),
            fetch("d1_c4",
                fetch("d1_c41")
            ),
            fetch("d1_c5",
                fetch("d1_c51"),
                fetch("d1_c52",
                    fetch("d1_c521")
                ),
                fetch("d1_c53",
                    fetch("d1_c531",
                        fetch("d1_c5311",
                            fetch("d1_c53111",
                                fetch("*")
                            )
                        )
                    )
                )
            ),
            fetch("public",
                fetch("void")
            ),
            fetch("static",
                fetch("private"),
                fetch("col")
            ),
            fetch("final"),
            fetch("forwardReference", flatten(desc3))
        );
    }
}

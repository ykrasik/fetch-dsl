/******************************************************************************
 * Copyright (C) 2015 Yevgeny Krasik                                          *
 *
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

import com.github.ykrasik.fetch.util.ClassPathScanner;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * A builder for a {@link FetchDescriptorManager}.
 * Can collect descriptors from multiple locations (descriptor definitions can safely be split among files).
 * Once all descriptors have been collected, calling {@link #build()} will create the {@link FetchDescriptorManager}.
 *
 * <B>NOT THREAD SAFE.</B>
 *
 * @author Yevgeny Krasik
 */
public class FetchDescriptorManagerBuilder {
    private final DescriptorRepository repository = new DescriptorRepository();

    /**
     * Load all descriptor definitions from the given {@link File}.
     * Must be a .groovy file conforming to the DSL.
     *
     * @param file File to load.
     * @return this, for chaining.
     * @throws IOException If there was an error reading from the file.
     */
    public FetchDescriptorManagerBuilder loadFile(File file) throws IOException {
        Objects.requireNonNull(file, "File is null!");
        final GroovyShell groovyShell = createGroovyShell();
        groovyShell.evaluate(file);
        return this;
    }

    /**
     * Load all descriptor definitions from the given {@link URL}.
     * Must be a .groovy file conforming to the DSL.
     *
     * @param url Url to load.
     * @return this, for chaining.
     * @throws IOException If there was an error reading from the url.
     */
    public FetchDescriptorManagerBuilder loadUrl(URL url) throws IOException {
        Objects.requireNonNull(url, "URL is null!");
        final GroovyShell groovyShell = createGroovyShell();
        final InputStreamReader reader = new InputStreamReader(url.openStream());
        groovyShell.evaluate(reader);
        return this;
    }

    /**
     * Load all descriptor definitions from the given {@link String}.
     * Must be a script conforming to the DSL.
     *
     * @param script Script to load.
     * @return this, for chaining.
     */
    public FetchDescriptorManagerBuilder evaluate(String script) {
        Objects.requireNonNull(script, "Script is null!");
        final GroovyShell groovyShell = createGroovyShell();
        groovyShell.evaluate(script);
        return this;
    }

    /**
     * Scan the class-path for any .groovy files and load them under the specified package and load them.
     * If the .groovy file is not a valid DSL script, an exception will be thrown, so make sure all your descriptor
     * definition files are grouped under the same package and that no non-descriptor-definition .groovy files are
     * accessible from that package.
     * Currently, does not pick up .jar files that are under a directory that is on the class-path, only .jar files that
     * are directly on the class-path.
     * Meaning, if there is a directory called 'dir' on the classpath (-cp dir), and under that directory is a .jar file
     * called 'file.jar' with the given package, that jar file will not be scanned.
     * Only .jar files that are directly on the classpath (-cp file.jar) will be picked up.
     *
     * @param packageName Package name to scan.
     * @return this, for chaining.
     * @throws IOException If there was an error reading from a file (or from a jar).
     */
    public FetchDescriptorManagerBuilder scanPackage(String packageName) throws IOException {
        final List<URL> resources = ClassPathScanner.scanClasspath(packageName, ".*\\.groovy");
        for (URL resource : resources) {
            loadUrl(resource);
        }
        return this;
    }

    /**
     * Build a {@link FetchDescriptorManager}.
     * This should be called after this builder was loaded with all descriptor definition files
     * through {@link #loadFile(File)}, {@link #loadUrl(URL)}, {@link #evaluate(String)} or {@link #scanPackage(String)}.
     *
     * @return A {@link FetchDescriptorManager} built from all the loaded descriptors.
     */
    public FetchDescriptorManager build() {
        return new FetchDescriptorManagerImpl(repository);
    }

    private GroovyShell createGroovyShell() {
        final FetchDslBuilder builder = new FetchDslBuilder(repository);
        final FetchDslBinding binding = new FetchDslBinding(builder);
        return new GroovyShell(binding);
    }
}

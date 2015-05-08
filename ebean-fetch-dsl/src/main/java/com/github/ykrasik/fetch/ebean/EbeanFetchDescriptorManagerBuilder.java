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

package com.github.ykrasik.fetch.ebean;

import com.github.ykrasik.fetch.FetchDescriptorManager;
import com.github.ykrasik.fetch.FetchDescriptorManagerBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A builder for an {@link EbeanFetchDescriptorManager}.
 * Can collect descriptors from multiple locations (descriptor definitions can safely be split among files).
 * Once all descriptors have been collected, calling {@link #build()} will create the {@link EbeanFetchDescriptorManager}.
 *
 * <B>NOT THREAD SAFE.</B>
 *
 * @author Yevgeny Krasik
 */
public class EbeanFetchDescriptorManagerBuilder {
    private final FetchDescriptorManagerBuilder builder = new FetchDescriptorManagerBuilder();
    private int fetchSize = 1000;

    /**
     * Set the query fetch size.
     *
     * @param fetchSize Fetch size to set.
     * @return this, for chaining.
     */
    public EbeanFetchDescriptorManagerBuilder setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    /**
     * Load all descriptor definitions from the given {@link File}.
     * Must be a .groovy file conforming to the DSL.
     *
     * @param file File to load.
     * @return this, for chaining.
     * @throws IOException If there was an error reading from the file.
     */
    public EbeanFetchDescriptorManagerBuilder loadFile(File file) throws IOException {
        builder.loadFile(file);
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
    public EbeanFetchDescriptorManagerBuilder loadUrl(URL url) throws IOException {
        builder.loadUrl(url);
        return this;
    }

    /**
     * Load all descriptor definitions from the given {@link String}.
     * Must be a script conforming to the DSL.
     *
     * @param script Script to load.
     * @return this, for chaining.
     */
    public EbeanFetchDescriptorManagerBuilder evaluate(String script) {
        builder.evaluate(script);
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
    public EbeanFetchDescriptorManagerBuilder scanPackage(String packageName) throws IOException {
        builder.scanPackage(packageName);
        return this;
    }

    /**
     * Build an {@link EbeanFetchDescriptorManager}.
     * This should be called after this builder was loaded with all descriptor definition files
     * through {@link #loadFile(File)}, {@link #loadUrl(URL)}, {@link #evaluate(String)} or {@link #scanPackage(String)}.
     *
     * @return An {@link EbeanFetchDescriptorManager} built from all the loaded descriptors,
     *         with the configured fetch size.
     */
    public EbeanFetchDescriptorManager build() {
        final FetchDescriptorManager manager = builder.build();
        return new EbeanFetchDescriptorManagerImpl(manager, fetchSize);
    }
}

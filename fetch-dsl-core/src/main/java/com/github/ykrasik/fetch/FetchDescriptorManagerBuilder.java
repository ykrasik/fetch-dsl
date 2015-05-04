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
 * This object is <B>NOT THREAD SAFE.</B>
 *
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class FetchDescriptorManagerBuilder {
    private final DescriptorRepository repository = new DescriptorRepository();

    public FetchDescriptorManagerBuilder loadFile(File file) throws IOException {
        Objects.requireNonNull(file, "File is null!");
        final GroovyShell groovyShell = createGroovyShell();
        groovyShell.evaluate(file);
        return this;
    }

    public FetchDescriptorManagerBuilder loadPath(String path) throws IOException {
        Objects.requireNonNull(path, "Path is null!");
        return loadUrl(Thread.currentThread().getContextClassLoader().getResource(path));
    }

    public FetchDescriptorManagerBuilder loadUrl(URL url) throws IOException {
        Objects.requireNonNull(url, "URL is null!");
        final GroovyShell groovyShell = createGroovyShell();
        final InputStreamReader reader = new InputStreamReader(url.openStream());
        groovyShell.evaluate(reader);
        return this;
    }

    public FetchDescriptorManagerBuilder evaluate(String script) {
        Objects.requireNonNull(script, "Script is null!");
        final GroovyShell groovyShell = createGroovyShell();
        groovyShell.evaluate(script);
        return this;
    }

    public FetchDescriptorManagerBuilder scanPath(String basePath) throws IOException {
        Objects.requireNonNull(basePath, "Path is null!");
        final List<URL> resources = ClassPathScanner.scanClasspathForResourceUrls(basePath, "\\.groovy");
        for (URL resource : resources) {
            loadUrl(resource);
        }
        return this;
    }

    public FetchDescriptorManager build() {
        return new FetchDescriptorManagerImpl(repository);
    }

    private GroovyShell createGroovyShell() {
        final FetchDslBuilder builder = new FetchDslBuilder(repository);
        final FetchDslBinding binding = new FetchDslBinding(builder);
        return new GroovyShell(binding);
    }
}

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

package com.github.ykrasik.fetch.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public final class ClassPathScanner {
    private static final Logger LOG = LoggerFactory.getLogger(ClassPathScanner.class);

    private final List<String> resources = new ArrayList<>();

    private final Pattern pattern;
    private final Path root;

    private ClassPathScanner(Pattern pattern) throws URISyntaxException {
        this.pattern = pattern;
        this.root = Paths.get(Thread.currentThread().getContextClassLoader().getResource(".").toURI());
    }

    public void scanPath(String path) throws IOException {
        final File file = new File(path);
        if (file.isDirectory()) {
            scanDirectory(file);
        } else {
            scanJarFile(file);
        }
    }

    private void scanDirectory(File directory) throws IOException {
        final File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file);
            } else {
                final String absolutePath = file.getCanonicalPath();
                final String fileName = toRelativePath(absolutePath).replace('\\', '/');
                addIfMatches(fileName);
            }
        }
    }

    private String toRelativePath(String absolutePath) {
        try {
            // Transform an absolute path to a path on the classPath relative to the working directory.
            final Path relativePath = root.relativize(Paths.get(absolutePath));
            return relativePath.normalize().toString();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void scanJarFile(File file) throws IOException {
        try (JarFile jarFile = new JarFile(file)) {
            final Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                final JarEntry entry = e.nextElement();
                final String fileName = entry.getName();
                addIfMatches(fileName);
            }
        }
    }

    private void addIfMatches(String name) {
        if (pattern.matcher(name).matches()) {
            LOG.debug("Classpath scan hit: {}", name);
            resources.add(name);
        }
    }

    public static List<String> scanClasspathForResourceNames(String basePath, String pattern) {
        Objects.requireNonNull(basePath, "Path is null!");
        Objects.requireNonNull(pattern, "Pattern is null!");

        final String classPath = System.getProperty("java.class.path", "");
        final String[] classPathElements = classPath.split(File.pathSeparator);

        try {
            final ClassPathScanner scanner = new ClassPathScanner(compilePattern(basePath, pattern));
            for (String path : classPathElements) {
                scanner.scanPath(path);
            }
            return scanner.resources;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<URL> scanClasspathForResourceUrls(String basePath, String pattern) {
        final List<String> names = scanClasspathForResourceNames(basePath, pattern);
        final List<URL> resources = new ArrayList<>(names.size());
        for (String name : names) {
            final URL url = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(name), "Internal error getting resource from classpath: " + name);
            resources.add(url);
        }
        return resources;
    }

    private static Pattern compilePattern(String basePath, String matchPattern) {
        final StringBuilder sb = new StringBuilder();
        sb.append(".*");
        sb.append(basePath);
        if (!basePath.endsWith("/")) {
            sb.append('/');
        }
        sb.append(".*");
        sb.append(matchPattern);
        return Pattern.compile(sb.toString());
    }
}

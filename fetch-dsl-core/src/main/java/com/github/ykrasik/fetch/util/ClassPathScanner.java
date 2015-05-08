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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A class-path scanner that matches files by a pattern.
 *
 * @author Yevgeny Krasik
 */
public final class ClassPathScanner {
    private static final Logger LOG = LoggerFactory.getLogger(ClassPathScanner.class);

    private ClassPathScanner() { }

    /**
     * Scan the class-path from the given package, returning {@link URL}s to each resource whose name matches the give pattern.
     * Currently, does not pick up .jar files that are under a directory that is on the class-path, only .jar files that
     * are directly on the class-path.
     * Meaning, if there is a directory called 'dir' on the classpath (-cp dir), and under that directory is a .jar file
     * called 'file.jar' with the given package, that jar file will not be scanned.
     * Only .jar files that are directly on the classpath (-cp file.jar) will be picked up.
     *
     * @param packageName Package to scan
     * @param pattern Pattern to match resource names on. Only applies to the actual file name, not it's path.
     * @return A {@link List} of {@link URL}s to resources on the class-path under the given package that match the given pattern.
     */
    public static List<URL> scanClasspath(String packageName, String pattern) {
        Objects.requireNonNull(packageName, "Package name is null!");
        Objects.requireNonNull(pattern, "Pattern is null!");

        final String packagePath = packageName.replace('.', '/');
        try {
            final ClassPathFileVisitor visitor = new ClassPathFileVisitor(Pattern.compile(pattern));
            final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                scanUrl(url, visitor);
            }
            return visitor.resources;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void scanUrl(URL url, ClassPathFileVisitor visitor) throws IOException, URISyntaxException {
        final String filePath = url.getPath();
        final String[] pathElements = filePath.split("!");
        if (pathElements.length > 1) {
            // URL points inside a Jar file - create a fileSystem from the jar file itself (the part before the '!'),
            // then navigate the the part after the '!' and walk the fileTree from there.
            try (final FileSystem fs = FileSystems.newFileSystem(Paths.get(URI.create(pathElements[0])), null)) {
                final Path path = fs.getPath(pathElements[1]);
                Files.walkFileTree(path, visitor);
            }
        } else {
            // URL points to a directory - just walk the fileTree.
            final Path path = Paths.get(url.toURI());
            Files.walkFileTree(path, visitor);
        }
    }

    private static class ClassPathFileVisitor extends SimpleFileVisitor<Path> {
        private final List<URL> resources = new ArrayList<>();
        private final Pattern pattern;

        private ClassPathFileVisitor(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            final String fileName = path.getFileName().toString();
            if (pattern.matcher(fileName).matches()) {
                LOG.debug("Classpath scan hit: {}", path);
                resources.add(path.toUri().toURL());
            }
            return FileVisitResult.CONTINUE;
        }
    }
}

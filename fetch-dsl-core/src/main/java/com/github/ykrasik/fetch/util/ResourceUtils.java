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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Yevgeny Krasik
 */
public final class ResourceUtils {
    private ResourceUtils() { }

    /**
     * for all elements of java.class.path get a Collection of resources
     * Pattern pattern = Pattern.compile(".*"); gets all resources
     */
    public static List<String> getResources(String basePath, String matchPattern) {
        final List<String> retval = new ArrayList<>();
        final String classPath = System.getProperty("java.class.path", "");
        final String[] classPathElements = classPath.split(File.pathSeparator);
        final Pattern pattern = compilePattern(basePath, matchPattern);
        for (String element : classPathElements) {
            retval.addAll(getResources(element, pattern));
        }
        return retval;
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

    private static List<String> getResources(String elementFullPath, Pattern pattern) {
        final List<String> retval = new ArrayList<>();
        final File file = new File(elementFullPath);
        if (file.isDirectory()) {
            retval.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private static List<String> getResourcesFromJarFile(File file, Pattern pattern) {
        final List<String> retval = new ArrayList<>();
        try (ZipFile zf = new ZipFile(file)) {
            final Enumeration e = zf.entries();
            while (e.hasMoreElements()) {
                final ZipEntry ze = (ZipEntry) e.nextElement();
                final String fileName = ze.getName();
                addIfMatches(retval, pattern, fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return retval;
    }

    private static List<String> getResourcesFromDirectory(File directory, Pattern pattern) {
        final List<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else {
                try {
                    final String fileName = file.getCanonicalPath();
                    addIfMatches(retval, pattern, fileName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return retval;
    }

    private static void addIfMatches(List<String> list, Pattern pattern, String fileName) {
        if (pattern.matcher(fileName).matches()) {
            list.add(fileName);
        }
    }
}

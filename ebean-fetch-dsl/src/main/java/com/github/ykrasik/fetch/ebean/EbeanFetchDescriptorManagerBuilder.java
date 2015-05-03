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
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
public class EbeanFetchDescriptorManagerBuilder {
    private final FetchDescriptorManagerBuilder builder = new FetchDescriptorManagerBuilder();
    private int fetchSize = 1000;

    public EbeanFetchDescriptorManagerBuilder setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }

    public EbeanFetchDescriptorManagerBuilder load(File file) throws IOException {
        builder.load(file);
        return this;
    }

    public EbeanFetchDescriptorManagerBuilder load(URL url) throws IOException {
        builder.load(url);
        return this;
    }

    public EbeanFetchDescriptorManagerBuilder evaluate(String script) {
        builder.evaluate(script);
        return this;
    }

    public EbeanFetchDescriptorManager build() {
        final FetchDescriptorManager manager = builder.build();
        return new EbeanFetchDescriptorManagerImpl(manager, fetchSize);
    }
}

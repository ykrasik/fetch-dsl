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

package com.github.ykrasik.fetch

/**
 * @author Yevgeny Krasik
 */
// TODO: JavaDoc
final class FetchDsl {
    private FetchDsl() { }

    /**
     * Describes a new fetch descriptor.
     * Takes 2 parameters:
     *   1. A String describing the name of the descriptor.
     *   2. A closure defining the columns to be fetched.
     */
    public static final String DESCRIPTOR = "descriptor";
}

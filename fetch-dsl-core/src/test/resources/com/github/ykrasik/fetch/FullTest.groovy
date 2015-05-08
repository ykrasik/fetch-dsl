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

descriptor('desc1') {
    // Simple column
    d1_c1

    // Fetch-all syntax
    d1_c2 '*'

    // 1-level nesting
    d1_c3 {
        d1_c31
    }

    // Shorthand 1-level nesting, the 2nd argument must be enclosed in single or double quotes (groovy constraint).
    d1_c4 'd1_c41'

    // Deep nesting
    d1_c5 {
        d1_c51
        d1_c52 {
            d1_c521
        }
        d1_c53 {
            d1_c531 {
                d1_c5311 {
                    d1_c53111 '*'
                }
            }
        }
    }

    // Java or groovy keywords must be prefixed by a single '_'
    _public 'void'
    _static {
        _private
        col
    }
    _final

    // Reference to another descriptor that wasn't defined yet, must be enclosed in single or double quotes.
    forwardReference '#desc3'
}

descriptor('desc2') {
    simple {
        but {
            nested
        }
    }
}

descriptor('desc3') {
    // Reference to another descriptor that was already defined
    backwardReference '#desc2'
    and {
        of {
            course {
                more 'columns'
            }
            and
            even
            more
            columns
        }
    }
    with '*'
}
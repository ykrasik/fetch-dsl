package com.rawcod.fetch.node;

import java.util.List;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public interface FetchNode {
    String getColumn();

    List<FetchNode> getChildren();
}

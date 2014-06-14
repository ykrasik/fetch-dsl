package com.rawcod.fetch.node;

import java.util.Collections;
import java.util.List;

/**
 * User: ykrasik
 * Date: 25/05/14
 */
public class ColumnFetchNode implements FetchNode {
    private final String column;

    public ColumnFetchNode(String column) {
        this.column = column;
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public List<FetchNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ColumnFetchNode{");
        sb.append("column='").append(column).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

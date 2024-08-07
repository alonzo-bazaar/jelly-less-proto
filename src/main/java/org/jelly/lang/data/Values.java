package org.jelly.lang.data;

import org.jelly.utils.ArrayUtils;

import java.util.List;

public record Values(List<Object> values) {
    @Override
    public String toString() {
        return "values[" + ArrayUtils.renderArr(values.toArray(), ", ") + "]";
    }
}

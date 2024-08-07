package org.jelly.eval.runtime.error;

import org.jelly.utils.ArrayUtils;

public class JellyError extends RuntimeException {
    public JellyError(String s) {
        super(s);
    }

    public JellyError(String s, Object... irritants) {
        super(s + ".\nAnnoyants:" + ArrayUtils.renderArr(irritants, ",\n") + "\n");
    }
}

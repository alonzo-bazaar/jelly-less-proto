package org.jelly.eval.runtime.error;

import org.jelly.utils.ArrayUtils;

public class JellyError extends RuntimeException {
    public JellyError(String s) {
        super(s);
    }

    public JellyError(String s, Throwable t) {
        super("Jelly Error : " + s, t);
    }

    public JellyError(String s, Object... annoyants) {
        super(s + ".\nAnnoyants:" + ArrayUtils.renderArr(annoyants, ",\n") + "\n");
    }
}

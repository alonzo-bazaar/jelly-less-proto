package org.jelly.parse;

import org.jelly.lang.LispList;

public class SynthaxDesugarer {
    public LispList desugar(LispList sugared) {
        /* removes syntactic sugar from sugared expressio
         * the two elements of syntactic sugar we're allowing are
         * - quotation : 'a -> (quote a)
         * - dotted cons notation : (a . b) -> (cons a b)
         *
         * this function detects both these notations and turns them
         */
        return null;
    }
}

package org.jelly.parse;

import org.jelly.parse.preprocessor.ReaderMacros;
import org.jelly.parse.syntaxtree.SyntaxTreeIterator;
import org.jelly.parse.token.TokenIterator;
import org.jelly.utils.StringArrIterator;

import java.util.Iterator;

public class BaseParserTest {
    public static TokenIterator tokensFromLines(String... lines) {
        return new TokenIterator(new StringArrIterator(lines));
    }

    public static SyntaxTreeIterator expressionsFromLines(String... lines) {
        return new SyntaxTreeIterator(tokensFromLines(lines));
    }

    public static ReaderMacros readMacroExpressionsFromLines(String... lines) {
        return ReaderMacros.from(expressionsFromLines(lines));
    }
}

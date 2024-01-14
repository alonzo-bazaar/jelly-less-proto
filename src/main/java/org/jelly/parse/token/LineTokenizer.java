package org.jelly.parse.token;

import org.jelly.parse.token.errors.TokenLineParsingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LineTokenizer {
    /**
     * tokenizza la riga
     * in caso di enti multiline (stringa o commento multiline) estrae fino al suddetto ente (incluso)
     * e con questo ho solo spostato il macigno di NewTokenIterator in un'altra classe, bene o male
     * TODO spezzetta sto tumore, stai facendo una mezza god class
     */
    private String currentLine;
    private final Iterator<String> otherLines;
    private int currentIndex;

    private boolean wentPastLine = false;

    public LineTokenizer(int startingIndex, String startingLine, Iterator<String> others) {
        this.currentIndex = startingIndex;
        this.currentLine = startingLine;
        this.otherLines = others;
    }

    public List<Token> tokenizeLine() throws TokenLineParsingException {
        List<Token> res = new ArrayList<>();
        while(skipUntilNonWhite()) {
            if(startsWithInlineComment()) {
                currentIndex = currentLine.length(); // flag for exhaustion
                return res;
            }

            // multliline shit (if(wentPastLine())
            else if(startsWithMultilineComment()) {
                skipMultilineComment();
                if(wentPastLine) {
                    return res;
                }
            }

            else if(startsWithStringLiteral()) {
                res.addLast(extractStringLiteral());
                if(wentPastLine) {
                    return res;
                }
            }

            // il resto sono tutti commenti inline
            // stessa logica per tutti
            else {
                res.addLast(extractInlineToken());
            }
        }

        return res;
    }

    /* to be called after calling tokenizeLine()
     * LineTokenizer::tokenizeLine() is one big mr.sideEffect,
     * and half this class's job is in those side effects
     *
     * getCurrentLine() should be called first, because side effects
     * I'm not too big a fan of temporal couplings either, but this works well enough for now
     * and it is hopefully moving toward a less shit structure of this lexer
     */
    public String getCurrentLine() {
        return currentLine;
    }

    public int getIndex() {
        return currentIndex;
    }


    public Iterator<String> getRemainingLines() {
        return otherLines;
    }



    // utility idiote, ma comunque utility
    private boolean isAtEndOfLine() {
        return currentIndex == currentLine.length();
    }

    boolean lineWasExhausted() {
        return currentIndex >= currentLine.length();
    }

    private boolean skipUntilNonWhite() {
        while(currentIndex < currentLine.length() && Character.isWhitespace(currentLine.charAt(currentIndex))) {
                currentIndex++;
        }
        return !isAtEndOfLine();
    }

    private void assertCurrentChar(char c, String error) throws TokenLineParsingException {
        if(currentLine.charAt(currentIndex) != c) {
            throw new TokenLineParsingException(error, currentIndex);
        }
    }

    private char currentChar() {
        return currentLine.charAt(currentIndex);
    }

    private boolean hasNextChar() {
        return !isAtEndOfLine() || otherLines.hasNext();
    }

    private boolean nextLine() {
        // advances lines to the next line
        // returns true if this was possible to do, false otherwise
        if(otherLines.hasNext()) {
            wentPastLine = true;
            currentIndex = 0;
            currentLine = otherLines.next();
            return true;
        }
        return false;
    }

    private boolean checkAdvance(int n) {
        // returns false if all chars were exhausted during advancement by n
        // exhaustion is not an exception as character exhaustion is expected behaviour, but it needs to be known
        int newIndex = currentIndex + n;
        while(newIndex >= currentLine.length()) {
            newIndex -= currentLine.length();
            if(!nextLine())
                return false;
        }
        currentIndex = newIndex;
        return true;
    }

    private Token extractInlineToken() throws TokenLineParsingException {
        /* CONTRACT (don't know how to enforce this, so I'll just state it in this comment)
         * - does not change currentLine
         * - does not change otherLines
         * - remains entirely within the context of currentLine
         */

        if (startsWithPunctuation())
            return extractPunctuation();
        // else if (startsWithStringLiteral())
        //     return extractStringLiteral();
        else if (startsWithCharLiteral())
            return extractCharLiteral();

        // symbols, integers, floats &Co. have the same rules, easier this way
        else {
            String lex = extractNormalString();
            if (StringUtils.stringIsInteger(lex))
                return new LiteralToken<>(lex, Long.parseLong(lex));
            else if (StringUtils.stringIsFloat(lex))
                return new LiteralToken<>(lex, Double.parseDouble(lex));
            else
                return new NormalToken(lex);
        }
    }

    // startsWith, startsWith everywhere
    // with index
    boolean startsWithPrefix(String prefix, int i) { return currentLine.startsWith(prefix, i); }
    boolean startsWithWhitespace(int i) { return Character.isWhitespace(currentLine.charAt(i)); }
    boolean startsWithPunctuation(int i) { return StringUtils.stringStartsWithPunctuation(currentLine, i); }
    boolean startsWithStringLiteral(int i) { return startsWithPrefix("\"", i); }
    boolean startsWithCharLiteral(int i) { return startsWithPrefix("#\\", i); }
    boolean startsWithLiteral(int i) { return startsWithStringLiteral(i) || startsWithCharLiteral(i); }
    boolean startsWithInlineComment(int i) { return startsWithPrefix(";", i); }
    boolean startsWithMultilineComment(int i) { return startsWithPrefix("#|", i); }
    boolean startsWithComment(int i) { return startsWithInlineComment(i) || startsWithMultilineComment(i); }

    // default index
    boolean startsWithPrefix(String prefix) { return startsWithPrefix(prefix, currentIndex); }
    boolean startsWithWhitespace() { return startsWithWhitespace(currentIndex); }
    boolean startsWithPunctuation() { return startsWithPunctuation(currentIndex); }
    boolean startsWithStringLiteral() { return startsWithStringLiteral(currentIndex); }
    boolean startsWithCharLiteral() { return startsWithCharLiteral(currentIndex); }
    boolean startsWithLiteral() { return startsWithStringLiteral() || startsWithCharLiteral(); }
    boolean startsWithInlineComment() { return startsWithInlineComment(currentIndex); }
    boolean startsWithMultilineComment() { return startsWithMultilineComment(currentIndex); }
    boolean startsWithComment() { return startsWithInlineComment() || startsWithMultilineComment(); }


    // time for extraction
    // this thing is going to be long, way too long
    private void skipMultilineComment() throws TokenLineParsingException {
        while(!startsWithPrefix("|#")) {
            if(!checkAdvance(1)) {
                throw new TokenLineParsingException("multiline comment not closed ", currentIndex);
            }
        }

        if(!startsWithPrefix("|#")) {
            throw new TokenLineParsingException("error skipping multiline comment ", currentIndex);
        }
        currentIndex += 2;
    }

    // token extraction
    String extractNormalString () {
        int tokenEnd = currentIndex;
        while(!endOfNormalTokenString(tokenEnd)) {
            tokenEnd++;
        }
        String res = currentLine.substring(currentIndex, tokenEnd);
        currentIndex = tokenEnd;
        return res;
    }

    boolean endOfNormalTokenString(int i) {
        return i >= currentLine.length()
                || startsWithWhitespace(i) || startsWithComment(i)
                || startsWithPunctuation(i) || startsWithLiteral(i);
    }

    Token extractPunctuation() throws TokenLineParsingException {
        // assumes that this.startsWithPunctuation()
        // and that the state
        // of the iterator hasn't been change after that called returned true

        String s = StringUtils.longestStartingPunctuation(currentLine, currentIndex);
        if(s == null) {
            throw new TokenLineParsingException("cannot get punctuation prefix ", currentIndex);
        }
        currentIndex += s.length();
        return new PunctuationToken(s);
    }

    LiteralToken<String> extractStringLiteral() throws TokenLineParsingException {
        StringBuilder lexBuild = new StringBuilder();
        assertCurrentChar('"', "error during extraction of string literal, could not find opening '\"'");
        appendNormalStringChar(lexBuild);

        while (hasNextChar() && !startsWithPrefix("\"")) {
            String escape = prefixEscape();
            if(escape == null) {
                appendNormalStringChar(lexBuild);
            }
            else {
                lexBuild.append(escape);
                if(!checkAdvance(2)) {
                    throw new TokenLineParsingException("error at an escaped character while reading string ", currentIndex);
                    /* all string escape sequences so far ('\n', \t', '\\', and '\r') have length 2
                     * TODO rework code if you need to introduce new escape sequences that have different lengths
                     * TODO this exception is also susceptible to row number changes,
                     * TokenLineParsingException will not work for this error, make or find something else
                     */
                }
            }
        }

        // closing '"'
        if(hasNextChar()) { // must have exited loop on the closing '"'
            assertCurrentChar('"', "error while finishing extraction of string literal, could not find '\"' terminator");
            lexBuild.append(currentChar());
            currentIndex++;
        }
        else
            throw new TokenLineParsingException("error while finishing extraction of string literal, likely character to be exhaustion", currentIndex);

        String lex = lexBuild.toString();
        return new LiteralToken<>(lex, lex.substring(1, lex.length()-1));
    }

    void appendNormalStringChar(StringBuilder sb) throws TokenLineParsingException {
        // check for newline before advancing
        // (advancing changes position in line, so we need to know beforehand)
        boolean wasLast = currentIndex == currentLine.length()-1;
        char c = currentLine.charAt(currentIndex);
        if(!checkAdvance(1))
            throw new TokenLineParsingException("string not closed ", currentIndex);
        sb.append(c);
        if(wasLast)
            sb.append('\n');
    }

    String prefixEscape() {
        if(startsWithPrefix("\\\\"))
            return "\\";
        if(startsWithPrefix("\\\""))
            return "\"";
        if(startsWithPrefix("\\r"))
            return "\r";
        if(startsWithPrefix("\\n"))
            return "\n";
        if(startsWithPrefix("\\t"))
            return "\t";
        else return null;
    }

    LiteralToken<Character> extractCharLiteral() {
        // assumes startsWithCharLiteral() was called, returned true
        // and that the state of the iterator hasn't changed since

        // special chars like #\Space or #\Newline are not yet supported
        checkAdvance(2); // past the "#\" starter
        String lex = extractNormalString();
        Character special = specialCharacter(lex);
        if(special != null)
            return new LiteralToken<Character>(lex, special);
        else if(lex.length() == 1)
            return new LiteralToken<Character>(lex, lex.charAt(0));
        else throw new RuntimeException("#\\" + lex + " is not a valid special character");
    }

    Character specialCharacter(String ch) {
        return switch(ch) {
            case "Space" -> ' ';
            case "Newline" -> '\n';
            case "Return" -> '\r';
            case "Tab" -> '\t';
            case "Null" -> '\0';
            default -> null;
        };
    }
}

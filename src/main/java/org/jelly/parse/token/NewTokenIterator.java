package org.jelly.parse.token;

import org.jelly.parse.token.errors.TokenLineParsingException;
import org.jelly.parse.token.errors.TokenParsingException;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * shitty class I'm making just to jave something to replace TokenIterator with, someday
 * the code is currently a fucking assfest
 * please rework into something like
 * String s = currentLine(); buffer = tokenizeRow(s, eventualOtherLines); // eventualOtherLines for multiline strings or comments
 */
public class NewTokenIterator implements Iterator<Token> {
    private Iterator<String> sourceLines;
    // private final Queue<Token> precomputed = new LinkedList<Token>();
    private int lineNumber = 0;
    private int indexInLine = 0;
    private String currentLine;

    private List<Token> buffer = new ArrayList<>(0);

    // should be sorted from longest to shortest for implementation reasons
    private static final String[] punctuationTokens = {":::", "::", ":", "(", ")", ",", "`", "'"};

    public NewTokenIterator(Iterator<String> lines) {
        this.sourceLines = lines;
        currentLine = lines.next();
    }

    @Override
    public Token next() {
        if(hasNext()) {
            return bufferNext();
        }
        return new EOFToken();
    }

    @Override
    public boolean hasNext() {
        tryPopulateBuffer();
        return !(buffer.isEmpty());
    }

    private void tryPopulateBuffer() {
        try {
            while (buffer.isEmpty()) {
                /* TODO fa un po' schifo,
                 * ma per funzionare con un repl bisogna essere lazy sul souurceLines.next();
                 * visto che se si chiama next() a caso, questo next potrebbe richiedere user interaction
                 * e bloccare l'applicazione in momenti poco desiderabili
                 * va wrappato in qualche modo, ma intanto vediamo se funziona
                 */
                if(currentLine == null)
                    if(sourceLines.hasNext())
                        currentLine = sourceLines.next();
                    else
                        return;

                LineTokenizer lt = new LineTokenizer(indexInLine, currentLine, sourceLines);
                buffer = lt.tokenizeLine();
                if(lt.lineWasExhausted()) {
                    currentLine = null;
                    indexInLine = 0;
                    sourceLines = lt.getRemainingLines();
                }
                else {
                    currentLine = lt.getCurrentLine();
                    indexInLine = lt.getIndex();
                    sourceLines = lt.getRemainingLines();
                }
            }
        }
        catch(TokenLineParsingException lineEx) {
            throw new TokenParsingException(lineEx.getMessage(), lineNumber, lineEx.getColumnNumber(), TokenParsingException.noFile);
        }
    }

    private Token bufferNext() {
        return buffer.removeFirst();
    }

    boolean rectify() {
        /*
         * ensures indexInLine is consistent with the length of currentLine
         * to be called after using unsafeAdvance() to ensure the iterator advanced correctly
         */
        while(indexInLine >= currentLine.length()) {
            if(sourceLines.hasNext()) {
                // consume the line, love isn't always on time
                indexInLine -= currentLine.length();
                currentLine = sourceLines.next();
                lineNumber++;
            }
            else {
                return false;
            }
        }
        return true;
    }

    void unsafeAdvance(int n) {
        /*
         * normal advance() might call sourceLines.next(),
         * which will lead to undesirable effects when sourceLines comes from user input
         * to fix this we provide an unsafe option that does not call sourceLines.next()
         * this is to be used with care, and one must make sure to call rectify()
         * by the next time indexInLine and currentLine are expected to work together
         */
        /*
         * the only reason why this beast was introduced was the fact that the current logic will want
         * the current line as the line that next() is from
         * if a token contains the last character of a line then we'll need to wait for the input to get nextLine()
         * as it will want nextLine()
         * but nextLine() is user input so guess what? if a token ends a line we'll need to wait for the next line
         */
        indexInLine+=n;
    }

    boolean advance(int n) {
        unsafeAdvance(n);
        return rectify();
    }

    char currChar() {
        return currentLine.charAt(indexInLine);
    }

    boolean hasNextChar() {
        return indexInLine < currentLine.length() || sourceLines.hasNext();
    }

    // prefixes and prefix discriminators
    boolean startsWithPrefix(String prefix) {
        return currentLine.startsWith(prefix, indexInLine);
    }

    boolean startsWithWhitespace() {
        return Character.isWhitespace(currentLine.charAt(indexInLine));
    }

    boolean startsWithPunctuation() {
        for(String s : punctuationTokens) {
            if(startsWithPrefix(s))
                return true;
        }
        return false;
    }

    boolean startsWithStringLiteral() {
        return startsWithPrefix("\"");
    }

    boolean startsWithCharLiteral() {
        return startsWithPrefix("#\\");
    }

    boolean startsWithLiteral() {
        return startsWithStringLiteral() || startsWithCharLiteral();
    }

    boolean startsWithInlineComment() {
        return startsWithPrefix(";");
    }

    boolean startsWithMultilineComment() {
        return startsWithPrefix("#|");
    }

    boolean startsWithComment() {
        return startsWithInlineComment() || startsWithMultilineComment();
    }

    boolean isLastInLine() {
        return indexInLine == currentLine.length()-1;
    }

    // comment and whitespace skipping
    boolean skipToNextSignificant() {
        /*
         * skips until the next significant
         * (signifcant menaning not inter-token whitespace and not inside a comment)
         * signaling whether `lines` was exhausted during the skip
         *
         * @returns whether the skip caused characters to be exhasuted
         */
        // is a boolean status return bad?
        while(skipToNonWhite()) {
            if (startsWithInlineComment()) {
                if(!skipInlineComment())
                    return false;
            }
            else if (startsWithMultilineComment()) {
                if(!skipMultilineComment())
                    return false;
            }
            else
                break;
        }
        return hasNextChar(); // assumes that !startsWithWhitespace(), should test that
    }

    boolean skipToNonWhite() {
        /*
         * advances skipping characters until the indexInLine points to
         * a char in currentLine that is not whitespace
         * @returns false if no next whitespace character exists in `lines`
         */
        while(hasNextChar() && startsWithWhitespace()) {
            if(!advance(1))
                return false;
        }
        // if no next char then no next nonwhite char
        return hasNextChar();
    }

    boolean skipMultilineComment() {
        while(!startsWithPrefix("|#")) {
            if(!advance(1)) {
                throw new TokenParsingException("multiline comment not closed ", lineNumber, TokenParsingException.noRow, TokenParsingException.noFile);
            }
        }

        return advance(2);
    }

    boolean skipInlineComment() {
        return startOfNextLine();
    }

    boolean startOfNextLine() {
        if(sourceLines.hasNext()) {
            indexInLine = 0;
            lineNumber++;
            currentLine = sourceLines.next();
            return true;
        }
        return false;
    }

    // token extraction
    String extractNormalString () {
        StringBuilder sb = new StringBuilder();
        while(hasNextChar()
              && !startsWithWhitespace()
              && !startsWithComment()
              && !startsWithPunctuation()
              && !startsWithLiteral()) {
            if(isLastInLine()) {
                sb.append(currChar());
                unsafeAdvance(1);
                break;
            }
            sb.append(currChar());
            unsafeAdvance(1);
        }
        return sb.toString();
    }

    Token extractPunctuation() {
        // assumes startsWithPunctuation() was called, returned true,
        // and that the state
        // of the iterator hasn't been change after that called returned true

        for(String s : punctuationTokens) {
            if(startsWithPrefix(s)) {
                PunctuationToken res = new PunctuationToken(s);
                unsafeAdvance(s.length());
                return res;
            }
        }
        throw new RuntimeException("extractPunctuation() was called while the character stream wasn't starting with a lisp recognized punctuation, this state was not supposed to be reachable");
    }

    LiteralToken<String> extractStringLiteral() {
        StringBuilder lexBuild = new StringBuilder();
        // opening '"' (currChar() is assumed to be '"' by contract)
        appendNormalStringChar(lexBuild);

        while (hasNextChar() && !startsWithPrefix("\"")) {
            String escape = prefixEscape();
            if(escape == null) {
                appendNormalStringChar(lexBuild);
            }
            else {
                lexBuild.append(escape);
                if(!advance(2)) {
                    // all escape sequences so far have length 2
                    // FIXME rework code if you introduce new escape sequences that have different length
                    throw new TokenParsingException("error while reading string ", lineNumber, indexInLine, TokenParsingException.noFile);
                }
            }
        }

        // closing '"'
        if(hasNextChar()) { // must have exited loop on the closing '"'
            lexBuild.append(currChar());
            unsafeAdvance(1);
        }

        String lex = lexBuild.toString();
        return new LiteralToken<String>(lex, lex.substring(1, lex.length()-1));
    }

    void appendNormalStringChar(StringBuilder sb) {
        // check for newline before advancing
        // (advancing changes position in line, so we need to know before advancing)
        boolean wasLast = isLastInLine();
        char c = currChar();
        if(!advance(1))
            throw new TokenParsingException("string not closed ", lineNumber, TokenParsingException.noRow, TokenParsingException.noFile);
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
        advance(2);
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

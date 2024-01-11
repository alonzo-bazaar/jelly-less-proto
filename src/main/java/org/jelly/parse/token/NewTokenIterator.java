package org.jelly.parse.token;

import org.jelly.parse.token.errors.TokenParsingException;

import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

/**
 * shitty class I'm making just to jave something to replace TokenIterator with, someday
 */
public class NewTokenIterator implements Iterator<Token> {
    private final Iterator<String> sourceLines;
    private final Queue<Token> precomputed = new LinkedList<Token>();
    private int lineNumber = 0;
    private int indexInLine = 0;
    private String currentLine;

    // should be sorted from longest to shortest for implementation reasons
    private static final String[] punctuationTokens = {":::", "::", ":", "(", ")", ",", "`", "'"};

    public NewTokenIterator(Iterator<String> lines) {
        this.sourceLines = lines;
        currentLine = lines.next();
    }

    @Override
    public Token next() {
        if(hasNext()) {
            precompute();
            return precomputed.remove();
        }
        return new EOFToken();
    }

    @Override
    public boolean hasNext() {
        if(precomputed.isEmpty())
            precompute();

        return !precomputed.isEmpty();
    }

    void precompute() {
        int startingLineNumber = lineNumber;
        while(skipToNextSignificant() && lineNumber == startingLineNumber) {
            // particulary tellable tokens
            if (startsWithPunctuation())
                precomputed.add(extractPunctuation());
            else if (startsWithStringLiteral())
                precomputed.add(extractStringLiteral());
            else if (startsWithCharLiteral())
                precomputed.add(extractCharLiteral());

            // symbols, integers, floats &Co. have the same rules, easier this way
            else {
                String lex = extractNormalString();
                if (stringIsInteger(lex))
                    precomputed.add(new LiteralToken<Long>(lex, Long.parseLong(lex)));
                else if (stringIsFloat(lex))
                    precomputed.add(new LiteralToken<Double>(lex, Double.parseDouble(lex)));
                else
                    precomputed.add(new NormalToken(lex));
            }
        }
    }

    // integers and floats
    boolean stringIsInteger(String s) {
        int i = 0;
        if(s.charAt(0) == '-')
            i = 1;
        while(i != s.length()) {
            if(!charIsDigit(s.charAt(i)))
                return false;
            i++;
        }
        return true;
    }

    boolean stringIsFloat(String s) {
        int i = 0;
        boolean dot = false;
        if(s.charAt(0) == '-')
            i = 1;
        while(i != s.length()) {
            if(charIsDigit(s.charAt(i)))
                i++;
            else {
                if(!dot && s.charAt(i) == '.') {
                    dot = true;
                    i++;
                }
                else
                    return false;
            }
        }
        return dot;
    }

    boolean charIsDigit(char c) {
        return c >= '0' && c<='9';
    }

    // skipping
    boolean advance(int n) {
        /*
         * advances iterator by n characters
         * returns false if characters are exhausted, this is not handled as an exception as character exhaustion
         * is expected behaviour
         * return value is often checked in situations where character exhaustion is not expected
         * , for example while closing a multiline comment
         */
        indexInLine += n;
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

    char consumeChar() {
        char res = currentLine.charAt(indexInLine);
        advance(1);
        return res;
    }

    char peekChar() {
        return currentLine.charAt(indexInLine);
    }

    String consumePrefix(int n) {
        try {
            String res = currentLine.substring(indexInLine, indexInLine+n);
            advance(n);
            return res;
        } catch(Throwable t) {
            throw new RuntimeException
                (String.format
                 ("failed to extract prefix from line of length %d starting from index %d up to index %d (%d + %d)",
                  currentLine.length(), indexInLine, n, indexInLine, n),
                 t);

        }
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
        // boolean status return bad?
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
        // advances looking for non whitespace chars
        // assumes failure (thus false) if advance fails
        while(hasNextChar() && startsWithWhitespace()) {
            if(!advance(1))
                return false;
        }
        // if no next char then no next significant char
        return hasNextChar();
    }

    boolean skipMultilineComment() {
        while(!startsWithPrefix("|#")) {
            if (!advance(1)) {
                throw new TokenParsingException("multiline comment not closed ", lineNumber, TokenParsingException.noRow, TokenParsingException.noFile);
            }
        }

        return advance(2); // also skip the comment delimiter
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
                sb.append(consumeChar());
                break;
            }
            sb.append(consumeChar());
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
                advance(s.length());
                return res;
            }
        }
        throw new RuntimeException("extractPunctuation() was called while the character stream wasn't starting with an iterator, this state was not supposed to be reachable");
    }

    LiteralToken<String> extractStringLiteral() {
        StringBuilder lexBuild = new StringBuilder();
        // opening '"'
        appendNormalStringChar(lexBuild);

        while (hasNextChar() && !startsWithPrefix("\"")) {
            String escape = prefixEscape();
            if(escape == null) {
                appendNormalStringChar(lexBuild);
            }
            else {
                lexBuild.append(escape);
                if(!advance(2)) {
                    throw new TokenParsingException("error while reading string ", lineNumber, TokenParsingException.noRow, TokenParsingException.noFile);
                }
            }
        }

        // closing '"'
        lexBuild.append(consumeChar());

        String lex = lexBuild.toString();
        return new LiteralToken<String>(lex, lex.substring(1, lex.length()-1));
    }

    void appendNormalStringChar(StringBuilder sb) {
        // check for newline before advancing
        // (advancing changes position in line, so we need to know before advancing)
        boolean wasLast = isLastInLine();
        char c = peekChar();
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

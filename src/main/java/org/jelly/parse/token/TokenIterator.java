package org.jelly.parse.token;

import org.jelly.parse.token.errors.TokenLineParsingException;
import org.jelly.parse.token.errors.TokenParsingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * shitty class I'm making just to jave something to replace TokenIterator with, someday
 * the code is currently a fucking assfest
 * please rework into something like
 * String s = currentLine(); buffer = tokenizeRow(s, eventualOtherLines); // eventualOtherLines for multiline strings or comments
 */
public class TokenIterator implements Iterator<Token> {
    private Iterator<String> sourceLines;
    // private final Queue<Token> precomputed = new LinkedList<Token>();
    private int lineNumber = 0;
    private int indexInLine = 0;
    private String currentLine;

    private List<Token> buffer = new ArrayList<>(0);

    // should be sorted from longest to shortest for implementation reasons
    public TokenIterator(Iterator<String> lines) {
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
}

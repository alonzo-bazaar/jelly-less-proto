package org.jelly.parse.token;

import org.jelly.utils.DebuggingUtils;
import org.junit.jupiter.api.Test;
import org.jelly.parse.token.errors.TokenParsingException;

import java.lang.Math;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class NewTokenIteratorTest {

    // repeated test logic, that is, repeated "packed assertions"
    // sorry for the verbosity in the exception messages, but it's very useful in detecting and fixing why the test failed
    <T> boolean literalEquals(LiteralToken<?> t, T target) {
        return t.getVal().equals(target);
    }
    <T> void assertEqualsLiteral(Token t, T target) {
        if(t instanceof LiteralToken<?> lt) {
            if(!literalEquals(lt, target)) {
                throw new AssertionError("token " + lt + " is literal of type "
                                         + target.getClass().getCanonicalName() +
                                         " but its value \n<" + DebuggingUtils.debugRender(lt.getVal())
                                         + "> is not equal to\n<" + DebuggingUtils.debugRender(target) +
                                         "> of type " + target.getClass().getCanonicalName());
            }
        }
        else
            throw new AssertionError("token " + t + " is not a literal token of type, "
                                     + target.getClass().getCanonicalName() +
                                     "  it is instead of type "
                                     + t.getClass().getCanonicalName());
    }

    void assertEqualsFloatLiteral(Token t, Double target, double eps) {
        // float comparisons requires some extra work because of the epsilon
        if(t instanceof LiteralToken<?> lts) {
            if(lts.getVal() instanceof Double vt) {
                if(Math.abs(vt - target) < eps)
                    return;
                throw new AssertionError("token is literal of type " +
                                          target.getClass().getCanonicalName() +
                                          " but it value <" + vt + "> != <" + target + "> (with eps=" + eps + ")");
            }
            else
                throw new AssertionError("token is a literal but its values is " +
                                          DebuggingUtils.debugRender(lts.getVal()) +
                                          ", of type " + lts.getVal().getClass().getCanonicalName());
        }
        else
            throw new AssertionError(t + " should have been a literal it is instead of type " + t.getClass().getCanonicalName());
    }

    void assertEqualsNormal(Token t, String target) {
        if (Objects.requireNonNull(t) instanceof NormalToken nt) {
            assertEquals(nt.getString(), target);
        } else {
            throw new AssertionError("token " + t + "is not a normal token, it is instead of type " + t.getClass().getCanonicalName());
        }
    }

    void assertEqualsPunctuation(Token t, String target) {
        if (Objects.requireNonNull(t) instanceof PunctuationToken nt) {
            assertEquals(nt.getString(), target);
        } else {
            throw new AssertionError("token " + t + "is not a normal token, it is instead of type " + t.getClass().getCanonicalName());
        }
    }

    @Test
    public void justOneChar() {
        NewTokenIterator nti = DebuggingUtils.fromStrings("a");
        assertEqualsNormal(nti.next(), "a");
        assertFalse(nti.hasNext());
    }

    @Test
    public void justOneCharMultiline() {
        NewTokenIterator nti = DebuggingUtils.fromStrings("a", "b");
        assertEqualsNormal(nti.next(), "a");
        assertEqualsNormal(nti.next(), "b");
        assertFalse(nti.hasNext());
    }
    @Test
    public void getSpecialNonWhite() {
        NewTokenIterator nti = DebuggingUtils.fromStrings("(prova)");
        assertEqualsPunctuation(nti.next(), "(");
        assertEqualsNormal(nti.next(), "prova");
        assertEqualsPunctuation(nti.next(), ")");
        assertFalse(nti.hasNext());
    }

    @Test
    public void getSpecialWhite() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("(  kitemmuort  )");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "kitemmuort");
        assertEqualsPunctuation(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespace() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("fat       ");
        assertEqualsNormal(ti.next(), "fat");
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpace() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("      fat");
        assertEqualsNormal(ti.next(), "fat");
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpaceSpecial() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("      ::");
        assertEqualsPunctuation(ti.next(), "::");
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespaceSpecial() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(":::       ");
        assertEqualsPunctuation(ti.next(), ":::");
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespace() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("   kitemmuort       ");
        assertEqualsNormal(ti.next(), "kitemmuort");
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespaceSpecial() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("   :       ");
        assertEqualsPunctuation(ti.next(), ":");
        assertFalse(ti.hasNext());
    }

    @Test
    public void getsBiggestSpecial() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(":::::::::::");
        assertEqualsPunctuation(ti.next(), ":::");
        assertEqualsPunctuation(ti.next(), ":::");
        assertEqualsPunctuation(ti.next(), ":::");
        assertEqualsPunctuation(ti.next(), "::");
        assertFalse(ti.hasNext());
    }

    @Test
    public void noCommentAllParen() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("(when (that (fat old sun)) in (the sky))");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "when");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "that");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "fat");
        assertEqualsNormal(ti.next(), "old");
        assertEqualsNormal(ti.next(), "sun");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsNormal(ti.next(), "in");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "the");
        assertEqualsNormal(ti.next(), "sky");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void CommentNoSpacesAllSpecial() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("(when(that(fat:old::sun)):::in::(the#||#sky))");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "when");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "that");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "fat");
        assertEqualsPunctuation(ti.next(), ":");
        assertEqualsNormal(ti.next(), "old");
        assertEqualsPunctuation(ti.next(), "::");
        assertEqualsNormal(ti.next(), "sun");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), ":::");
        assertEqualsNormal(ti.next(), "in");
        assertEqualsPunctuation(ti.next(), "::");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "the");
        assertEqualsNormal(ti.next(), "sky");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void whitestHour() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("mannaggia:  kite", "\t mmuort", "\t");
        assertEqualsNormal(ti.next(), "mannaggia");
        assertEqualsPunctuation(ti.next(), ":");
        assertEqualsNormal(ti.next(), "kite");
        assertEqualsNormal(ti.next(), "mmuort");
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialWhite() { // non il dentifricio
        NewTokenIterator ti = DebuggingUtils.fromStrings("kono: dio :da");
        assertEqualsNormal(ti.next(), "kono");
        assertEqualsPunctuation(ti.next(), ":");
        assertEqualsNormal(ti.next(), "dio");
        assertEqualsPunctuation(ti.next(), ":");
        assertEqualsNormal(ti.next(), "da");
        assertFalse(ti.hasNext());
    }

    @Test
    public void string() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"stringus\"");
        assertEqualsLiteral(ti.next(), "stringus");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringStartingWithWhitespace() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"    stringus\"");
        assertEqualsLiteral(ti.next(), "    stringus");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringEndingWithWhitespace() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"stringus      \"");
        assertEqualsLiteral(ti.next(), "stringus      ");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringBeforeTokens() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"something\"in the way  ");
        assertEqualsLiteral(ti.next(), "something");
        assertEqualsNormal(ti.next(), "in");
        assertEqualsNormal(ti.next(), "the");
        assertEqualsNormal(ti.next(), "way");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringAfterTokens() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("çeci est une\"stringa\"");
        assertEqualsNormal(ti.next(), "çeci");
        assertEqualsNormal(ti.next(), "est");
        assertEqualsNormal(ti.next(), "une");
        assertEqualsLiteral(ti.next(), "stringa");
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleStrings() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"jojo\" \"bizarre\" \"strings\"");
        assertEqualsLiteral(ti.next(), "jojo");
        assertEqualsLiteral(ti.next(), "bizarre");
        assertEqualsLiteral(ti.next(), "strings");
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleAttachedStrings() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"jojo\"\"bizarre\"\"strings\"");
        assertEqualsLiteral(ti.next(), "jojo");
        assertEqualsLiteral(ti.next(), "bizarre");
        assertEqualsLiteral(ti.next(), "strings");
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringsAndSpecials() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("(strcat\"kite\"\"mmurt\" \"mannaggia \\\"\" bello bello\"mannaggia\")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "strcat");
        assertEqualsLiteral(ti.next(), "kite");
        assertEqualsLiteral(ti.next(), "mmurt");
        assertEqualsLiteral(ti.next(), "mannaggia \"");
        assertEqualsNormal(ti.next(), "bello");
        assertEqualsNormal(ti.next(), "bello");
        assertEqualsLiteral(ti.next(), "mannaggia");
        assertEqualsPunctuation(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void emptyString() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"\"");
        assertEqualsLiteral(ti.next(), "");
        assertFalse(ti.hasNext());
    }
    
    @Test
    public void multipleEmptyStrings() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"\"\"\"\"\"");
        assertEqualsLiteral(ti.next(), "");
        assertEqualsLiteral(ti.next(), "");
        assertEqualsLiteral(ti.next(), "");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupid() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("((((((((((((((((((((");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupidWhitespace() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(" ((    ((((((((((((((((((  ");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), "(");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupider() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("()()()()()()()()()()");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupiderWhitespace() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("() ( )()()()(  )()()()() ");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsPunctuation(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void ignoresInlinedMultlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        NewTokenIterator ti = DebuggingUtils.fromStrings("ti voglio #| bene |# bastonare");
        assertEqualsNormal(ti.next(), "ti");
        assertEqualsNormal(ti.next(), "voglio");
        assertEqualsNormal(ti.next(), "bastonare");
    }

    @Test
    public void ignoresMultilineComments() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("ti voglio #| bene", " |# bastonare");
        assertEqualsNormal(ti.next(), "ti");
        assertEqualsNormal(ti.next(), "voglio");
        assertEqualsNormal(ti.next(), "bastonare");
        assertFalse(ti.hasNext());
    }

    @Test
    public void ignoresInlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        NewTokenIterator ti = DebuggingUtils.fromStrings("ti voglio ;; bastonare", "benissimo; muori", "<3");
        assertEqualsNormal(ti.next(), "ti");
        assertEqualsNormal(ti.next(), "voglio");
        assertEqualsNormal(ti.next(), "benissimo");
        assertEqualsNormal(ti.next(), "<3");
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineBreaksTokensNormal() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("lovely",
                                          "rita",
                                          "meter",
                                          "maid");
        assertEqualsNormal(ti.next(), "lovely");
        assertEqualsNormal(ti.next(), "rita");
        assertEqualsNormal(ti.next(), "meter");
        assertEqualsNormal(ti.next(), "maid");
    }

    @Test
    public void newlineBreaksTokensNormalWithWhitespace() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("lovely  ",
                "rita",
                "meter ",
                "maid");
        assertEqualsNormal(ti.next(), "lovely");
        assertEqualsNormal(ti.next(), "rita");
        assertEqualsNormal(ti.next(), "meter");
        assertEqualsNormal(ti.next(), "maid");
    }

    @Test
    public void newlineBreaksTokensNormalWithComments() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("lovely ;; adjective",
                "rita #| name |#",
                "meter",
                "maid ;; because someone thought getting a parking ticket was sexy");
        assertEqualsNormal(ti.next(), "lovely");
        assertEqualsNormal(ti.next(), "rita");
        assertEqualsNormal(ti.next(), "meter");
        assertEqualsNormal(ti.next(), "maid");
    }

    @Test
    public void inlineCommentFinisher() {
        // bug earlier where skipInlineComment() wouldn't signal eof and set currentLine() to the next() of an iterator
        // that didn't hasNext(), ergo, null
        NewTokenIterator ti = DebuggingUtils.fromStrings("hello ;; comment");
        assertEqualsNormal(ti.next(), "hello");
        assertFalse(ti.hasNext());
    }


    @Test
    public void justInlineComment() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(" ;; comment");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justInlineCommentBarebone() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justInlineComments() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(" ;; comment", "; and another comment", ";;; now for no comment", ";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineComment() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(" #| comment", " and comment still |#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineCommentBarebone() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(" #||#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineCommentBareboneNewlineHugger() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(" #|","","|#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineComments() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(" #| comment", " still another |#", "#| now for no comment|#", "#||#", "");
        assertFalse(ti.hasNext());
    }

    @Test
    public void multilineCommentFinisher() {
        NewTokenIterator ti = DebuggingUtils.fromStrings ("lovely #| rita |#");
        assertEqualsNormal(ti.next(), "lovely");
        assertFalse(ti.hasNext());
    }

    @Test
    public void multilineCommentMultilineComment() {
        NewTokenIterator ti = DebuggingUtils.fromStrings ("lovely #| #| rita #| |#");
        assertEqualsNormal(ti.next(), "lovely");
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineBreaksTokensNumbers() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("lovely",
                                          "100",
                                          "meter",
                                          "0.4");
        assertEqualsNormal(ti.next(), "lovely");
        assertEqualsLiteral(ti.next(), (long)100);
        assertEqualsNormal(ti.next(), "meter");
        assertEqualsFloatLiteral(ti.next(), 0.4, 0.0001);
    }

    @Test
    public void normalCharacterLiterals() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("#\\a",
                                          "#\\b",
                                          "#\\c");
        assertEqualsLiteral(ti.next(), 'a');
        assertEqualsLiteral(ti.next(), 'b');
        assertEqualsLiteral(ti.next(), 'c');
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialCharacterLiterals() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("#\\Space",
                                          "#\\Newline",
                                          "#\\Tab");
        assertEqualsLiteral(ti.next(), ' ');
        assertEqualsLiteral(ti.next(), '\n');
        assertEqualsLiteral(ti.next(), '\t');
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringEscapeSequences() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"lovely \\n rita \\t meter\"");
        assertEqualsLiteral(ti.next(), "lovely \n rita \t meter");
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineTokenSeparators() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("made\nin\theaven");
        assertEqualsNormal(ti.next(), "made");
        assertEqualsNormal(ti.next(), "in");
        assertEqualsNormal(ti.next(), "heaven");
        assertFalse(ti.hasNext());
    }

    @Test
    public void tortureTest1() {
        NewTokenIterator ti = DebuggingUtils.fromStrings
                ("made#\\a\"in\" \t \t\t\t\t",
                        "hea#| ;:::; |#ven",
                        "it's #\\f or     ;;");
        assertEqualsNormal(ti.next(), "made");
        assertEqualsLiteral(ti.next(), 'a');
        assertEqualsLiteral(ti.next(), "in");
        assertEqualsNormal(ti.next(), "hea");
        assertEqualsNormal(ti.next(), "ven");
        assertEqualsNormal(ti.next(), "it");
        assertEqualsPunctuation(ti.next(), "'");
        assertEqualsNormal(ti.next(), "s");
        assertEqualsLiteral(ti.next(), 'f');
        assertEqualsNormal(ti.next(), "or");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testThrowsOnUnclosedComment() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("beginning #|  ", "and no end");
        TokenParsingException tpe = assertThrows(TokenParsingException.class, () -> {ti.next(); ti.next();});
        assertTrue(tpe.getMessage().contains("comment"));
    }

    @Test
    public void testThrowsOnUnclosedString() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\" some string with no end  ");
        TokenParsingException tpe = assertThrows(TokenParsingException.class,
                () -> {
            Token a = ti.next();
        });
        assertTrue(tpe.getMessage().contains("string"));
    }

    @Test
    public void testMultilineStrings() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"lovely rita", "meter maid\"");
        assertEqualsLiteral(ti.next(), "lovely rita\nmeter maid");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineStringStartAtEndOfLine() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"", "ok\"");
        assertEqualsLiteral(ti.next(), "\nok");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineStringEndAtEndOfLine() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"ok", "\"");
        assertEqualsLiteral(ti.next(), "ok\n");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingPoint() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("(+ 10 0.1)");
        assertEqualsPunctuation(ti.next(), "(");
        assertEqualsNormal(ti.next(), "+");
        assertEqualsLiteral(ti.next(), (long)10);
        assertEqualsFloatLiteral(ti.next(), 0.1, 0.00001);
        assertEqualsPunctuation(ti.next(), ")");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingNothingBeforeDot() {
        NewTokenIterator ti = DebuggingUtils.fromStrings(".01");
        assertEqualsFloatLiteral(ti.next(), 0.01, 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingNothingBeforeDotSign() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("-.01");
        assertEqualsFloatLiteral(ti.next(), -0.01, 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingSign() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("-0.01");
        assertEqualsFloatLiteral(ti.next(), -0.01, 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractInteger() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("100");
        assertEqualsLiteral(ti.next(), (long)100);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractIntegerLeadingZeros() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("0100");
        assertEqualsLiteral(ti.next(), (long)100);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractIntegerSign() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("-100");
        assertEqualsLiteral(ti.next(), (long)-100);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractCharacterLiteral() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("#\\Space",
                    "#\\Tab",
                    "#\\Newline",
                    "#\\Return",
                    "#\\Null"
        );
        assertEqualsLiteral(ti.next(), ' ');
        assertEqualsLiteral(ti.next(), '\t');
        assertEqualsLiteral(ti.next(), '\n');
        assertEqualsLiteral(ti.next(), '\r');
        assertEqualsLiteral(ti.next(), '\0');
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringEscapes() {
        NewTokenIterator ti = DebuggingUtils.fromStrings("\"lovely \\\\ rita \\n meter \\t maid \\r\\n\"");
        assertEqualsLiteral(ti.next(), "lovely \\ rita \n meter \t maid \r\n");
        assertFalse(ti.hasNext());
    }
}

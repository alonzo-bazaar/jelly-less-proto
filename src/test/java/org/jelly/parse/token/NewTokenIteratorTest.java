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
    <T> void assertEqualsLiteral(T target, Token t) {
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

    void assertEqualsFloatLiteral(Double target, Token t, double eps) {
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

    void assertEqualsNormal(String target, Token t) {
        if (Objects.requireNonNull(t) instanceof NormalToken nt) {
            assertEquals(nt.getString(), target);
        } else {
            throw new AssertionError("token " + t + "is not a normal token, it is instead of type " + t.getClass().getCanonicalName());
        }
    }

    void assertEqualsPunctuation(String target, Token t) {
        if (Objects.requireNonNull(t) instanceof PunctuationToken nt) {
            assertEquals(nt.getString(), target);
        } else {
            throw new AssertionError("token " + t + "is not a normal token, it is instead of type " + t.getClass().getCanonicalName());
        }
    }

    @Test
    public void justOneChar() {
        NewTokenIterator nti = DebuggingUtils.tokensFromStrings("a");
        assertEqualsNormal("a", nti.next());
        assertFalse(nti.hasNext());
    }

    @Test
    public void justOneCharMultiline() {
        NewTokenIterator nti = DebuggingUtils.tokensFromStrings("a", "b");
        assertEqualsNormal("a", nti.next());
        assertEqualsNormal("b", nti.next());
        assertFalse(nti.hasNext());
    }
    @Test
    public void getSpecialNonWhite() {
        NewTokenIterator nti = DebuggingUtils.tokensFromStrings("(prova)");
        assertEqualsPunctuation("(", nti.next());
        assertEqualsNormal("prova", nti.next());
        assertEqualsPunctuation(")", nti.next());
        assertFalse(nti.hasNext());
    }

    @Test
    public void getSpecialWhite() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("(  kitemmuort  )");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("kitemmuort", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespace() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("fat       ");
        assertEqualsNormal("fat", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpace() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("      fat");
        assertEqualsNormal("fat", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpaceSpecial() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("      ::");
        assertEqualsPunctuation("::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespaceSpecial() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(":::       ");
        assertEqualsPunctuation(":::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespace() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("   kitemmuort       ");
        assertEqualsNormal("kitemmuort", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespaceSpecial() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("   :       ");
        assertEqualsPunctuation(":", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void getsBiggestSpecial() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(":::::::::::");
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation("::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void noCommentAllParen() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("(when (that (fat old sun)) in (the sky))");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("when", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("that", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("fat", ti.next());
        assertEqualsNormal("old", ti.next());
        assertEqualsNormal("sun", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsNormal("in", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("the", ti.next());
        assertEqualsNormal("sky", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void CommentNoSpacesAllSpecial() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("(when(that(fat:old::sun)):::in::(the#||#sky))");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("when", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("that", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("fat", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("old", ti.next());
        assertEqualsPunctuation("::", ti.next());
        assertEqualsNormal("sun", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsNormal("in", ti.next());
        assertEqualsPunctuation("::", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("the", ti.next());
        assertEqualsNormal("sky", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void whitestHour() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("mannaggia:  kite", "\t mmuort", "\t");
        assertEqualsNormal("mannaggia", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("kite", ti.next());
        assertEqualsNormal("mmuort", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialWhite() { // non il dentifricio
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("kono: dio :da");
        assertEqualsNormal("kono", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("dio", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("da", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void string() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"stringus\"");
        assertEqualsLiteral("stringus", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringStartingWithWhitespace() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"    stringus\"");
        assertEqualsLiteral("    stringus", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringEndingWithWhitespace() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"stringus      \"");
        assertEqualsLiteral("stringus      ", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringBeforeTokens() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"something\"in the way  ");
        assertEqualsLiteral("something", ti.next());
        assertEqualsNormal("in", ti.next());
        assertEqualsNormal("the", ti.next());
        assertEqualsNormal("way", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringAfterTokens() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("çeci est une\"stringa\"");
        assertEqualsNormal("çeci", ti.next());
        assertEqualsNormal("est", ti.next());
        assertEqualsNormal("une", ti.next());
        assertEqualsLiteral("stringa", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleStrings() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"jojo\" \"bizarre\" \"strings\"");
        assertEqualsLiteral("jojo", ti.next());
        assertEqualsLiteral("bizarre", ti.next());
        assertEqualsLiteral("strings", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleAttachedStrings() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"jojo\"\"bizarre\"\"strings\"");
        assertEqualsLiteral("jojo", ti.next());
        assertEqualsLiteral("bizarre", ti.next());
        assertEqualsLiteral("strings", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringsAndSpecials() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("(strcat\"kite\"\"mmurt\" \"mannaggia \\\"\" bello bello\"mannaggia\")");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("strcat", ti.next());
        assertEqualsLiteral("kite", ti.next());
        assertEqualsLiteral("mmurt", ti.next());
        assertEqualsLiteral("mannaggia \"", ti.next());
        assertEqualsNormal("bello", ti.next());
        assertEqualsNormal("bello", ti.next());
        assertEqualsLiteral("mannaggia", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void emptyString() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"\"");
        assertEqualsLiteral("", ti.next());
        assertFalse(ti.hasNext());
    }
    
    @Test
    public void multipleEmptyStrings() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"\"\"\"\"\"");
        assertEqualsLiteral("", ti.next());
        assertEqualsLiteral("", ti.next());
        assertEqualsLiteral("", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupid() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("((((((((((((((((((((");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupidWhitespace() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(" ((    ((((((((((((((((((  ");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupider() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("()()()()()()()()()()");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupiderWhitespace() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("() ( )()()()(  )()()()() ");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertEqualsPunctuation("(", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void ignoresInlinedMultlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("ti voglio #| bene |# bastonare");
        assertEqualsNormal("ti", ti.next());
        assertEqualsNormal("voglio", ti.next());
        assertEqualsNormal("bastonare", ti.next());
    }

    @Test
    public void ignoresMultilineComments() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("ti voglio #| bene", " |# bastonare");
        assertEqualsNormal("ti", ti.next());
        assertEqualsNormal("voglio", ti.next());
        assertEqualsNormal("bastonare", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void ignoresInlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("ti voglio ;; bastonare", "benissimo; muori", "<3");
        assertEqualsNormal("ti", ti.next());
        assertEqualsNormal("voglio", ti.next());
        assertEqualsNormal("benissimo", ti.next());
        assertEqualsNormal("<3", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineBreaksTokensNormal() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("lovely",
                                          "rita",
                                          "meter",
                                          "maid");
        assertEqualsNormal("lovely", ti.next());
        assertEqualsNormal("rita", ti.next());
        assertEqualsNormal("meter", ti.next());
        assertEqualsNormal("maid", ti.next());
    }

    @Test
    public void newlineBreaksTokensNormalWithWhitespace() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("lovely  ",
                "rita",
                "meter ",
                "maid");
        assertEqualsNormal("lovely", ti.next());
        assertEqualsNormal("rita", ti.next());
        assertEqualsNormal("meter", ti.next());
        assertEqualsNormal("maid", ti.next());
    }

    @Test
    public void newlineBreaksTokensNormalWithComments() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("lovely ;; adjective",
                "rita #| name |#",
                "meter",
                "maid ;; because someone thought getting a parking ticket was sexy");
        assertEqualsNormal("lovely", ti.next());
        assertEqualsNormal("rita", ti.next());
        assertEqualsNormal("meter", ti.next());
        assertEqualsNormal("maid", ti.next());
    }

    @Test
    public void inlineCommentFinisher() {
        // bug earlier where skipInlineComment() wouldn't signal eof and set currentLine() to the next() of an iterator
        // that didn't hasNext(), ergo, null
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("hello ;; comment");
        assertEqualsNormal("hello", ti.next());
        assertFalse(ti.hasNext());
    }


    @Test
    public void justInlineComment() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(" ;; comment");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justInlineCommentBarebone() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justInlineComments() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(" ;; comment", "; and another comment", ";;; now for no comment", ";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineComment() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(" #| comment", " and comment still |#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineCommentBarebone() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(" #||#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineCommentBareboneNewlineHugger() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(" #|","","|#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineComments() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(" #| comment", " still another |#", "#| now for no comment|#", "#||#", "");
        assertFalse(ti.hasNext());
    }

    @Test
    public void multilineCommentFinisher() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("lovely #| rita |#");
        assertEqualsNormal("lovely", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void multilineCommentMultilineComment() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("lovely #| #| rita #| |#");
        assertEqualsNormal("lovely", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineBreaksTokensNumbers() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("lovely",
                                          "100",
                                          "meter",
                                          "0.4");
        assertEqualsNormal("lovely", ti.next());
        assertEqualsLiteral(100, ti.next());
        assertEqualsNormal("meter", ti.next());
        assertEqualsFloatLiteral(0.0001, ti.next(), 0.4);
    }

    @Test
    public void normalCharacterLiterals() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("#\\a",
                                          "#\\b",
                                          "#\\c");
        assertEqualsLiteral('a', ti.next());
        assertEqualsLiteral('b', ti.next());
        assertEqualsLiteral('c', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialCharacterLiterals() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("#\\Space",
                                          "#\\Newline",
                                          "#\\Tab");
        assertEqualsLiteral(' ', ti.next());
        assertEqualsLiteral('\n', ti.next());
        assertEqualsLiteral('\t', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringEscapeSequences() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"lovely \\n rita \\t meter\"");
        assertEqualsLiteral("lovely \n rita \t meter", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineTokenSeparators() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("made\nin\theaven");
        assertEqualsNormal("made", ti.next());
        assertEqualsNormal("in", ti.next());
        assertEqualsNormal("heaven", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void tortureTest1() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings
                ("made#\\a\"in\" \t \t\t\t\t",
                        "hea#| ;:::; |#ven",
                        "it's #\\f or     ;;");
        assertEqualsNormal("made", ti.next());
        assertEqualsLiteral('a', ti.next());
        assertEqualsLiteral("in", ti.next());
        assertEqualsNormal("hea", ti.next());
        assertEqualsNormal("ven", ti.next());
        assertEqualsNormal("it", ti.next());
        assertEqualsPunctuation("'", ti.next());
        assertEqualsNormal("s", ti.next());
        assertEqualsLiteral('f', ti.next());
        assertEqualsNormal("or", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testThrowsOnUnclosedComment() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("beginning #|  ", "and no end");
        TokenParsingException tpe = assertThrows(TokenParsingException.class, () -> {ti.next(); ti.next();});
        assertTrue(tpe.getMessage().contains("comment"));
    }

    @Test
    public void testThrowsOnUnclosedString() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\" some string with no end  ");
        TokenParsingException tpe = assertThrows(TokenParsingException.class,
                () -> {
            Token a = ti.next();
        });
        assertTrue(tpe.getMessage().contains("string"));
    }

    @Test
    public void testMultilineStrings() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"lovely rita", "meter maid\"");
        assertEqualsLiteral("lovely rita\nmeter maid", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineStringStartAtEndOfLine() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"", "ok\"");
        assertEqualsLiteral("\nok", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineStringEndAtEndOfLine() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"ok", "\"");
        assertEqualsLiteral("ok\n", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingPoint() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("(+ 10 0.1)");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("+", ti.next());
        assertEqualsLiteral(10, ti.next());
        assertEqualsFloatLiteral(0.1, ti.next(), 0.00001);
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingNothingBeforeDot() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings(".01");
        assertEqualsFloatLiteral(0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingNothingBeforeDotSign() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("-.01");
        assertEqualsFloatLiteral(-0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingSign() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("-0.01");
        assertEqualsFloatLiteral(-0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractInteger() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("100");
        assertEqualsLiteral(100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractIntegerLeadingZeros() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("0100");
        assertEqualsLiteral(100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractIntegerSign() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("-100");
        assertEqualsLiteral(-100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractCharacterLiteral() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("#\\Space",
                    "#\\Tab",
                    "#\\Newline",
                    "#\\Return",
                    "#\\Null"
        );
        assertEqualsLiteral(' ', ti.next());
        assertEqualsLiteral('\t', ti.next());
        assertEqualsLiteral('\n', ti.next());
        assertEqualsLiteral('\r', ti.next());
        assertEqualsLiteral('\0', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringEscapes() {
        NewTokenIterator ti = DebuggingUtils.tokensFromStrings("\"lovely \\\\ rita \\n meter \\t maid \\r\\n\"");
        assertEqualsLiteral("lovely \\ rita \n meter \t maid \r\n", ti.next());
        assertFalse(ti.hasNext());
    }
}

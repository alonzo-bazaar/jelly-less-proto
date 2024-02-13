package org.jelly.parse.token;

import org.jelly.utils.DebuggingUtils;
import org.junit.jupiter.api.Test;
import org.jelly.parse.token.errors.TokenParsingException;

import java.lang.Math;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TokenIteratorTest {

    // repeated assertions, packed for clarity
    <T> boolean literalEquals(LiteralToken<?> t, T target) {
        return t.getVal().equals(target);
    }
    <T> void assertEqualsLiteral(T target, Token t) {
        assertInstanceOf(LiteralToken.class, t);
        assertEquals(target, ((LiteralToken<?>)t).getVal());
    }

    void assertEqualsFloatLiteral(Double target, Token t, double eps) {
        assertInstanceOf(LiteralToken.class, t);
        assertEquals((double)((LiteralToken<?>)t).getVal(), target, eps);
    }

    void assertEqualsNormal(String target, Token t) {
        assertInstanceOf(NormalToken.class, t);
        assertEquals(t.getString(), target);
    }

    void assertEqualsPunctuation(String target, Token t) {
        assertInstanceOf(PunctuationToken.class, t);
        assertEquals(t.getString(), target);
    }


    @Test
    public void justOneChar() {
        TokenIterator nti = DebuggingUtils.tokensFromStrings("a");
        assertEqualsNormal("a", nti.next());
        assertFalse(nti.hasNext());
    }

    @Test
    public void justOneCharMultiline() {
        TokenIterator nti = DebuggingUtils.tokensFromStrings("a", "b");
        assertEqualsNormal("a", nti.next());
        assertEqualsNormal("b", nti.next());
        assertFalse(nti.hasNext());
    }
    @Test
    public void getSpecialNonWhite() {
        TokenIterator nti = DebuggingUtils.tokensFromStrings("(prova)");
        assertEqualsPunctuation("(", nti.next());
        assertEqualsNormal("prova", nti.next());
        assertEqualsPunctuation(")", nti.next());
        assertFalse(nti.hasNext());
    }

    @Test
    public void getSpecialWhite() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("(  prova  )");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("prova", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespace() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("fat       ");
        assertEqualsNormal("fat", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpace() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("      fat");
        assertEqualsNormal("fat", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void leadingWhiteSpaceSpecial() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("      ::");
        assertEqualsPunctuation("::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void trailingWhitespaceSpecial() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(":::       ");
        assertEqualsPunctuation(":::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespace() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("   prova       ");
        assertEqualsNormal("prova", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void bothWhitespaceSpecial() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("   :       ");
        assertEqualsPunctuation(":", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void getsBiggestSpecial() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(":::::::::::");
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation("::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void noCommentAllParen() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("(when (that (fat old sun)) in (the sky))");
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("(when(that(fat:old::sun)):::in::(the#||#sky))");
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("mannaggia:  anna", "\t montemurro", "\t");
        assertEqualsNormal("mannaggia", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("anna", ti.next());
        assertEqualsNormal("montemurro", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialWhite() { // non il dentifricio
        TokenIterator ti = DebuggingUtils.tokensFromStrings("kono: dio :da");
        assertEqualsNormal("kono", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("dio", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("da", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void string() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"stringus\"");
        assertEqualsLiteral("stringus", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringStartingWithWhitespace() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"    stringus\"");
        assertEqualsLiteral("    stringus", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringEndingWithWhitespace() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"stringus      \"");
        assertEqualsLiteral("stringus      ", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringBeforeTokens() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"something\"in the way  ");
        assertEqualsLiteral("something", ti.next());
        assertEqualsNormal("in", ti.next());
        assertEqualsNormal("the", ti.next());
        assertEqualsNormal("way", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringAfterTokens() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("çeci est une\"stringa\"");
        assertEqualsNormal("çeci", ti.next());
        assertEqualsNormal("est", ti.next());
        assertEqualsNormal("une", ti.next());
        assertEqualsLiteral("stringa", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleStrings() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"jojo\" \"bizarre\" \"strings\"");
        assertEqualsLiteral("jojo", ti.next());
        assertEqualsLiteral("bizarre", ti.next());
        assertEqualsLiteral("strings", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void multipleAttachedStrings() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"jojo\"\"bizarre\"\"strings\"");
        assertEqualsLiteral("jojo", ti.next());
        assertEqualsLiteral("bizarre", ti.next());
        assertEqualsLiteral("strings", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringsAndSpecials() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("(strcat\"walu\"\"igi\" \"mannaggia \\\"\" bello bello\"forse\")");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("strcat", ti.next());
        assertEqualsLiteral("walu", ti.next());
        assertEqualsLiteral("igi", ti.next());
        assertEqualsLiteral("mannaggia \"", ti.next());
        assertEqualsNormal("bello", ti.next());
        assertEqualsNormal("bello", ti.next());
        assertEqualsLiteral("forse", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void emptyString() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"\"");
        assertEqualsLiteral("", ti.next());
        assertFalse(ti.hasNext());
    }
    
    @Test
    public void multipleEmptyStrings() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"\"\"\"\"\"");
        assertEqualsLiteral("", ti.next());
        assertEqualsLiteral("", ti.next());
        assertEqualsLiteral("", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void gettingStupid() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("((((((((((((((((((((");
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings(" ((    ((((((((((((((((((  ");
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("()()()()()()()()()()");
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("() ( )()()()(  )()()()() ");
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("ti voglio #| bene |# bastonare");
        assertEqualsNormal("ti", ti.next());
        assertEqualsNormal("voglio", ti.next());
        assertEqualsNormal("bastonare", ti.next());
    }

    @Test
    public void ignoresMultilineComments() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("ti voglio #| bene", " |# bastonare");
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("ei fu ;; siccome", "immobile; (morto)", "<3");
        assertEqualsNormal("ei", ti.next());
        assertEqualsNormal("fu", ti.next());
        assertEqualsNormal("immobile", ti.next());
        assertEqualsNormal("<3", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineBreaksTokensNormal() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("lovely",
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("lovely  ",
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("lovely ;; adjective",
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("hello ;; comment");
        assertEqualsNormal("hello", ti.next());
        assertFalse(ti.hasNext());
    }


    @Test
    public void justInlineComment() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(" ;; comment");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justInlineCommentBarebone() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justInlineComments() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(" ;; comment", "; and another comment", ";;; now for no comment", ";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineComment() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(" #| comment", " and comment still |#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineCommentBarebone() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(" #||#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineCommentBareboneNewlineHugger() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(" #|","","|#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void justMultilineComments() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(" #| comment", " still another |#", "#| now for no comment|#", "#||#", "");
        assertFalse(ti.hasNext());
    }

    @Test
    public void multilineCommentFinisher() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("lovely #| rita |#");
        assertEqualsNormal("lovely", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void multilineCommentMultilineComment() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("lovely #| #| rita #| |#");
        assertEqualsNormal("lovely", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineBreaksTokensNumbers() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("lovely",
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("#\\a",
                                          "#\\b",
                                          "#\\c");
        assertEqualsLiteral('a', ti.next());
        assertEqualsLiteral('b', ti.next());
        assertEqualsLiteral('c', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void specialCharacterLiterals() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("#\\Space",
                                          "#\\Newline",
                                          "#\\Tab");
        assertEqualsLiteral(' ', ti.next());
        assertEqualsLiteral('\n', ti.next());
        assertEqualsLiteral('\t', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void stringEscapeSequences() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"lovely \\n rita \\t meter\"");
        assertEqualsLiteral("lovely \n rita \t meter", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void newlineTokenSeparators() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("made\nin\theaven");
        assertEqualsNormal("made", ti.next());
        assertEqualsNormal("in", ti.next());
        assertEqualsNormal("heaven", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void tortureTest1() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("beginning #|  ", "and no end");
        TokenParsingException tpe = assertThrows(TokenParsingException.class, () -> {ti.next(); ti.next();});
        assertTrue(tpe.getMessage().contains("comment"));
    }

    @Test
    public void testThrowsOnUnclosedString() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\" some string with no end  ");
        TokenParsingException tpe = assertThrows(TokenParsingException.class,
                () -> {
            Token a = ti.next();
        });
        assertTrue(tpe.getMessage().contains("string"));
    }

    @Test
    public void testMultilineStrings() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"lovely rita", "meter maid\"");
        assertEqualsLiteral("lovely rita\nmeter maid", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineStringStartAtEndOfLine() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"", "ok\"");
        assertEqualsLiteral("\nok", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineStringEndAtEndOfLine() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"ok", "\"");
        assertEqualsLiteral("ok\n", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingPoint() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("(+ 10 0.1)");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("+", ti.next());
        assertEqualsLiteral(10, ti.next());
        assertEqualsFloatLiteral(0.1, ti.next(), 0.00001);
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingNothingBeforeDot() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings(".01");
        assertEqualsFloatLiteral(0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingNothingBeforeDotSign() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("-.01");
        assertEqualsFloatLiteral(-0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingSign() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("-0.01");
        assertEqualsFloatLiteral(-0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractInteger() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("100");
        assertEqualsLiteral(100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractIntegerLeadingZeros() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("0100");
        assertEqualsLiteral(100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractIntegerSign() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("-100");
        assertEqualsLiteral(-100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractCharacterLiteral() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("#\\Space",
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
        TokenIterator ti = DebuggingUtils.tokensFromStrings("\"lovely \\\\ rita \\n meter \\t maid \\r\\n\"");
        assertEqualsLiteral("lovely \\ rita \n meter \t maid \r\n", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testCharacterLiteralSpecial() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("#\\(#\\)", "#\\)");
        assertEqualsLiteral('(', ti.next());
        assertEqualsLiteral(')', ti.next());
        assertEqualsLiteral(')', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testCharacterLiteralSpecialEtAl() {
        TokenIterator ti = DebuggingUtils.tokensFromStrings("qualche #\\(", "tt #\\(");
        assertEqualsNormal("qualche", ti.next());
        assertEqualsLiteral('(', ti.next());
        assertEqualsNormal("tt", ti.next());
        assertEqualsLiteral('(', ti.next());
        assertFalse(ti.hasNext());
    }
}

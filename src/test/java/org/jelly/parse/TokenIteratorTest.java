package org.jelly.parse;

import org.jelly.parse.token.*;
import org.junit.jupiter.api.Test;
import org.jelly.parse.token.errors.TokenParsingException;

import static org.junit.jupiter.api.Assertions.*;

public class TokenIteratorTest extends BaseParserTest {

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
    public void testJustOneChar() {
        TokenIterator nti = tokensFromLines("a");
        assertEqualsNormal("a", nti.next());
        assertFalse(nti.hasNext());
    }

    @Test
    public void testJustOneCharMultiline() {
        TokenIterator nti = tokensFromLines("a", "b");
        assertEqualsNormal("a", nti.next());
        assertEqualsNormal("b", nti.next());
        assertFalse(nti.hasNext());
    }
    @Test
    public void testExtractSpecialNonWhite() {
        TokenIterator nti = tokensFromLines("(prova)");
        assertEqualsPunctuation("(", nti.next());
        assertEqualsNormal("prova", nti.next());
        assertEqualsPunctuation(")", nti.next());
        assertFalse(nti.hasNext());
    }

    @Test
    public void testExtractSpecialWhite() {
        TokenIterator ti = tokensFromLines("(  prova  )");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("prova", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testTrailingWhitespace() {
        TokenIterator ti = tokensFromLines("fat       ");
        assertEqualsNormal("fat", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testLeadingWhiteSpace() {
        TokenIterator ti = tokensFromLines("      fat");
        assertEqualsNormal("fat", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testLeadingWhiteSpaceSpecial() {
        TokenIterator ti = tokensFromLines("      ::");
        assertEqualsPunctuation("::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testTrailingWhitespaceSpecial() {
        TokenIterator ti = tokensFromLines(":::       ");
        assertEqualsPunctuation(":::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testBothWhitespace() {
        TokenIterator ti = tokensFromLines("   prova       ");
        assertEqualsNormal("prova", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testBothWhitespaceSpecial() {
        TokenIterator ti = tokensFromLines("   :       ");
        assertEqualsPunctuation(":", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testGetsBiggestSpecial() {
        TokenIterator ti = tokensFromLines(":::::::::::");
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation(":::", ti.next());
        assertEqualsPunctuation("::", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testSymbolsAndParens() {
        TokenIterator ti = tokensFromLines("(when (that (fat old sun)) in (the sky))");
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
    public void testSymbolsAndParensComments() {
        TokenIterator ti = tokensFromLines("(when(that(fat:old::sun)):::in::(the#||#sky)); ok, done");
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
    public void testLotsOfWhitespaces() {
        TokenIterator ti = tokensFromLines("mannaggia:  anna", "\t montemurro", "\t");
        assertEqualsNormal("mannaggia", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("anna", ti.next());
        assertEqualsNormal("montemurro", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testSpecialWhite() { // non il dentifricio
        TokenIterator ti = tokensFromLines("kono: dio :da");
        assertEqualsNormal("kono", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("dio", ti.next());
        assertEqualsPunctuation(":", ti.next());
        assertEqualsNormal("da", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testString() {
        TokenIterator ti = tokensFromLines("\"stringus\"");
        assertEqualsLiteral("stringus", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringStartingWithWhitespace() {
        TokenIterator ti = tokensFromLines("\"    stringus\"");
        assertEqualsLiteral("    stringus", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringEndingWithWhitespace() {
        TokenIterator ti = tokensFromLines("\"stringus      \"");
        assertEqualsLiteral("stringus      ", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringBothEndsWithWhitespace() {
        TokenIterator ti = tokensFromLines("\"  stringus      \"");
        assertEqualsLiteral("  stringus      ", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringBeforeTokens() {
        TokenIterator ti = tokensFromLines("\"something\"in the way  ");
        assertEqualsLiteral("something", ti.next());
        assertEqualsNormal("in", ti.next());
        assertEqualsNormal("the", ti.next());
        assertEqualsNormal("way", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringAfterTokens() {
        TokenIterator ti = tokensFromLines("çeci est une\"stringa\"");
        assertEqualsNormal("çeci", ti.next());
        assertEqualsNormal("est", ti.next());
        assertEqualsNormal("une", ti.next());
        assertEqualsLiteral("stringa", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultipleStrings() {
        TokenIterator ti = tokensFromLines("\"jojo\" \"bizarre\" \"strings\"");
        assertEqualsLiteral("jojo", ti.next());
        assertEqualsLiteral("bizarre", ti.next());
        assertEqualsLiteral("strings", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultipleAttachedStrings() {
        TokenIterator ti = tokensFromLines("\"jojo\"\"bizarre\"\"strings\"");
        assertEqualsLiteral("jojo", ti.next());
        assertEqualsLiteral("bizarre", ti.next());
        assertEqualsLiteral("strings", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringsAndSpecials() {
        TokenIterator ti = tokensFromLines("(strcat\"walu\"\"igi\" \"mannaggia \\\"\" bello bello\"forse\")");
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
    public void testEmptyString() {
        TokenIterator ti = tokensFromLines("\"\"");
        assertEqualsLiteral("", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultipleEmptyStrings() {
        TokenIterator ti = tokensFromLines("\"\"\"\"\"\"");
        assertEqualsLiteral("", ti.next());
        assertEqualsLiteral("", ti.next());
        assertEqualsLiteral("", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testVariousParentheses() {
        TokenIterator ti = tokensFromLines("((((((((((((((((((((");
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
    public void testVariousParenthesesWhitespace() {
        TokenIterator ti = tokensFromLines(" ((    ((((((((((((((((((  ");
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
    public void testVariousMatchingParentheses() {
        TokenIterator ti = tokensFromLines("()()()()()()()()()()");
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
    public void testVariousMatchingParenthesesWhitespace() {
        TokenIterator ti = tokensFromLines("() ( )()()()(  )()()()() ");
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
    public void testIgnoresInlinedMultlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        TokenIterator ti = tokensFromLines("ti voglio #| bene |# bastonare");
        assertEqualsNormal("ti", ti.next());
        assertEqualsNormal("voglio", ti.next());
        assertEqualsNormal("bastonare", ti.next());
    }

    @Test
    public void testIgnoresMultilineComments() {
        TokenIterator ti = tokensFromLines("ti voglio #| bene", " |# bastonare");
        assertEqualsNormal("ti", ti.next());
        assertEqualsNormal("voglio", ti.next());
        assertEqualsNormal("bastonare", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testIgnoresInlineComments() {
        /* visto che si usa un SignificantCharsIterator,
         * questo dovrebbe essere in grado di ignorare i commenti dati
         * controllo giusto in caso
         */
        TokenIterator ti = tokensFromLines("ei fu ;; siccome", "immobile; (morto)", "<3");
        assertEqualsNormal("ei", ti.next());
        assertEqualsNormal("fu", ti.next());
        assertEqualsNormal("immobile", ti.next());
        assertEqualsNormal("<3", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testNewlineBreaksTokensNormal() {
        TokenIterator ti = tokensFromLines("lovely",
                "rita",
                "meter",
                "maid");
        assertEqualsNormal("lovely", ti.next());
        assertEqualsNormal("rita", ti.next());
        assertEqualsNormal("meter", ti.next());
        assertEqualsNormal("maid", ti.next());
    }

    @Test
    public void testNewlineBreaksTokensNormalWhitespace() {
        TokenIterator ti = tokensFromLines("lovely  ",
                "rita",
                "meter ",
                "maid");
        assertEqualsNormal("lovely", ti.next());
        assertEqualsNormal("rita", ti.next());
        assertEqualsNormal("meter", ti.next());
        assertEqualsNormal("maid", ti.next());
    }

    @Test
    public void testNewlineBreaksTokensNormalComments() {
        TokenIterator ti = tokensFromLines("lovely ;; adjective",
                "rita #| name |#",
                "meter",
                "maid ;; because someone thought getting a parking ticket was sexy");
        assertEqualsNormal("lovely", ti.next());
        assertEqualsNormal("rita", ti.next());
        assertEqualsNormal("meter", ti.next());
        assertEqualsNormal("maid", ti.next());
    }



    @Test
    public void testJustInlineComment() {
        TokenIterator ti = tokensFromLines(" ;; comment");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testJustInlineCommentBarebone() {
        TokenIterator ti = tokensFromLines(";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testJustInlineComments() {
        TokenIterator ti = tokensFromLines(" ;; comment", "; and another comment", ";;; now for no comment", ";");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testJustMultilineComment() {
        TokenIterator ti = tokensFromLines(" #| comment", " and comment still |#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testJustMultilineCommentBarebone() {
        TokenIterator ti = tokensFromLines(" #||#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testJustMultilineCommentBareboneNewlineWrapper() {
        TokenIterator ti = tokensFromLines(" #|","","|#");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testJustMultilineComments() {
        TokenIterator ti = tokensFromLines(" #| comment", " still another |#", "#| now for no comment|#", "#||#", "");
        assertFalse(ti.hasNext());
    }

    @Test
    public void testInlineCommentFinisher() {
        // bug earlier where skipInlineComment() wouldn't signal eof and set currentLine() to the next() of an iterator
        // that didn't hasNext(), ergo, null
        TokenIterator ti = tokensFromLines("hello ;; comment");
        assertEqualsNormal("hello", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineCommentFinisher() {
        TokenIterator ti = tokensFromLines("lovely #| rita |#");
        assertEqualsNormal("lovely", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineCommentMultilineComment() {
        TokenIterator ti = tokensFromLines("lovely #| #| rita #| |#");
        assertEqualsNormal("lovely", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testNewlineBreaksTokensNumbers() {
        TokenIterator ti = tokensFromLines("lovely",
                "100",
                "meter",
                "0.4");
        assertEqualsNormal("lovely", ti.next());
        assertEqualsLiteral(100, ti.next());
        assertEqualsNormal("meter", ti.next());
        assertEqualsFloatLiteral(0.0001, ti.next(), 0.4);
    }

    @Test
    public void testNormalCharacterLiterals() {
        TokenIterator ti = tokensFromLines("#\\a",
                "#\\b",
                "#\\c");
        assertEqualsLiteral('a', ti.next());
        assertEqualsLiteral('b', ti.next());
        assertEqualsLiteral('c', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testSpecialCharacterLiterals() {
        TokenIterator ti = tokensFromLines("#\\Space",
                "#\\Newline",
                "#\\Tab");
        assertEqualsLiteral(' ', ti.next());
        assertEqualsLiteral('\n', ti.next());
        assertEqualsLiteral('\t', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testStringEscapeSequences() {
        TokenIterator ti = tokensFromLines("\"lovely \\n rita \\t meter\"");
        assertEqualsLiteral("lovely \n rita \t meter", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testNewlineTokenSeparators() {
        TokenIterator ti = tokensFromLines("made\nin\theaven");
        assertEqualsNormal("made", ti.next());
        assertEqualsNormal("in", ti.next());
        assertEqualsNormal("heaven", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void tortureTest1() {
        TokenIterator ti = tokensFromLines
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
        TokenIterator ti = tokensFromLines("beginning #|  ", "and no end");
        TokenParsingException tpe = assertThrows(TokenParsingException.class, () -> {ti.next(); ti.next();});
        assertTrue(tpe.getMessage().contains("comment"));
    }

    @Test
    public void testThrowsOnUnclosedString() {
        TokenIterator ti = tokensFromLines("\" some string with no end  ");
        TokenParsingException tpe = assertThrows(TokenParsingException.class,
                () -> {
                    Token a = ti.next();
                });
        assertTrue(tpe.getMessage().contains("string"));
    }

    @Test
    public void testMultilineStrings() {
        TokenIterator ti = tokensFromLines("\"lovely rita", "meter maid\"");
        assertEqualsLiteral("lovely rita\nmeter maid", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineStringStartAtEndOfLine() {
        TokenIterator ti = tokensFromLines("\"", "ok\"");
        assertEqualsLiteral("\nok", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testMultilineStringEndAtEndOfLine() {
        TokenIterator ti = tokensFromLines("\"ok", "\"");
        assertEqualsLiteral("ok\n", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingPoint() {
        TokenIterator ti = tokensFromLines("(+ 10 0.1)");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("+", ti.next());
        assertEqualsLiteral(10, ti.next());
        assertEqualsFloatLiteral(0.1, ti.next(), 0.00001);
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingNothingBeforeDot() {
        TokenIterator ti = tokensFromLines(".01");
        assertEqualsFloatLiteral(0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingNothingBeforeDotSign() {
        TokenIterator ti = tokensFromLines("-.01");
        assertEqualsFloatLiteral(-0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractFloatingSign() {
        TokenIterator ti = tokensFromLines("-0.01");
        assertEqualsFloatLiteral(-0.01, ti.next(), 0.00001);
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractInteger() {
        TokenIterator ti = tokensFromLines("100");
        assertEqualsLiteral(100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractIntegerLeadingZeros() {
        TokenIterator ti = tokensFromLines("0100");
        assertEqualsLiteral(100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractIntegerSign() {
        TokenIterator ti = tokensFromLines("-100");
        assertEqualsLiteral(-100, ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testExtractCharacterLiteral() {
        TokenIterator ti = tokensFromLines("#\\Space",
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
        TokenIterator ti = tokensFromLines("\"lovely \\\\ rita \\n meter \\t maid \\r\\n\"");
        assertEqualsLiteral("lovely \\ rita \n meter \t maid \r\n", ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testCharacterLiteralSpecial() {
        TokenIterator ti = tokensFromLines("#\\(#\\)", "#\\)");
        assertEqualsLiteral('(', ti.next());
        assertEqualsLiteral(')', ti.next());
        assertEqualsLiteral(')', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testCharacterLiteralSpecialEtAl() {
        TokenIterator ti = tokensFromLines("qualche #\\(", "tt #\\(");
        assertEqualsNormal("qualche", ti.next());
        assertEqualsLiteral('(', ti.next());
        assertEqualsNormal("tt", ti.next());
        assertEqualsLiteral('(', ti.next());
        assertFalse(ti.hasNext());
    }

    @Test
    public void testQuote() {
        TokenIterator ti = tokensFromLines("(id 'a)");
        assertEqualsPunctuation("(", ti.next());
        assertEqualsNormal("id", ti.next());
        assertEqualsPunctuation("'", ti.next());
        assertEqualsNormal("a", ti.next());
        assertEqualsPunctuation(")", ti.next());
        assertFalse(ti.hasNext());
    }
}

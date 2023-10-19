package lang;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SerializerTest {
    @Test
    public void somethingInteger() {
        LispExpression lil = Serializer.fromToken("20");
        LispValue<Integer> li = (LispValue<Integer>)lil;
        int i = li.get();
        assertEquals(i, 20);
    }

    @Test
    public void somethingNil() {
        assertTrue(Ops.isNil(Serializer.fromToken("nil")));
    }

    @Test
    public void somethingString() {
        LispExpression lil = Serializer.fromToken("\"waluigi\"");
        LispValue<String> li = (LispValue<String>)lil;
        String s = li.get();
        assertEquals(s, "waluigi");
    }

    @Test
    public void somethingStringWithSpaces() {
        LispExpression lil = Serializer.fromToken("\"waluigi time cheater\"");
        LispValue<String> li = (LispValue<String>)lil;
        String s = li.get();
        assertEquals(s, "waluigi time cheater");
    }

    @Test
    public void somethingCharacter() {
        LispExpression lil = Serializer.fromToken("#\\x");
        LispValue<Character> li = (LispValue<Character>)lil;
        char c = li.get();
        assertEquals(c, 'x');
    }

    @Test
    public void somethingFloat() {
        LispExpression lil = Serializer.fromToken("3.1415");
        LispValue<Double> li = (LispValue<Double>)lil;
        double f = li.get();
        assertEquals(f, 3.1415, 0.01);
    }

    @Test
    public void somethingSymbol() {
        LispExpression lil = Serializer.fromToken("kee");
        LispSymbol li = (LispSymbol) lil;
        String s = li.getName();
        assertEquals(s, "kee");
    }
}

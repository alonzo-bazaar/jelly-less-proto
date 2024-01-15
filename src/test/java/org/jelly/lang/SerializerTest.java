package org.jelly.lang;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializerTest {
    @Test
    public void somethingInteger() {
        Object o = Serializer.fromToken("20");
        assertEquals(20, (int)((Integer)o));
    }

    @Test
    public void somethingNil() {
        assertEquals(Constants.NIL, Serializer.fromToken("nil"));
    }

    @Test
    public void somethingString() {
        Object o = Serializer.fromToken("\"waluigi\"");
        assertEquals("waluigi", (String)o);
    }

    @Test
    public void somethingStringWithSpaces() {
        Object o = Serializer.fromToken("\"waluigi time cheater\"");
        assertEquals("waluigi time cheater", (String)o);
    }

    @Test
    public void somethingCharacter() {
        Object o = Serializer.fromToken("#\\x");
        assertEquals('x', (char)((Character)o));
    }

    @Test
    public void somethingFloat() {
        Object o = Serializer.fromToken("3.1415");
        assertEquals(3.1415, (double)((Double)o),  0.01);
    }

    @Test
    public void somethingSymbol() {
        Object o = Serializer.fromToken("kee");
        assertEquals("kee", ((LispSymbol)o).getName());
    }
}

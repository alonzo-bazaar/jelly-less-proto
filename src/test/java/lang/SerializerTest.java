package lang;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializerTest {
    @Test
    public void somethingInteger() {
        Object o = Serializer.fromToken("20");
        assertEquals((int)((Integer)o), 20);
    }

    @Test
    public void somethingNil() {
        assertEquals(Serializer.fromToken("nil"), Constants.NIL);
    }

    @Test
    public void somethingString() {
        Object o = Serializer.fromToken("\"waluigi\"");
        assertEquals((String)o, "waluigi");
    }

    @Test
    public void somethingStringWithSpaces() {
        Object o = Serializer.fromToken("\"waluigi time cheater\"");
        assertEquals((String)o, "waluigi time cheater");
    }

    @Test
    public void somethingCharacter() {
        Object o = Serializer.fromToken("#\\x");
        assertEquals((char)((Character)o), 'x');
    }

    @Test
    public void somethingFloat() {
        Object o = Serializer.fromToken("3.1415");
        assertEquals((double)((Double)o), 3.1415, 0.01);
    }

    @Test
    public void somethingSymbol() {
        Object o = Serializer.fromToken("kee");
        assertEquals(((LispSymbol)o).getName(), "kee");
    }
}

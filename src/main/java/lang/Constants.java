package lang;

public class Constants {
    /* lisp ha delle costanti builtin
     * che a volte uso anche nel codice per boh
     * e non sapevo dove metterle nel package
     */
    public static final NilValue NIL = new NilValue();
    public static final UndefinedValue UNDEFINED = new UndefinedValue();
    public static final Boolean TRUE = Boolean.TRUE;
    public static final Boolean FALSE = Boolean.FALSE;
}

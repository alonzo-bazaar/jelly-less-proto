package lang;

public class Constants {
    /* lisp ha delle costanti builtin
     * che a volte uso anche nel codice per boh
     * e non sapevo dove metterle nel package
     */
    public static final NilValue NIL = new NilValue();
    public static final LispValue<Boolean> T = new LispValue<Boolean>(true);
}

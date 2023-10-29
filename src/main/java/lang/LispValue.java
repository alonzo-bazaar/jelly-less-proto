package lang;

public class LispValue<T extends Object> implements LispExpression {
    /* a lisp value is an expression which you can get/set
     * the type of the get/set object is T
     */
    private T val;

    public LispValue(T val) {
        this.val = val;
    }
    public T get() {
        // poi vedi per la safe copy, non mi ricordo come farla
        return val;
    }
    public void set(T val) {
        this.val = val;
    }
    public void copy(LispValue<T> lv) {
        this.val = lv.get();
    }

    public boolean equals(LispExpression le) {
        if (le instanceof LispValue lv) {
            return val.equals(lv.get());
        }
        return false;
    }

    @Override
    public String toString() {
        String className = val
            .getClass()
            .getSimpleName()
            .substring(0, 3)
            .toLowerCase();
        return className + "(" + val.toString() + ")";
    }
}

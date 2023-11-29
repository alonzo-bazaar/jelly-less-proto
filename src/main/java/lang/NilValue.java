package lang;

public class NilValue implements LispList {
    public NilValue() {
        // niente
    }
    @Override
    public Object getCar() {
        return Constants.NIL;
    }
    @Override
    public Object getCdr() {
        return Constants.NIL; 
    }
    @Override
    public int length() {
        return 0;
    }
    @Override
    public Object nth(int n) {
        return Constants.NIL; 
    }
    @Override
    public Object nthCdr(int n) {
        return Constants.NIL; 
    }
    @Override
    public Object last() {
        return Constants.NIL;
    }
    @Override
    public String toString() {
        return "nil";
    }
}

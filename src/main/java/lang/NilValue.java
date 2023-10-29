package lang;


public class NilValue implements LispList {

    public NilValue() {

        // na sega
    }

    @Override
    public LispExpression getCar() {
        return Constants.NIL;
    }
    @Override
    public LispExpression getCdr() {
        return Constants.NIL; 
    }
    @Override
    public int length() {
        return 0;
    }
    @Override
    public LispExpression nth(int n) {
        return Constants.NIL; 
    }
    @Override
    public LispExpression nthCdr(int n) {
        return Constants.NIL; 
    }
    @Override
    public LispExpression last() {
        return Constants.NIL;
    }

    @Override
    public String toString() {
        return "nil";
    }
}

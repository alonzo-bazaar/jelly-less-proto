package lang;

public interface LispList extends LispExpression {
    public LispExpression getCar();
    public LispExpression getCdr();

    public int length();

    public LispExpression nth(int n);
    public LispExpression nthCdr(int n);

    public LispExpression last();
}

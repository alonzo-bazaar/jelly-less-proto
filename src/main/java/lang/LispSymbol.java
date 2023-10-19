package lang;

public class LispSymbol implements LispExpression {
    private String name;

    public LispSymbol(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

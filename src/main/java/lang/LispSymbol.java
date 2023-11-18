package lang;

public class LispSymbol {
    private String name;
    public LispSymbol(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "symbol(" + name + ")";
    }
}

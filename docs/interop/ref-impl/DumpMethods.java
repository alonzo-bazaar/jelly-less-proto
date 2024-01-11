import java.lang.reflect.*;

public class DumpMethods {
    public static void main(String args[]) {
        try {
            Class c = Class.forName(args[0]);
            Method meths[] = c.getDeclaredMethods();
            for(int i = 0; i<meths.length; ++i) {
                System.out.println(meths[i].toString());
            }
        }
        catch(Throwable t) {
            System.err.println(t);
        }
    }
}

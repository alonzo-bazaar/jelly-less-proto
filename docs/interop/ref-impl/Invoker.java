import java.lang.reflect.*;

public class Invoker {
    public int add(int a, int b) {
        return a+b;
    }

    public static void main(String args[]) {
        try {
            Class<?> cls = Class.forName("Invoker");
            Class<?> paramTypes[] = new Class<?>[2];
            paramTypes[0] = Integer.TYPE;
            paramTypes[1] = Integer.TYPE;
            Method meth = cls.getMethod("add", paramTypes);

            Invoker invoker = new Invoker();
            Object arguments[] = new Object[2];
            arguments[0] = 10;
            arguments[0] = 20;

            Object returned = meth.invoke(invoker, arguments);
            System.out.println(returned);
            System.out.println(returned);
            System.out.println(returned);
            System.out.println(returned);
            System.out.println(returned);
            System.out.println(returned);
            Integer returnedInt = (Integer) returned;
            System.out.println(returnedInt.intValue());
        }
        catch(Throwable t) {
            System.err.println(t);
        }
    }
}

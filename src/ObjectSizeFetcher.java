import java.lang.instrument.Instrumentation;

public class ObjectSizeFetcher {
    private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static void getObjectSize(Object o) {
        System.out.println("El peso del archivo es de "+instrumentation.getObjectSize(o));
    }
}
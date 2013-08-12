package BitemporalSpace.Pricing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class InstrumentFactory {
    public static Object create(final Class name, final Object... args) {
        try {
            final Constructor cd = name.getConstructor(Object[].class);
            return cd.newInstance(new Object[]{args});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}

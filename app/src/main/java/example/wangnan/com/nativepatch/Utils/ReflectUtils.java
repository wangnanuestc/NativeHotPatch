package example.wangnan.com.nativepatch.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by wangnan on 2016/10/6.
 */

public class ReflectUtils {
    private static final String TAG = "ReflectUtils";

    public static Object getField(Object obj, Class<?> cl, String field)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    public static void setField(Object obj, Class<?> cl, String field, Object value)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        localField.set(obj, value);
    }


    /**
     * @param c
     * @param name
     * @param parameterTypes
     * @return
     */
    public static final Method lookupDeclaredMethodRecursive(Class<?> c, String name, Class<?>... parameterTypes) {
        Method m = null;
        do {
            try {
                m = c.getDeclaredMethod(name, parameterTypes);
                break;
            } catch (NoSuchMethodException e) {
                // do nothing continue
            }
        } while ((c = c.getSuperclass()) != null);
        return m;
    }
}

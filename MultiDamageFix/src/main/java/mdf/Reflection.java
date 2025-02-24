package mdf;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reflection {

    private static final Logger logger = LogManager.getLogger(Reflection.class.getName());

    // gain access to private field
    public static Object get(Object obj, Class c, String fieldName) {
        try {
            // must use class where field is originally defined
            Field f = (c == null ? obj.getClass() : c).getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    // gain access to private field
    public static void set(Object obj, Class c, String fieldName, Object value) {
        try {
            // must use class where field is originally defined
            Field f = (c == null ? obj.getClass() : c).getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            logger.error(e);
        }
    }

}

package icr;

import com.megacrit.cardcrawl.cards.AbstractCard;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    // gain access to private field
    public static void setInt(Object obj, Class c, String fieldName, int value) {
        try {
            // must use class where field is originally defined
            Field f = (c == null ? obj.getClass() : c).getDeclaredField(fieldName);
            f.setAccessible(true);
            f.setInt(obj, value);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    // gain access to private method
    public static Object invoke(Object obj, Class c, String methodName, Object... args) {
        try {
            Class<?>[] argClassArray = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                Class argClass = args[i].getClass();
                // we'll make a guess here that the method uses int/float rather than Integer/Float
                argClassArray[i] = (argClass == Integer.class) ? int.class :
                        (argClass == Float.class) ? float.class : argClass;
            }
            // must use class where method is originally defined
            Method m = (c == null ? obj.getClass() : c).getDeclaredMethod(methodName, argClassArray);
            m.setAccessible(true);
            return m.invoke(obj, args);
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

}

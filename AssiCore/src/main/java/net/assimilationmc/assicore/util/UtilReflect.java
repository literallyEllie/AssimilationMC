package net.assimilationmc.assicore.util;

import java.lang.reflect.Field;

public class UtilReflect {

    /**
     * Set a field of a object to a value.
     *
     * @param instance  The object to modify.
     * @param fieldName The field name of the object.
     * @param value     The new value.
     */
    public static void setValue(Object instance, String fieldName, Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set a field of a object to a value in the object's superclass
     *
     * @param instance  The object to modify.
     * @param fieldName The field name of the object.
     * @param value     The new value.
     */
    public static void setSuperValue(Object instance, String fieldName, Object value) {
        try {
            Field field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}

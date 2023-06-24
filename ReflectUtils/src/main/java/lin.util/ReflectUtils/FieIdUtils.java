package lin.util.ReflectUtils;


import java.lang.reflect.Field;
import java.util.HashMap;

public class FieIdUtils {

    private static final HashMap<String, Field> FIELD_CACHE = new HashMap<>();

    public static void setField(Object target, String FieldName, Object value) throws Exception {
        setField(target, target.getClass(), FieldName, value.getClass(), value);
    }

    public static void setField(Object target, String fieldName, Class<?> type, Object value) throws Exception {
        setField(target, target.getClass(), fieldName, type, value);
    }

    public static void setField(Object targetObj,
                                Class<?> CheckClass,
                                String FieldName,
                                Class<?> fieldType,
                                Object value) throws Exception {
        Field field = findField(CheckClass, FieldName, fieldType);
        field.set(targetObj, value);
    }

    public static void setFirstField(Object targetObj, Class<?> findClass, Class<?> fieldType, Object value) throws Exception {
        Field field = findFirstField(findClass, fieldType);
        field.set(targetObj, value);
    }

    public static <T> T getField(Object targetObj, String fieldName, Class<?> fieldType) throws Exception {
        return getField(targetObj, targetObj.getClass(), fieldName, fieldType);
    }

    public static <T> T getFirstField(Object targetObj, Class<?> fieldType) throws Exception {
        return getFirstField(targetObj, targetObj.getClass(), fieldType);
    }

    /**
     * 查找并获取首个类型为fieldType的字段
     */
    public static <T> T getFirstField(Object targetObj, Class<?> findClass, Class<?> fieldType) throws Exception {
        Field field = findFirstField(findClass, fieldType);
        return (T) field.get(targetObj);
    }

    /**
     * 查找并获取字段值
     */
    public static <T> T getField(Object targetObj, Class<?> findClass, String fieldName,
                                 Class<?> fieldType) throws Exception {
        Field field = findField(findClass, fieldName, fieldType);
        return (T) field.get(targetObj);
    }

    /**
     * 查找并获取静态字段
     */
    public static <T> T getStaticFieId(Class<?> findClass, String findName) throws Exception {
        Field field = findUnknownTypeField(findClass, findName);
        return (T) field.get(null);
    }

    /**
     * 查找并获取静态字段
     */
    public static <T> T getStaticFieId(Class<?> findClass, String findName, Class<?> fieldType) throws Exception {
        Field field = findField(findClass, findName, fieldType);
        return (T) field.get(null);
    }

    public static <T> T getUnknownTypeField(Object target, String fieldName) throws Exception {
        Field field = findUnknownTypeField(target.getClass(), fieldName);
        return (T) field.get(target);
    }

    /**
     * 获取未知类型但有字段名的字段
     */
    public static Field findUnknownTypeField(Class<?> findClass, String fieldName) throws Exception {
        String key = findClass.getName() + " " + fieldName;
        if (FIELD_CACHE.containsKey(key)) {
            return FIELD_CACHE.get(key);
        }
        Class<?> Check = findClass;
        while (Check != null) {
            for (Field f : Check.getDeclaredFields()) {
                if (f.getName().equals(fieldName)) {
                    f.setAccessible(true);
                    FIELD_CACHE.put(key, f);
                    return f;
                }
            }
            Check = Check.getSuperclass();
        }
        throw new ReflectException("查找不到未知类型但有字段名的字段 " + key);
    }

    /**
     * 查找首个此类型的字段
     */
    public static Field findFirstField(Class<?> findClass, Class<?> fieldType) {
        String fieldSignText = findClass.getName() + " type= " + fieldType.getName();
        if (FIELD_CACHE.containsKey(fieldSignText)) {
            return FIELD_CACHE.get(fieldSignText);
        }
        Class<?> FindClass = findClass;
        while (FindClass != null) {
            for (Field f : FindClass.getDeclaredFields()) {
                if (f.getType() == fieldType) {
                    f.setAccessible(true);
                    FIELD_CACHE.put(fieldSignText, f);
                    return f;
                }
            }
            FindClass = FindClass.getSuperclass();
        }
        throw new ReflectException("查找不到唯一此类型的字段 : " + fieldSignText);
    }

    public static Field findField(Class<?> findClass, String fieldName, Class<?> fieldType) {
        String fieldSignText = findClass.getName() + " " + fieldType.getName() + " " + fieldName;
        if (FIELD_CACHE.containsKey(fieldSignText)) {
            return FIELD_CACHE.get(fieldSignText);
        }
        Class<?> FindClass = findClass;
        while (FindClass != null) {
            for (Field f : FindClass.getDeclaredFields()) {
                if (f.getName().equals(fieldName) && CheckClassType.CheckClass(f.getType(), (fieldType))) {
                    f.setAccessible(true);
                    FIELD_CACHE.put(fieldSignText, f);
                    return f;
                }
            }
            FindClass = FindClass.getSuperclass();
        }
        throw new ReflectException("查找不到字段 : " + fieldSignText);
    }

}

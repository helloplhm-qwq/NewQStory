package top.linl.dexparser.util;

import java.lang.reflect.Array;

public class ClassUtils {


    public static Class<?> findSimpleTypeArray(String baseClassName) {
        int baseTypeIndex = baseClassName.lastIndexOf('[') + 1;
        Class<?> result = findSimpleType(baseClassName.charAt(baseTypeIndex));
        for (int i = 0; i < baseClassName.length(); i++) {
            char c = baseClassName.charAt(i);
            if (c == '[') {
                result = Array.newInstance(result, 0).getClass();
            } else {
                break;
            }
        }
        return result;
    }

    private static boolean isSimpleArray(String className) {
        int index = className.lastIndexOf('[');
        return index != -1 && className.charAt(index + 1) != 'L';
    }

    /**
     * conversion base type
     *
     * @param simpleType Smali Base Type V,Z,B,I...
     */
    public static Class<?> findSimpleType(char simpleType) {
        switch (simpleType) {
            case 'V':
                return void.class;
            case 'Z':
                return boolean.class;
            case 'B':
                return byte.class;
            case 'S':
                return short.class;
            case 'C':
                return char.class;
            case 'I':
                return int.class;
            case 'J':
                return long.class;
            case 'F':
                return float.class;
            case 'D':
                return double.class;
        }
        throw new RuntimeException("Not an underlying type");
    }
}

package lin.util.ReflectUtils;

import java.lang.reflect.Constructor;

/**
 * 构造方法工具类
 */
public class ConstructorUtils {

    public static <T> T newInstance(Class<?> findClass, Object... params) throws Exception {
        Class<?>[] paramTypes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramTypes[i] = params[i].getClass();
        }
        return newInstance(findClass, paramTypes, params);
    }

    public static <T> T newInstance(Class<?> findClass, Class<?>[] paramTypes, Object... params) throws Exception {
        Constructor<?> constructor = findConstructor(findClass, paramTypes);
        return (T) constructor.newInstance(params);
    }

    /**
     * 查找构造方法
     *
     * @param findClass  类
     * @param paramTypes 构造参数列表
     */
    public static Constructor<?> findConstructor(Class<?> findClass, Class<?>[] paramTypes) {
        //这里就不做构造方法缓存了 使用场景比较少 弄个StringBuilder方便观察日志
        StringBuilder sb = new StringBuilder();
        sb.append(findClass.getName()).append("(");
        for (Class<?> type : paramTypes) sb.append(type.getName()).append(",");
        if (sb.charAt(sb.length() - 1) == ',') sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        Loop:
        for (Constructor<?> con : findClass.getDeclaredConstructors()) {
            Class<?>[] CheckParam = con.getParameterTypes();
            if (CheckParam.length != paramTypes.length) continue;
            for (int i = 0; i < paramTypes.length; i++) {
                if (!CheckClassType.CheckClass(CheckParam[i], paramTypes[i])) continue Loop;
            }
            con.setAccessible(true);
            return con;
        }
        throw new ReflectException("找不到构造方法" + sb);
    }

    public static Constructor<?> findConstructorByParamLength(Class<?> findClass, int length) {
        for (Constructor<?> constructor : findClass.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == length) {
                constructor.setAccessible(true);
                return constructor;
            }
        }
        throw new ReflectException("查找不到指定长度的构造方法 : " + findClass.getName() + " 想要查找的长度 : " + length);
    }

}

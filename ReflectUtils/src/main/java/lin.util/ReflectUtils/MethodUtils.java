package lin.util.ReflectUtils;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 查找和调用方法工具类
 */
public class MethodUtils {
    private static final Map<String, Method> Method_Cache = new HashMap<>();

    public static <T> T callNoParamsMethod(Object obj, String methodName, Class<?> returnType) throws Exception {
        Method m = findNoParamsMethod(obj.getClass(), methodName, returnType);
        return (T) m.invoke(obj);
    }

    public static <T> T callMethod(Object obj, String methodName, Class<?> returnType) throws Exception {
        Method m = findNoParamsMethod(obj.getClass(), methodName, returnType);
        return (T) m.invoke(obj);
    }

    public static <T> T callMethod(Object targetObj, String methodName, Class<?> returnType, Class<?>[] paramTypes, Object... params) throws Exception {
        Method method = findMethod(targetObj.getClass(), methodName, returnType, paramTypes);
        return (T) method.invoke(targetObj, params);
    }

    public static <T> T callStaticNoParamsMethod(Class<?> findClassName, String methodName,
                                                 Class<?> returnType) throws Exception {
        Method m = findNoParamsMethod(findClassName, methodName, returnType);
        return (T) m.invoke(null);
    }

    public static <T> T callStaticMethod(Class<?> findClass, String name, Class<?> ReturnType, Class<?>[] params, Object... param) throws Exception {
        Method m = findMethod(findClass, name, ReturnType, params);
        return (T) m.invoke(null, param);
    }

    public static <T> T callMethodByName(Object targetObj, String name, Object... params) {
        Method m = findMethodByName(targetObj.getClass(), name);
        try {
            return (T) m.invoke(targetObj, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 同下
     */
    public static Method findNoParamsMethod(String findClassName, String MethodName, Class<?> returnType) {
        return findMethod(findClassName, MethodName, returnType, new Class<?>[0]);
    }

    /**
     * 查找无参方法
     */
    public static Method findNoParamsMethod(Class<?> findClass, String MethodName, Class<?> returnType) {
        return findMethod(findClass, MethodName, returnType, new Class<?>[0]);
    }


    /**
     * 查找未知返回方法
     */
    public static Method findUnknownReturnMethod(String className, String methodName,
                                                 Class<?>[] paramsType) {
        return findUnknownReturnMethod(ClassUtils.getClass(className), methodName, paramsType);
    }

    /**
     * 查找未知返回的无参方法
     */
    public static Method findUnknownReturnNoParamMethod(Class<?> findClass, String methodName) {
        return findUnknownReturnMethod(findClass, methodName, new Class[0]);
    }

    /**
     * 查找未知返回类型的方法
     */
    public static Method findUnknownReturnMethod(Class<?> target,
                                                 String methodName,
                                                 Class<?>[] paramsType) {
        StringBuilder sb = new StringBuilder();
        sb.append(target.getName()).append(".").append(methodName).append("(");
        for (Class<?> type : paramsType) sb.append(type.getName()).append(",");
        if (sb.charAt(sb.length()) == ',') sb.delete(sb.length() - 1, sb.length());
        sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        String key = sb.toString();
        if (Method_Cache.containsKey(key)) {
            return Method_Cache.get(key);
        }
        Class<?> Current_Find = target;
        while (Current_Find != null) {
            Loop:
            for (Method method : Current_Find.getDeclaredMethods()) {
                if ((method.getName().equals(methodName) || methodName == null)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == paramsType.length) {
                        for (int i = 0; i < params.length; i++) {
                            if (!Objects.equals(params[i], paramsType[i])) continue Loop;
                        }
                        Method_Cache.put(key, method);
                        method.setAccessible(true);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();//向父类查找
        }

        Current_Find = target;
        while (Current_Find != null) {
            Loop:
            for (Method method : Current_Find.getDeclaredMethods()) {
                if ((method.getName().equals(methodName) || methodName == null)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == paramsType.length) {
                        for (int i = 0; i < params.length; i++) {
                            if (!CheckClassType.CheckClass(params[i], paramsType[i])) continue Loop;
                        }
                        Method_Cache.put(key, method);
                        method.setAccessible(true);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();
        }
        throw new ReflectException("没有查找到未知返回的方法 : " + key);
    }

    public static Method findMethod(String findClassName,
                                    String methodName,
                                    Class<?> returnType,
                                    Class<?>[] paramTypes) {
        Class<?> clz = ClassUtils.getClass(findClassName);
        return findMethod(clz, methodName, returnType, paramTypes);
    }

    /**
     * 查找方法
     */
    public static Method findMethod(Class<?> findClass, String methodName, Class<?> returnType, Class<?>[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(findClass.getName()).append(".").append(methodName).append("(");
        for (Class<?> type : paramTypes) sb.append(type.getName()).append(",");
        if (sb.charAt(sb.length()) == ',') sb.delete(sb.length() - 1, sb.length());
        sb.append(")").append(returnType.getName());
        String signature = sb.toString();
        if (Method_Cache.containsKey(signature)) {
            return Method_Cache.get(signature);
        }
        Class<?> Current_Find = findClass;
        while (Current_Find != null) {
            Loop:
            for (Method method : Current_Find.getDeclaredMethods()) {
                if ((method.getName().equals(methodName) || methodName == null) && method.getReturnType().equals(returnType)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == paramTypes.length) {
                        for (int i = 0; i < params.length; i++) {
                            if (!Objects.equals(params[i], paramTypes[i])) continue Loop;
                            if (!CheckClassType.CheckClass(params[i], paramTypes[i])) continue Loop;
                        }
                        method.setAccessible(true);
                        Method_Cache.put(signature, method);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();//向父类查找
        }

        Current_Find = findClass;
        while (Current_Find != null) {
            Loop:
            for (Method method : Current_Find.getDeclaredMethods()) {
                if ((method.getName().equals(methodName) || methodName == null) && method.getReturnType().equals(returnType)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == paramTypes.length) {
                        for (int i = 0; i < params.length; i++) {
                            if (!CheckClassType.CheckClass(params[i], paramTypes[i])) continue Loop;
                        }
                        method.setAccessible(true);
                        Method_Cache.put(signature, method);
                        return method;
                    }
                }
            }
            Current_Find = Current_Find.getSuperclass();
        }
        throw new ReflectException("没有查找到方法 : " + signature);
    }


    /**
     * 通过方法名查找方法
     * 适用于参数列表变量被混淆的场景
     */
    public static Method findMethodByName(Class<?> clz, String Name) {
        for (Method method : clz.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().equals(Name)) return method;
        }
        return null;
    }


    public static String GetMethodInfoText(Method method) {
        if (method == null) return "方法可能为空没有获取到方法文本信息";
        /*MethodInfo info = GetMethodInfo(method);
        return info.DeclaringClassName+" -> "+info.Signature;*/
        return String.valueOf(method);
    }

    public static MethodInfo GetMethodInfo(Method method) {
        if (MethodInfo.MethodInfoCache.containsKey(method)) {
            return MethodInfo.MethodInfoCache.get(method);
        }
        StringBuilder sb = new StringBuilder();
        //方法签名
        sb.append(Modifier.toString(method.getModifiers()));
        sb.append(" ").append(method.getReturnType().getName());
        if (method.getReturnType().isArray()) sb.append("[]");
        sb.append(" ").append(method.getName());
        sb.append("(");
        //参数类型
        for (Class<?> paramsType : method.getParameterTypes()) {
            sb.append(paramsType.getName()).append(" , ");
        }
        if (sb.toString().endsWith(" , ")) {
            sb.delete(sb.length() - 3, sb.length());
        }
        sb.append(");");
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.DeclaringClassName = method.getDeclaringClass().getName();
        methodInfo.Signature = sb.toString();
        MethodInfo.MethodInfoCache.put(method, methodInfo);
        return methodInfo;
    }

    public static class MethodInfo {
        public static final HashMap<Method, MethodInfo> MethodInfoCache = new HashMap<>();
        public String DeclaringClassName;//com.android.view...
        public String Signature;//public int getInt(Object params);
    }
}

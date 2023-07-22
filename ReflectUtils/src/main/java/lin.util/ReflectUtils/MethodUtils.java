package lin.util.ReflectUtils;


import java.lang.reflect.Method;
import java.util.*;

/**
 * 查找和调用方法工具类
 */
@SuppressWarnings("unused")
public class MethodUtils {
    private static final Map<String, Method> METHOD_CACHE = new HashMap<>();

    /**
     * 调用无参数方法
     *
     * @param targetObj  运行时对象
     * @param methodName 方法名
     * @param returnType 返回类型
     * @return 调用后的方法返回
     */
    public static <T> T callNoParamsMethod(Object targetObj, String methodName, Class<?> returnType) throws Exception {
        Method m = findNoParamsMethod(targetObj.getClass(), methodName, returnType);
        return (T) m.invoke(targetObj);
    }

    /**
     * 调用方法
     *
     * @param targetObj  运行时对象
     * @param methodName 方法名
     * @param paramTypes 参数列表
     * @param returnType 返回类型
     * @param params     调用参数列表
     * @return 调用后的方法返回
     */
    public static <T> T callMethod(Object targetObj, String methodName, Class<?> returnType, Class<?>[] paramTypes, Object... params) throws Exception {
        Method method = findMethod(targetObj.getClass(), methodName, returnType, paramTypes);
        return (T) method.invoke(targetObj, params);
    }

    /**
     * 调用未知返回类型的方法
     * 适用场景 返回类型未知或返回类名混淆
     * public final b<VH> t(@Nullable View.OnClickListener onClickListener)
     *
     * @param targetObj  运行时对象
     * @param methodName 方法名
     * @param paramTypes 参数列表
     * @param params     调用参数列表
     */
    public static <T> T callUnknownReturnTypeMethod(Object targetObj,
                                                    String methodName,
                                                    Class<?>[] paramTypes,
                                                    Object... params) throws Exception {
        Method method = findUnknownReturnTypeMethod(targetObj.getClass(), methodName, paramTypes);
        return (T) method.invoke(targetObj, params);
    }

    /**
     * 调用未知返回类型的无参数方法
     * 适用场景 返回类型未知或返回类名混淆
     *
     * @param targetObj  运行时对象
     * @param methodName 方法名
     */
    public static <T> T callUnknownReturnTypeNoParamMethod(Object targetObj,
                                                           String methodName) throws Exception {
        Method method = findUnknownReturnTypeMethod(targetObj.getClass(), methodName, new Class[0]);
        return (T) method.invoke(targetObj);
    }

    /**
     * 调用静态无参数方法
     *
     * @param findClassName 查找的类名
     * @param methodName    方法名
     * @param returnType    返回类型
     * @return 调用后的方法返回
     */
    public static <T> T callStaticNoParamMethod(Class<?> findClassName, String methodName,
                                                Class<?> returnType) throws Exception {
        Method m = findNoParamsMethod(findClassName, methodName, returnType);
        return (T) m.invoke(null);
    }

    /**
     * 调用静态方法
     *
     * @param findClass  查找的类
     * @param methodName 方法名
     * @param returnType 返回类型
     * @param paramTypes 要查找的方法参数列表
     * @param params     入参
     * @return 调用后的方法返回
     */
    public static <T> T callStaticMethod(Class<?> findClass, String methodName, Class<?> returnType, Class<?>[] paramTypes, Object... params) throws Exception {
        Method m = findMethod(findClass, methodName, returnType, paramTypes);
        return (T) m.invoke(null, params);
    }


    /**
     * 同下
     *
     * @param findClassName 查找的类名
     * @param methodName    方法名
     * @param returnType    返回类型
     */
    public static Method findNoParamsMethod(String findClassName, String methodName, Class<?> returnType) {
        return findMethod(findClassName, methodName, returnType, new Class<?>[0]);
    }

    /**
     * 查找无参数方法
     *
     * @param findClass  查找的类
     * @param methodName 方法名
     * @param returnType 返回类型
     */
    public static Method findNoParamsMethod(Class<?> findClass, String methodName, Class<?> returnType) {
        return findMethod(findClass, methodName, returnType, new Class<?>[0]);
    }

    /**
     * 查找方法
     *
     * @param findClassName 查找的类名
     * @param methodName    方法名
     * @param paramTypes    参数列表
     * @param returnType    返回类型
     */
    public static Method findMethod(String findClassName,
                                    String methodName,
                                    Class<?> returnType,
                                    Class<?>[] paramTypes) {
        Class<?> clz = ClassUtils.getClass(findClassName);
        return findMethod(clz, methodName, returnType, paramTypes);
    }


    /**
     * 查找未知返回方法(以字符串类名)
     *
     * @param className  查找的类名
     * @param methodName 方法名
     */
    public static Method findUnknownReturnTypeMethod(String className,
                                                     String methodName,
                                                     Class<?>[] paramsType) {
        return findUnknownReturnTypeMethod(ClassUtils.getClass(className), methodName, paramsType);
    }

    /**
     * 查找未知返回的无参方法
     *
     * @param findClass  查找的类名
     * @param methodName 方法名
     */
    public static Method findUnknownReturnTypeNoParamMethod(Class<?> findClass, String methodName) {
        return findUnknownReturnTypeMethod(findClass, methodName, new Class[0]);
    }

    /**
     * 查找未知返回类型的方法
     *
     * @param findClass  查找的类名
     * @param methodName 方法名
     * @param paramTypes 参数列表
     */
    public static Method findUnknownReturnTypeMethod(Class<?> findClass,
                                                     String methodName,
                                                     Class<?>[] paramTypes) {
        StringBuilder sb = buildMethodSignature(findClass, methodName, paramTypes);
        String signature = sb.toString();
        if (METHOD_CACHE.containsKey(signature)) {
            return METHOD_CACHE.get(signature);
        }
        for (Class<?> currentFindClass = findClass; currentFindClass != Object.class; currentFindClass = currentFindClass.getSuperclass()) {
            for (Method method : currentFindClass.getDeclaredMethods()) {
                if ((method.getName().equals(methodName) || methodName == null)) {
                    Method currentMethod = verificationAndGetMethod(paramTypes, signature, method);
                    if (currentMethod != null) return currentMethod;
                }
            }
        }
        throw new ReflectException("没有查找到未知类型返回的方法 : " + signature);
    }

    /**
     * 查找方法
     *
     * @param findClass  查找的类
     * @param methodName 方法名
     * @param paramTypes 参数列表
     * @param returnType 返回类型
     */
    public static Method findMethod(Class<?> findClass, String methodName, Class<?> returnType, Class<?>[] paramTypes) {
        StringBuilder sb = buildMethodSignature(findClass, methodName, paramTypes);
        sb.append(returnType.getName());
        String signature = sb.toString();
        if (METHOD_CACHE.containsKey(signature)) {
            return METHOD_CACHE.get(signature);
        }
        for (Class<?> currentFindClass = findClass; currentFindClass != Object.class; currentFindClass = currentFindClass.getSuperclass()) {
            for (Method method : currentFindClass.getDeclaredMethods()) {
                if ((method.getName().equals(methodName) || methodName == null) && method.getReturnType().equals(returnType)) {
                    Method currentMethod = verificationAndGetMethod(paramTypes, signature, method);
                    if (currentMethod != null) return currentMethod;
                }
            }
        }
        throw new ReflectException("没有查找到方法 : " + signature);
    }


    /**
     * 构建方法签名
     *
     * @return com.linl.get(Object, int)的格式
     */
    private static StringBuilder buildMethodSignature(Class<?> findClass, String methodName, Class<?>[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(findClass.getName()).append(".").append(methodName).append("(");
        for (Class<?> type : paramTypes) sb.append(type.getName()).append(",");
        if (sb.charAt(sb.length() - 1) == ',') sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        return sb;
    }

    /**
     * 验证并获取方法
     *
     * @param paramTypes 目的参数列表
     * @param signature  方法签名
     * @param method     对比的方法
     * @return 返回的方法 如果验证不对会返回Null
     */
    private static Method verificationAndGetMethod(Class<?>[] paramTypes, String signature, Method method) {
        Class<?>[] methodParams = method.getParameterTypes();
        if (methodParams.length == paramTypes.length) {
            for (int i = 0; i < methodParams.length; i++) {
                if (!Objects.equals(methodParams[i], paramTypes[i])) return null;
                if (!CheckClassType.CheckClass(methodParams[i], paramTypes[i])) return null;
            }
            method.setAccessible(true);
            METHOD_CACHE.put(signature, method);
            return method;
        }
        return null;
    }

    /**
     * 模糊查找方法
     *
     * @param findClass        要查找的类
     * @param lookupConditions 判断方法是否符合条件
     */
    public static Method[] fuzzyLookupMethod(Class<?> findClass, FuzzyLookupConditions lookupConditions) {
        List<Method> methodList = new ArrayList<>();
        for (Class<?> currentFindClass = findClass; currentFindClass != Object.class; currentFindClass = currentFindClass.getSuperclass()) {
            for (Method method : currentFindClass.getDeclaredMethods()) {
                if (lookupConditions.isItCorrect(method)) {
                    method.setAccessible(true);
                    methodList.add(method);
                }
            }
        }
        if (methodList.isEmpty()) {
            throw new ReflectException("模糊查找方法异常(可能是没有查找到方法) : " + findClass.getName());
        }
        return methodList.toArray(new Method[0]);
    }

    public interface FuzzyLookupConditions {
        boolean isItCorrect(Method currentMethod);
    }

}

package lin.util.ReflectUtils;

import de.robv.android.xposed.XposedHelpers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClassUtils {
    private static final Map<String, Class<?>> Class_Cache = new HashMap<>();
    private static final AtomicBoolean LOADER_IS_INIT = new AtomicBoolean();
    public static ClassLoader moduleLoader;//模块类加载器
    private static ClassLoader hostLoader;//宿主应用类加载器

    /**
     * 排除常用类
     */
    public static boolean isCommonlyUsedClass(String name) {
        return name.startsWith("androidx.") || name.startsWith("android.") ||
                name.startsWith("kotlin.") || name.startsWith("kotlinx.")
                || name.startsWith("com.tencent.mmkv.")
                || name.startsWith("com.android.tools.r8.")
                || name.startsWith("com.google.android.")
                || name.startsWith("com.google.gson.")
                || name.startsWith("com.google.common.")
                || name.startsWith("com.microsoft.appcenter.")
                || name.startsWith("org.intellij.lang.annotations.")
                || name.startsWith("org.jetbrains.annotations.");
    }


    /*
     * 获取类
     */
    public static Class<?> getClass(String className) {
        if (!LOADER_IS_INIT.get())
            throw new ReflectException("宿主类加载器未初始化 请先使用 setHostClassLoader方法初始化 !");
        //类缓存里有这个类就直接返回
        Class<?> clazz = Class_Cache.get(className);
        if (clazz != null) {
            return clazz;
        }
        try {
            if (className.equals("void")) {
                clazz = void.class;
            } else {
                //因为默认的ClassLoader.load() 不能加载"int"这种类型
                clazz = XposedHelpers.findClass(className, hostLoader);
            }
            Class_Cache.put(className, clazz);
            return clazz;
        } catch (Throwable throwable) {
            throw new ReflectException("没有找到类: " + className);
        }
    }

    public static void setHostClassLoader(ClassLoader loader) {
        if (loader == null) throw new ReflectException("类加载器为空 无法设置");
        try {
            loader.loadClass(ClassUtils.class.getName());
        } catch (ClassNotFoundException e) {
            hostLoader = loader;
            LOADER_IS_INIT.set(true);
            return;
        }
        throw new ReflectException("想要设置的类加载器不是宿主的 !");
    }

    public static ClassLoader getHostLoader() {
        return hostLoader;
    }

    public static ClassLoader getModuleLoader() {
        return moduleLoader;
    }

    public static void setModuleLoader(ClassLoader loader) {
        moduleLoader = loader;
    }
}

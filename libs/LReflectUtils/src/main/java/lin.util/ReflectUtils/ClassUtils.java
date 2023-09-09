package lin.util.ReflectUtils;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClassUtils {
    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();
    private static final AtomicBoolean LOADER_IS_INIT = new AtomicBoolean();
    private static final Object[][] baseTypes = {
            {"int", int.class},
            {"boolean", boolean.class},
            {"byte", byte.class},
            {"long", long.class},
            {"char", char.class},
            {"double", double.class},
            {"float", float.class},
            {"short", short.class},
            {"void", void.class}
    };
    public static ClassLoader ModuleLoader;//模块类加载器
    private static ClassLoader HostLoader;//宿主应用类加载器

    /**
     * 获取基本类型
     */
    private static Class<?> getBaseTypeClass(String baseTypeName) {
        for (Object[] baseType : baseTypes) {
            if (baseTypeName.equals(baseType[0])) {
                return (Class<?>) baseType[1];
            }
        }
        throw new ReflectException(baseTypeName + " <-不是基本的数据类型");
    }

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


    /**
     * 获取类
     */
    public static Class<?> getClass(String className) {
        if (!LOADER_IS_INIT.get())
            throw new ReflectException("宿主类加载器未初始化 请先使用 setHostClassLoader方法初始化 !");
        //类缓存里有这个类就直接返回
        Class<?> clazz = CLASS_CACHE.get(className);
        if (clazz != null) {
            return clazz;
        }
        try {
            try {
                clazz = getBaseTypeClass(className);
            } catch (Exception e) {
                //因为默认的ClassLoader.load() 不能加载"int"这种类型
                clazz = HostLoader.loadClass(className);
            }
            CLASS_CACHE.put(className, clazz);
            return clazz;
        } catch (Throwable throwable) {
            throw new ReflectException("没有找到类: " + className);
        }
    }

    public static void setHostClassLoader(ClassLoader loader) {
        if (loader == null) throw new ReflectException("类加载器为Null 无法设置");
        try {
            loader.loadClass(ClassUtils.class.getName());
        } catch (Throwable e) {
            HostLoader = loader;
            LOADER_IS_INIT.set(true);
            return;
        }
        throw new ReflectException("想要设置的类加载器不是宿主的 !");
    }

    public static ClassLoader getHostLoader() {
        return HostLoader;
    }

    public static ClassLoader getModuleLoader() {

        return ModuleLoader;
    }

    public static void setModuleLoader(ClassLoader loader) {
        ModuleLoader = loader;
    }
}

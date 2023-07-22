package lin.xposed.hook.load;


import lin.util.ReflectUtils.FieIdUtils;
import lin.util.ReflectUtils.ReflectException;
import lin.xposed.hook.load.base.BaseHookItem;
import top.linl.annotationprocessor.AnnotationClassNameTools;

import java.util.HashMap;
import java.util.Map;

//扫描hook项目并加载
public class HookItemLoader {

    private static final HashMap<Class<?>, BaseHookItem> HookInstance = new HashMap<>();

    static {
        //扫描hook类
        try {
            ClassLoader classLoader = HookItemLoader.class.getClassLoader();
            if (classLoader == null) throw new ReflectException("HookItemLoader.class.getClassLoader() == null");
            //反射获取由AnnotationProcessor根据注解动态生成的java文件 不会直接出现在源码 只会在编译期自动参与打包
            Class<?> hookItemNameClass = classLoader.loadClass(AnnotationClassNameTools.getClassName());
            Class<?>[] allHookItemClass = FieIdUtils.getStaticFieId(hookItemNameClass, "allHookItemClass", Class[].class);
            for (Class<?> hookItemClass : allHookItemClass) {
                BaseHookItem baseHookItem = (BaseHookItem) hookItemClass.newInstance();
                HookInstance.put(hookItemClass, baseHookItem);
            }
        } catch (Exception e) {
        }
    }

    public static void initHookItem() throws Exception {
        for (Map.Entry<Class<?>, BaseHookItem> hookItemEntry : HookInstance.entrySet()) {
            Class<?> hookItemClass = hookItemEntry.getKey();
            BaseHookItem hookItemInstance = hookItemEntry.getValue();
            try {
                //是默认加载的话那就直接加载(比如设置页入口注入)
                if (hookItemInstance.isLoadedByDefault()) hookItemInstance.loadHook();

                /*
                 * 想做的其他事
                 */

            } catch (Exception e) {
                hookItemInstance.getExceptionCollectionToolInstance().addException(e);
            }
        }
    }

}

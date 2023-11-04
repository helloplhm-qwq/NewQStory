package lin.xposed.hook.item;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import lin.util.ReflectUtils.ClassUtils;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.load.base.BaseHookItem;

@HookItem("保护模块数据不被qq清理")
public class ProtectionModulePathFile extends BaseHookItem {

    @Override
    public boolean isLoadedByDefault() {
        return true;
    }

    @Override
    public void loadHook(ClassLoader loader) throws Exception {

        Class<?> CleanUpClass = ClassUtils.getClass("com.tencent.mobileqq.app.message.cleanup.CleanUpThoroughManager");

        for (Method ms : CleanUpClass.getDeclaredMethods()) {
            ms.setAccessible(true);
            if (ms.getParameterTypes().length >= 6 && ms.getParameterTypes()[0] == File.class) {

                AtomicReference<XC_MethodHook.Unhook> unhook = new AtomicReference<>();
                unhook.set(XposedBridge.hookMethod(ms, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        File file = (File) param.args[0];
                        if (file.getAbsolutePath().contains("Android/data/com.tencent.mobileqq/files")) {
                            for (Field field : CleanUpClass.getDeclaredFields()) {
                                field.setAccessible(true);
                                if (field.getType() != ArrayList.class) continue;
                                ArrayList list = (ArrayList) field.get(param.thisObject);
                                list.removeIf(o -> {
                                    String path = String.valueOf(o);
                                    if (path.contains("QStory")) {
                                        Unhook unhook_ = unhook.getAndSet(null);
                                        if (unhook_ != null) unhook_.unhook();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                });
                            }
                        }
                    }
                }));

            }
            /*if (ms.getReturnType() == ArrayList.class) {
                XposedBridge.hookMethod(ms, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        ArrayList list = (ArrayList) param.getResult();
                        list.removeIf(o -> {
                            String path = String.valueOf(o);
                            return path.contains("QStory");
                        });
                    }
                });
            }*/
        }



    }
}

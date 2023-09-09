package lin.xposed.hook.item;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.HookItem;

@HookItem("净化/实验功能/禁用日志写入的线程")
public class CloseLogWriteThread extends BaseSwitchFunctionHookItem {
    List<String> logLevelNameList = Arrays.asList("w", "i", "d", "q");
    @Override
    public void loadHook(ClassLoader loader) throws Exception {
        Method method = MethodUtils.findMethod("com.tencent.qphone.base.util.QLogItemManager$WriteHandler", "tryInit", void.class, new Class[0]);
        hookBefore(method, param -> {
            Class<?> logClass = ClassUtils.getClass("com.tencent.qphone.base.util.QLog");
            if (logClass != null) {
                for (Method m : logClass.getDeclaredMethods()) {
                    m.setAccessible(true);
                    if (logLevelNameList.contains(m.getName())) {
                        hookBefore(m, param1 -> {
                            if (((Method) param1.method).getReturnType().equals(void.class)) {
                                param1.setResult(null);
                            }
                        });
                    }
                }
            }
            param.setResult(null);
        });
    }
}

package lin.xposed.hook.item;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import lin.util.ReflectUtils.ClassUtils;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.HookItem;

@HookItem("辅助功能/实验功能/平板模式")
public class DeviceTypePad extends BaseSwitchFunctionHookItem {

    @Override
    public String getTips() {
        return "需要重启生效";
    }

    @Override
    public void loadHook(ClassLoader loader) throws Exception {

        XposedHelpers.findAndHookMethod(ClassUtils.getClass("com.tencent.common.config.pad.g"), "d", Context.class,new XC_MethodHook(){
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                Class<?> typeClass = ClassUtils.getClass("com.tencent.common.config.pad.DeviceType");

                Object typeResult = null;

                for (Method method : typeClass.getDeclaredMethods()) {
                    method.setAccessible(true);
                    if (method.getName().equals("valueOf") && method.getReturnType() == typeClass) {
                        Class<?>[] methodParams = method.getParameterTypes();
                        if (methodParams.length == 1 && methodParams[0] == String.class) {
                            typeResult = method.invoke(null, "TABLET");
                        }
                    }
                }

                for (Field field : param.method.getDeclaringClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (field.getType() == typeClass) {
                        field.set(null, typeResult);
                        break;
                    }
                }
            }
        });
    }
}

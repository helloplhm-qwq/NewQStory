package lin.xposed.hook.item;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lin.util.ReflectUtils.ClassUtils;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.load.methodfind.IMethodFinder;
import lin.xposed.hook.load.methodfind.MethodFinder;

@HookItem("辅助功能/实验功能/平板模式")
public class DeviceTypePad extends BaseSwitchFunctionHookItem implements IMethodFinder {

    private Method initDeviceTypeMethod;

    @Override
    public String getTips() {
        return "需要重启生效";
    }

    @Override
    public void loadHook(ClassLoader loader) throws Exception {

        hookAfter(initDeviceTypeMethod, param -> {

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
        });
    }

    @Override
    public void startFind(MethodFinder methodFinder) throws Exception {
        initDeviceTypeMethod = methodFinder.findMethodString("initDeviceType type = ")[0];
        methodFinder.putMethod("initDeviceType type = ", initDeviceTypeMethod);
    }

    @Override
    public void getMethod(MethodFinder finder) {
        initDeviceTypeMethod = finder.getMethod("initDeviceType type = ");
    }
}

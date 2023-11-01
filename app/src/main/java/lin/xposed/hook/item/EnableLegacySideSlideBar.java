package lin.xposed.hook.item;

import java.lang.reflect.Method;

import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.hook.HookEnv;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.QQVersion;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;

@HookItem("净化/侧滑栏/启用旧版侧滑栏")
public class EnableLegacySideSlideBar extends BaseSwitchFunctionHookItem {
    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {
        String methodName = "e";
        if (HookEnv.getVersionCode() < QQVersion.QQ_8_9_35)
            methodName = "f";
        Method method  = MethodUtils.findNoParamsMethod("com.tencent.mobileqq.activity.qqsettingme.utils.a",methodName,boolean.class);
        hookBefore(method,param -> param.setResult(false));
    }
}

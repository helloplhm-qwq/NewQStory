package lin.xposed.hook.item;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.QQVersion;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;

@HookItem("净化/聊天/标题栏/去除标题栏听筒标识")
public class HideChatTitleHandsetIcon extends BaseSwitchFunctionHookItem {
    @Override
    public String getTips() {
        return "当前功能未在QQNT修复 可用状态 : " + (QQVersion.isQQNT() ? "不可用" : "可用");
    }

    @Override
    public void loadHook(ClassLoader loader) throws Exception {

        Method m1 = XposedHelpers.findMethodBestMatch(loader.loadClass("com.tencent.mobileqq.widget.navbar.NavBarAIO"), "setEarIconVisible", boolean.class);
        hookBefore(m1, new HookBehavior() {
            @Override
            public void execute(XC_MethodHook.MethodHookParam param) throws Throwable {
                if ((boolean) param.args[0]) {
                    param.args[0] = false;
                }
            }
        });
    }
}

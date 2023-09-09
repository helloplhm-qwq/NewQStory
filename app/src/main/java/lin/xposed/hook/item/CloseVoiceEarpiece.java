package lin.xposed.hook.item;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.HookItem;

@HookItem("辅助功能/聊天/禁用听语音时听筒来回自动切换")
public class CloseVoiceEarpiece extends BaseSwitchFunctionHookItem {

    @Override
    public void loadHook(ClassLoader loader) throws Exception {

        Method notifyAllDeviceStatusChanged0 = MethodUtils.findMethod("com.tencent.mobileqq.qqaudio.audioplayer.impl.AudioDeviceServiceImpl",
                "notifyAllDeviceStatusChanged", void.class, new Class[]{
                        int.class,
                        boolean.class
                });
        hookBefore(notifyAllDeviceStatusChanged0, new HookBehavior() {
            @Override
            public void execute(XC_MethodHook.MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });
    }
}

package lin.xposed.hook.item;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Method;
import java.util.List;

import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.common.utils.ActivityTools;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;

@HookItem("辅助功能/聊天/查看表情时背景透明")
public class EmojiActivityTransparentBackground extends BaseSwitchFunctionHookItem {
    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {
        Class<?> AIOEmotionFragmentClass = ClassUtils.getClass("com.tencent.mobileqq.emotionintegrate.AIOEmotionFragment");
        Method method = AIOEmotionFragmentClass.getMethod("onResume");
        hookAfter(method, param -> {
            Activity activity = MethodUtils.callUnknownReturnTypeNoParamMethod(param.thisObject, "getActivity");
            List<View> allView = ActivityTools.getAllChildViews(activity);
            for (View v : allView) {
                if (v.getClass() == View.class) {
                    v.setBackground(null);
                    break;
                }
            }
        });
    }
}

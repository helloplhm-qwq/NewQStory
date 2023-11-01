package lin.xposed.hook.item;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;
import java.util.List;

import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.common.utils.ActivityTools;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.QQVersion;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;

@HookItem("辅助功能/聊天/查看图片时背景透明模糊")
public class ChatImageViewTransparentBackground extends BaseSwitchFunctionHookItem {

    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {
        Method method;
        if (QQVersion.isQQNT()) {
            method = MethodUtils.findNoParamsMethod("com.tencent.richframework.gallery.QQGalleryActivity", "onResume", void.class);
        } else {
            method = MethodUtils.findNoParamsMethod("com.tencent.mobileqq.richmediabrowser.AIOGalleryActivity", "onResume", void.class);
        }
        hookAfter(method,param -> {
            Activity activity = (Activity) param.thisObject;
            List<View> ActivityAllView = ActivityTools.getAllChildViews(activity);
            ALLVIEWFOR : for (View view : ActivityAllView) {
                for (int i = 0; i < 3; i++) {
                    View parent = (View) view.getParent();
                    if (parent instanceof RelativeLayout) {
                        continue ALLVIEWFOR;
                    }
                }
                if (!(view instanceof ImageButton))
                    view.setBackground(null);
                /*if (!(view instanceof ImageButton) )
                    view.setBackground(null);
                */
            }
        },2376);
    }


}

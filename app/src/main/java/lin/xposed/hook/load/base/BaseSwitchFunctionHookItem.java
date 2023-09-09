package lin.xposed.hook.load.base;


import android.view.View;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

/**
 * 可以开关的Hook项目
 */
public abstract class BaseSwitchFunctionHookItem extends BaseHookItem {
    private Boolean enabled = false;

    /**
     * @return 提示文字信息
     */
    public String getTips() {
        return null;
    }

    /**
     * @return 点击item view时被调用
     */
    public View.OnClickListener getViewOnClickListener() {
        return null;
    }
    /**
     * 得到功能开关状态
     *
     * @return 开关状态
     */
    public final boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置功能开关状态
     *
     * @param enabled 开关状态
     */
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 如果功能想要自定义Item点击事件时重写此方法
     *
     * @return 点击事件
     */
    public View.OnClickListener getOnClick() {
        return null;
    }

    protected final void hookAfter(Method method, HookBehavior hookBehavior) {
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (isEnabled()) {
                    hookBehavior.execute(param);
                }
            }
        });
    }

    protected final void hookBefore(Method method, HookBehavior hookBehavior) {
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (isEnabled()) {
                    hookBehavior.execute(param);
                }
            }
        });
    }

    protected final void hookBefore(Method method, HookBehavior hookBehavior, int priority) {
        XposedBridge.hookMethod(method, new XC_MethodHook(priority) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (isEnabled()) {
                    hookBehavior.execute(param);
                }
            }
        });
    }

    protected final void hookAfter(Method method, HookBehavior hookBehavior, int priority) {
        XposedBridge.hookMethod(method, new XC_MethodHook(priority) {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (isEnabled()) {
                    hookBehavior.execute(param);
                }
            }
        });
    }

    protected interface HookBehavior {
        void execute(XC_MethodHook.MethodHookParam param) throws Throwable;
    }

}

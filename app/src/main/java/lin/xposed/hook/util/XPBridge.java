package lin.xposed.hook.util;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;

public class XPBridge {

    /**
     * 替换方法
     */
    public static void hookReplace(Member m, replaceMethod replaceMethod) {
        XposedBridge.hookMethod(m, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return replaceMethod.execute(methodHookParam);
            }
        });
    }

    public static void hookClassAllMethodAfter(Class<?> clz, Action action) {
        for (Method method : clz.getDeclaredMethods()) {
            method.setAccessible(true);
            XposedBridge.hookMethod(method, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    action.execute(param);
                }
            });
        }
    }

    public static void hookBefore(Member member, Action action) {
        XposedBridge.hookMethod(member, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                action.execute(param);
            }
        });
    }

    public static void hookBefore(Member member, Action action, int priority) {
        XposedBridge.hookMethod(member, new XC_MethodHook(priority) {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                action.execute(param);
            }
        });
    }

    public static void hookAfter(Member member, Action action, int priority) {
        XposedBridge.hookMethod(member, new XC_MethodHook(priority) {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                action.execute(param);
            }
        });
    }

    public static void hookAfter(Member member, Action action) {
        XposedBridge.hookMethod(member, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                action.execute(param);
            }
        });
    }

    public static void HookAfterOnce(Member member, Action action) {
        AtomicReference<XC_MethodHook.Unhook> unAr = new AtomicReference<>();
        unAr.set(XposedBridge.hookMethod(member, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Unhook unhook = unAr.getAndSet(null);
                if (unhook != null) {
                    unhook.unhook();
                    action.execute(param);
                }
            }
        }));
    }

    public static void HookBeforeOnce(Member member, Action action) {
        AtomicReference<XC_MethodHook.Unhook> unAr = new AtomicReference<>();
        unAr.set(XposedBridge.hookMethod(member, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Unhook unhook = unAr.getAndSet(null);
                if (unhook != null) {
                    unhook.unhook();
                    action.execute(param);
                }
            }
        }));
    }

    public interface replaceMethod {
        Object execute(XC_MethodHook.MethodHookParam param);
    }

    public interface Action {
        void execute(XC_MethodHook.MethodHookParam param) throws Exception;
    }
}


package lin.xposed.hook;

import android.app.Application;
import android.content.Context;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import lin.util.ReflectUtils.ClassUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class InitInject implements IXposedHookLoadPackage
        /*IXposedHookZygoteInit,
        IXposedHookInitPackageResources */ {
    private static final AtomicBoolean Initialized = new AtomicBoolean();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        String packageName = loadPackageParam.packageName;
        if (!loadPackageParam.isFirstApplication) return;//通用的注入可能不需要这样的判断方式
        if (!packageName.matches(HookEnv.HostPackageName)) return;

        //设置当前应用包名
        HookEnv.setCurrentHostAppPackageName(packageName);

        /*不考虑出现锁同步对象空的情况*/
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                //防止App创建服务的时候Application对象重复调用导致的重复注入
                if (Initialized.getAndSet(true)) return;
                //获取和设置全局上下文和类加载器
                HookEnv.setHostAppContext((Context) param.args[0]);
                ClassUtils.setHostClassLoader(HookEnv.getHostAppContext().getClassLoader());

                //加载hook
                HookInit.loadHook();
            }
        });
    }

}

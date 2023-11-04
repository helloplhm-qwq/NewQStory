package lin.xposed.hook;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import lin.util.ReflectUtils.ClassUtils;
import lin.xposed.R;
import top.linl.activity.proxy.ActivityProxyManager;

public class InitInject implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static final AtomicBoolean Initialized = new AtomicBoolean();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws ClassNotFoundException {
        String packageName = loadPackageParam.packageName;
        if (!loadPackageParam.isFirstApplication) return;
        if (!packageName.matches(HookEnv.getTargetPackageName())) return;
        //设置当前应用包名
        HookEnv.setCurrentHostAppPackageName(packageName);

        //设置宿主apk路径
        HookEnv.setHostApkPath(loadPackageParam.appInfo.sourceDir);
        XC_MethodHook initHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //防止App创建serve的时候Application对象重复创建调用导致的重复注入
                if (Initialized.getAndSet(true)) return;
                Application application = (Application) param.thisObject;
                //获取和设置全局上下文和类加载器
                Context appContext = application.getApplicationContext();
                HookEnv.setHostAppContext(appContext);
                ClassUtils.setHostClassLoader(appContext.getClassLoader());
                ClassUtils.setModuleLoader(InitInject.class.getClassLoader());
                //初始化注入活动代理
                ActivityProxyManager.initActivityProxyManager(appContext, HookEnv.getModuleApkPath(), R.string.app_name);
                try {
                    //加载hook
                    HookInit.initMainHook();
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
            }
        };
        Class<?> QFixAppClass = loadPackageParam.classLoader.loadClass("com.tencent.mobileqq.qfix.QFixApplication");

        XposedHelpers.findAndHookMethod(QFixAppClass, "onCreate", initHook);
//        XposedHelpers.findAndHookMethod(QFixAppClass, "attachBaseContext", Context.class, initHook);
//        XposedHelpers.findAndHookMethod(ContextWrapper.class, "onCreate", initHook);
    }


    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        HookEnv.setModuleApkPath(startupParam.modulePath);
    }
}

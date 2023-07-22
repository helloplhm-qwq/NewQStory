package lin.xposed.hook;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XModuleResources;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import lin.util.ReflectUtils.ClassUtils;
import lin.xposed.demo.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class InitInject implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {
    private static final AtomicBoolean Initialized = new AtomicBoolean();

    public static int IconId = 0;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        String packageName = loadPackageParam.packageName;
        if (!loadPackageParam.isFirstApplication) return;
        if (!packageName.matches(HookEnv.HostPackageName)) return;

        //设置当前应用包名
        HookEnv.setCurrentHostAppPackageName(packageName);


        /*不考虑出现锁同步对象空的情况*/
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                //防止App创建serve的时候Application对象重复创建调用导致的重复注入
                if (Initialized.getAndSet(true)) return;

                //获取和设置全局上下文和类加载器
                HookEnv.setHostAppContext((Context) param.args[0]);
                ClassUtils.setHostClassLoader(HookEnv.getHostAppContext().getClassLoader());

                try {
                    //加载hook
                    HookInit.loadHook();
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
            }
        });
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) throws Throwable {
        Resources res = XModuleResources.createInstance(HookEnv.ModuleApkPath, initPackageResourcesParam.res);
        IconId = initPackageResourcesParam.res.addResource(res, R.mipmap.ic_launcher_round);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        HookEnv.ModuleApkPath = startupParam.modulePath;
    }
}

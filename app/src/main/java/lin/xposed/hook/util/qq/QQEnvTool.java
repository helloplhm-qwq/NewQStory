package lin.xposed.hook.util.qq;

import android.annotation.SuppressLint;
import android.content.res.Resources;

import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.hook.HookEnv;

public class QQEnvTool {

    @SuppressLint("DiscouragedApi")
    public static int findResId(String idName) {
        Resources resources = HookEnv.getHostAppContext().getResources();
        return resources.getIdentifier(idName, "id", HookEnv.getCurrentHostPackageName());
    }

    public static long getLongAccountUin() {
        try {
            Object runTime = getAppRuntime();
            if (runTime == null) return 0;
            return MethodUtils.callMethod(runTime, "getLongAccountUin", long.class, new Class[]{});
        } catch (Exception e) {
            return 0;
        }
    }
    public static String getCurrentUin() {
        try {
            Object runTime = getAppRuntime();
            if (runTime == null) return null;
            return MethodUtils.callMethod(runTime, "getCurrentAccountUin", String.class, new Class[]{});
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPSkey(String url) {
        try {
            Object manager = getManager(2);
            return MethodUtils.callMethod(manager, "getPskey", String.class, new Class[]{String.class, String.class}, getCurrentUin(), url);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getManager(int i) {
        try {
            Object runTime = getAppRuntime();
            if (runTime == null) return null;
            return MethodUtils.callMethod(runTime, "getManager", ClassUtils.getClass("mqq.manager.Manager"), new Class[]{int.class}, i);
        } catch (Exception e) {
            return null;
        }
    }
    public static Object getAppRuntime() throws Exception {
        Object sApplication = MethodUtils.callStaticMethod(ClassUtils.getClass("com.tencent.common.app.BaseApplicationImpl"),
                "getApplication", ClassUtils.getClass("com.tencent.common.app.BaseApplicationImpl"), new Class[]{});
        return MethodUtils.callMethod(sApplication, "getRuntime", ClassUtils.getClass("mqq.app.AppRuntime"), new Class[]{});
    }
}

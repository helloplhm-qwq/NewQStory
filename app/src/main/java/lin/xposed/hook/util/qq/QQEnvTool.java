package lin.xposed.hook.util.qq;

import android.util.Log;

import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.MethodUtils;

public class QQEnvTool {

    public static String getCurrentUin() {
        try {
            Object runTime = getAppRuntime();
            if (runTime == null) return null;
            return MethodUtils.callMethod(runTime, "getCurrentAccountUin", String.class, new Class[]{});
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

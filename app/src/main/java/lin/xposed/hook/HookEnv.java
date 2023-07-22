package lin.xposed.hook;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("StaticFieldLeak")
public class HookEnv {


    //目标包名 如果通用填.+
    public static final String HostPackageName = ".+";
    public static String currentHostAppPackageName;

    public static String ModuleApkPath;
    private static Context hostAppContext;

    public static String getCurrentHostAppPackageName() {
        return currentHostAppPackageName;
    }

    public static void setCurrentHostAppPackageName(String currentHostAppPackageName) {
        HookEnv.currentHostAppPackageName = currentHostAppPackageName;
    }

    public static Context getHostAppContext() {
        return hostAppContext;
    }

    public static void setHostAppContext(Context hostAppContext) {
        HookEnv.hostAppContext = hostAppContext;
    }
}

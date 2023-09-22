package lin.app.main;

import android.content.Context;

import lin.xposed.BuildConfig;
import lin.xposed.R;

public class ModuleBuildInfo {
    public static String moduleName;
    public static String BuildTime;
    public static String modulePath;
    public static String modulePackageName = BuildConfig.APPLICATION_ID;
    public static String moduleVersionName = BuildConfig.VERSION_NAME;
    public static int moduleVersion  = BuildConfig.VERSION_CODE;

    public static boolean init(Context context) {
        moduleName = context.getString(R.string.app_name);

        return true;
    }
}

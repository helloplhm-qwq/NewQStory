package top.linl.activity.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import dalvik.system.DexClassLoader;
import top.linl.activity.proxy.Info;

public class ActivityUtils {
    /**
     * 注入模块的Res资源到上下文中
     *
     * @param context 要注入的上下文
     */
    public static void injectResourcesToContext(Context context) {
        Resources resources = context.getResources();
        try {
            //如果能获取到自己的资源说明是自己的Activity或已经注入过了
            resources.getString(Info.resID);
        } catch (Exception e) {
            try {
                AssetManager assetManager = resources.getAssets();
                @SuppressLint("DiscouragedPrivateApi")
                Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
                method.invoke(assetManager, Info.ModuleApkPath);
            } catch (Exception ex) {
                throw new RuntimeException("将Resources资源注入到 " + context.getClass().getName() + " 失败 " + ex);
            }
        }
    }

    /**
     * 获取所有声明在AndroidManifest中已经有注册过的activityInfo
     *
     * @param context 上下文
     */
    public static ActivityInfo[] getAllActivity(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            //所有的Activity
            ActivityInfo[] activities = packageInfo.activities;
            return activities;

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * 获取当前正在运行的Activity
     */
    @SuppressLint("PrivateApi")
    public static Activity getActivity() {
        Class<?> activityThreadClass;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            //获取当前活动线程
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            @SuppressLint("DiscouragedPrivateApi")
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            //获取线程Map
            Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
            if (activities == null) return null;
            for (Object activityRecord : activities.values()) {
                Class<?> activityRecordClass = activityRecord.getClass();
                //获取暂停状态
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                //不是暂停状态的话那就是当前正在运行的Activity
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    /*
     * 结束进程
     */
    public static void killAppProcess(Context context) {
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList) {
            if (runningAppProcessInfo.pid != android.os.Process.myPid()) {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}

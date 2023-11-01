package lin.xposed.common.config;

import android.content.Context;
import android.content.SharedPreferences;

import lin.xposed.hook.HookEnv;

public class GlobalConfig {
    public static final String FileName = "NewQStory";

    public static void remove(String key) {
        SharedPreferences share = getAppContext().getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.remove(key);
        editor.apply();
    }

    public static String getString(String key) {
        SharedPreferences share = getAppContext().getSharedPreferences(FileName, 0);
        return share.getString(key, "");
    }

    public static void putString(String key, String value) {
        SharedPreferences share = getAppContext().getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putBoolean(String key, Boolean value) {
        SharedPreferences share = getAppContext().getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key) {
        SharedPreferences share = getAppContext().getSharedPreferences(FileName, 0);
        return share.getBoolean(key, false);
    }

    public static void putInt(String key, int i) {
        SharedPreferences share = getAppContext().getSharedPreferences(FileName, 0);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(key, i);
        editor.apply();
    }

    public static int getInt(String key) {
        SharedPreferences share = getAppContext().getSharedPreferences(FileName, 0);
        return share.getInt(key, 0);
    }

    private static Context getAppContext() {
        return HookEnv.getHostAppContext();
    }
}

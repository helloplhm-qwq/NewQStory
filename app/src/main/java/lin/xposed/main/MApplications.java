package lin.xposed.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

@SuppressLint("StaticFieldLeak")
public class MApplications extends Application {

    private static Context globalContext;

    public static Context getGlobalContext() {
        return globalContext;
    }

    @Override
    public void onCreate() {
        // 程序创建的时候执行
        super.onCreate();
        globalContext = this.getApplicationContext();
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //配置更改的时候执行
        super.onConfigurationChanged(newConfig);
    }
}

package lin.xposed.hook.util;

import android.content.Context;
import android.os.Environment;

import lin.xposed.common.utils.GlobalConfig;
import lin.xposed.hook.HookEnv;

public class PathTool {

    public static String getDataSavePath(Context context, String dirName) {
        //getExternalFilesDir()：SDCard/Android/data/你的应用的包名/files/dirName
        return context.getExternalFilesDir(dirName).getAbsolutePath();
    }

    public static String getStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getModuleDataPath() {
        String dataPath = GlobalConfig.getString("DataPath");
        if (!dataPath.equals("")) return dataPath;
        return getDataSavePath(HookEnv.getHostAppContext(), "QStory");
    }

    public static void updateDataPath(String path) {
        GlobalConfig.putString("DataPath", path);
    }
}

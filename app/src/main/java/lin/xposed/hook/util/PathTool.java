package lin.xposed.hook.util;

import android.content.Context;
import android.os.Environment;

public class PathTool {
    public static final String ABSOLUTE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static String getDataSavePath(Context context, String dirName) {
        return context.getExternalFilesDir(dirName).getAbsolutePath();
    }

    public static String getModuleDataPath() {
        return ABSOLUTE_PATH + "/QStory";
    }
}

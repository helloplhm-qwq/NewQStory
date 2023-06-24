package lin.xposed.hook.util;


import lin.xposed.common.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LogUtils {
    //日志根目录 需以目录分隔符结束
    private static final String LOG_ROOT_DIRECTORY = PathTool.getModuleDataPath() + "/Log/";

    private static String getRunLogDirectory() {
        return LOG_ROOT_DIRECTORY + "RunLog" + File.separator;
    }

    private static String getErrorLogDirectory() {
        return LOG_ROOT_DIRECTORY + "ErrorLog" + File.separator;
    }

    public static String getStackTrace(Exception exception) {
        StringBuilder sb = new StringBuilder();
        sb.append(exception).append("\n");
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append(stackTraceElement).append("\n");
        }
        return sb.toString();
    }

    public static void addError(Exception e) {
        addError("Error Log", e.toString(), e);
    }

    public static void addRunLog(Object content) {
        addRunLog("Run Log", content);
    }

    /**
     * 记录运行日志 确保能走到那一行代码
     *
     * @param TAG(文件名) 内容
     */
    public static void addRunLog(String TAG, Object content) {
        addLog(TAG, content.toString(), content, false);
    }

    /**
     * 记录异常
     */
    public static void addError(String TAG, Exception e) {
        addLog(TAG, e.toString(), e, true);
    }

    /**
     * 记录异常
     *
     * @param TAG         (标签 文件名)
     * @param Description 错误的相关描述
     * @param content     Exception
     */
    public static void addError(String TAG, String Description, Throwable content) {
        addLog(TAG, Description, content, true);
    }


    private static void addLog(String fileName, String Description, Object content, boolean isError) {
        new Thread(() -> {

            String path = (isError ? getErrorLogDirectory() : getRunLogDirectory()) + fileName + ".txt";
            StringBuilder stringBuffer = new StringBuilder(getTime());
            stringBuffer.append("\n").append(Description);
            if (content instanceof Exception) {
                stringBuffer.append("\n").append(getStackTrace((Exception) content));
            }
            stringBuffer.append("\n\n");
            FileUtils.writeTextToFile(path, stringBuffer.toString(), true);
        }).start();
    }

    public static String getTime() {
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy年MM月dd日"),
                df2 = new SimpleDateFormat("E", Locale.CHINA),
                df3 = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String TimeMsg1 = df1.format(calendar.getTime()),
                TimeMsg2 = df2.format(calendar.getTime()),
                TimeMsg3 = df3.format(calendar.getTime());
        if (TimeMsg1.contains("年0"))//去掉多余的 0
            TimeMsg1 = TimeMsg1.replace("年0", "年");
        if (TimeMsg1.contains("月0"))
            TimeMsg1 = TimeMsg1.replace("月0", "月");
        if (TimeMsg2.contains("周"))
            TimeMsg2 = TimeMsg2.replace("周", "星期");//转换为星期
        return TimeMsg1 + TimeMsg2 + TimeMsg3;
    }
}

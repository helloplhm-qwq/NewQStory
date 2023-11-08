package lin.xposed.hook.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import lin.util.ReflectUtils.MethodUtils;

public class ReadWriteRequestUtil {
    private static final int WRITE_REQUEST_CODE = 2376738;
    private Activity activity;
    private String dir;
    private OnActivityResult onActivityResult;
    public ReadWriteRequestUtil(Activity activity) {
        this.activity = activity;
    }

    /**
     * 打开目录
     *
     * @param dirName 该参数为/storage/emulated/0/下的文件夹名
     */
    public void openDirectoryName(String dirName) {
        //注册回调
        hookResults(activity.getClass());
        File dirFile = new File(Environment.getExternalStorageDirectory() + "/" + dirName);
        if (! dirFile.exists()) dirFile.mkdirs();
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:"+dirName);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
        activity.startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    /**
     * 获取用户最终选择的文件夹 该属性在用户选择了文件夹触发onActivityResult才会不为null
     */
    public String getFinalDir() {
        return this.dir;
    }

    /**
     * 设置请求后的回调 在这个回调被调用时 {@link #getFinalDir()} 才能调用 否则为空
     *
     * @param onActivity 回调
     */
    public void setOnActivity(OnActivityResult onActivity) {
        this.onActivityResult = onActivity;
    }
    /**
     * @return 判断目录是否可写
     */
    public boolean isWritable() {
        File nomedia = new File(this.dir ,".nomedia");
        if (nomedia.exists()) nomedia.delete();
        try {
            return nomedia.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    private void hookResults(Class<?> clz) {
        Method m = MethodUtils.findUnknownReturnTypeMethod(clz.getName(), "onActivityResult", new Class[]{int.class, int.class, Intent.class});
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int requestCode = (int) param.args[0];
                int resultCode = (int) param.args[1];
                Intent data = (Intent) param.args[2];
                hookOnActivityResult(requestCode, resultCode, data);
            }
        });
    }

    /**
     * 使用完请求框架的回调
     *
     * @param requestCode 请求代码 自定义的
     * @param resultCode 是否成功的代码
     * @param data 数据
     */
    protected void hookOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != Activity.RESULT_OK) return;
        if (requestCode == WRITE_REQUEST_CODE) {
            Uri uri = data.getData();
            this.dir = uri.getPath().replace("/tree/primary:", Environment.getExternalStorageDirectory() + "/");
            if (this.onActivityResult != null) onActivityResult.onActivityResult(data);
        }
    }

    public interface OnActivityResult {
        void onActivityResult(Intent intent);
    }
}

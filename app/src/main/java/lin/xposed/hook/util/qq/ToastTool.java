package lin.xposed.hook.util.qq;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import lin.xposed.hook.HookEnv;

public class ToastTool {
    public static void show(Object content) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(HookEnv.getHostAppContext(), String.valueOf(content), Toast.LENGTH_LONG).show();
            }
        });
    }
}

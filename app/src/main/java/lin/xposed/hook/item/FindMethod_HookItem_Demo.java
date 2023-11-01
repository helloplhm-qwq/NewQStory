package lin.xposed.hook.item;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import lin.util.ReflectUtils.FieIdUtils;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.util.LogUtils;

public class FindMethod_HookItem_Demo extends BaseSwitchFunctionHookItem {

    @Override
    public void loadHook(ClassLoader loader) throws Exception {

        Class<?> keyC = loader.loadClass("oicq.wlogin_sdk.tools.EcdhCrypt");
        XposedBridge.hookMethod(keyC.getMethod("get_g_share_key"), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String result = bytesToHex((byte[]) param.getResult());

            }
        });
        XposedBridge.hookMethod(keyC.getMethod("get_c_pub_key"), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String result = bytesToHex((byte[]) param.getResult());
            }
        });
        XposedHelpers.findAndHookMethod("oicq.wlogin_sdk.request.WUserSigInfo", loader, "get_clone", loader.loadClass("oicq.wlogin_sdk.sharemem.WloginSigInfo"), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object result = param.args[0];
                String TGTKey = bytesToHex(FieIdUtils.getField(result, "_TGTKey", byte[].class));
                LogUtils.addRunLog("TGTKEY : " + TGTKey);
            }
        });

    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }

}

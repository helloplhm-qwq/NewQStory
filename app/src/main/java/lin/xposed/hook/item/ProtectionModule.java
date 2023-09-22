package lin.xposed.hook.item;

import java.io.File;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;

@HookItem("辅助功能/实验功能/保护模块数据不被qq清理")
public class ProtectionModule extends BaseSwitchFunctionHookItem {
    @Override
    public String getTips() {
        return "防止本模块和其他模块的数据目录被QQ当缓存清掉 qstory qa_mmkv xa_mmkv";
    }

    @Override
    public void loadHook(ClassLoader loader) throws Exception {
        Method method = File.class.getMethod("delete");
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                File file = (File) param.thisObject;
                String path = file.getAbsolutePath();
                if (path.contains("QStory") || path.contains("qa_mmkv") || path.contains("xa_mmkv") ) {
                    param.setResult(true);
                }
            }
        });
    }
}

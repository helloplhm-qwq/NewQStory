package lin.xposed.hook.item;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.load.base.BaseHookItem;

@HookItem("保护模块数据不被qq清理")
public class ProtectionModule extends BaseHookItem {

    @Override
    public boolean isLoadedByDefault() {
        return true;
    }

    @Override
    public void loadHook(ClassLoader loader) throws Exception {

        for (Method m : File.class.getMethods()) {
            if (m.getReturnType().isArray()) {
                if (m.getReturnType() == File[].class) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            File[] result = (File[]) param.getResult();
                            if (result == null) return;
                            boolean isContains = false;
                            for (File file : result) {
                                if (file.getAbsolutePath().contains("QStory")) {
                                    isContains = true;
                                    break;
                                }
                            }
                            if (isContains) {
                                List<File> fileList = Arrays.asList(result);
                                fileList.removeIf(file -> file.getAbsolutePath().contains("QStory"));
                                param.setResult(fileList.toArray(new File[0]));
                            }
                        }
                    });
                } else if (m.getReturnType() == String[].class) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            String[] pathList = (String[]) param.getResult();
                            if (pathList == null) return;
                            boolean isContains = false;
                            for (String file : pathList) {
                                if (file.contains("QStory")) {
                                    isContains = true;
                                    break;
                                }
                            }
                            if (isContains) {
                                List<String> fileList = Arrays.asList(pathList);
                                fileList.removeIf(file -> file.contains("QStory"));
                                param.setResult(fileList.toArray(new String[0]));
                            }
                        }
                    });
                }
            }
        }
    }
}

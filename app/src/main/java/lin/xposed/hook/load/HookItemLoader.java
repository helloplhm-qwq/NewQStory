package lin.xposed.hook.load;


import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import de.robv.android.xposed.XposedBridge;
import lin.app.main.ModuleBuildInfo;
import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.FieIdUtils;
import lin.util.ReflectUtils.ReflectException;
import lin.widget.dialog.SimpleLoadingDialog;
import lin.xposed.BuildConfig;
import lin.xposed.common.config.SimpleConfig;
import lin.xposed.common.utils.ActivityTools;
import lin.xposed.common.utils.FileUtils;
import lin.xposed.hook.HookEnv;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.load.base.BaseHookItem;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.load.methodfind.IMethodFinder;
import lin.xposed.hook.load.methodfind.MethodFinder;
import lin.xposed.hook.util.LogUtils;
import lin.xposed.hook.util.PathTool;
import lin.xposed.hook.util.qq.ToastTool;
import top.linl.annotationprocessor.AnnotationClassNameTools;
import top.linl.dexparser.DexFinder;

//扫描hook项目并加载
public class HookItemLoader {

    public static final HashMap<Class<?>, BaseHookItem> HookInstance = new LinkedHashMap<>();
    /**
     * 是否方法查找期
     */
    public static final AtomicBoolean isMethodFindPeriod = new AtomicBoolean();

    /**
     * 方法数据已更新
     */
    private static final AtomicBoolean methodDataUpdate = new AtomicBoolean();
    private static Handler mHandler;

    static {

        //扫描hook类
        try {
            ClassLoader classLoader = HookItemLoader.class.getClassLoader();
            if (classLoader == null)
                throw new ReflectException("HookItemLoader.class.getClassLoader() == null");
            //反射获取由AnnotationProcessor根据注解动态生成的java文件 不会直接出现在源码 只会在编译期自动参与打包
            Class<?> hookItemNameClass = classLoader.loadClass(AnnotationClassNameTools.getClassName());
            Class<?>[] allHookItemClass = FieIdUtils.getStaticFieId(hookItemNameClass, "allHookItemClass", Class[].class);
            for (Class<?> hookItemClass : allHookItemClass) {
                //new出hookitem
                BaseHookItem baseHookItem = (BaseHookItem) hookItemClass.newInstance();

                //获取注解和信息并初始化 GetAnnotationInfo and initItemPath
                HookItem annotation = hookItemClass.getAnnotation(HookItem.class);
                assert annotation != null;
                String itemPath = annotation.value();
                baseHookItem.initItemPath(itemPath);
                baseHookItem.setHasUiPath(annotation.hasPath());

                HookInstance.put(hookItemClass, baseHookItem);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 初始化hook类
     */
    public static void initHookItem() {

        //对所有的项目做想做的事
        for (Map.Entry<Class<?>, BaseHookItem> hookItemEntry : HookInstance.entrySet()) {
            Class<?> hookItemClass = hookItemEntry.getKey();
            BaseHookItem hookItemInstance = hookItemEntry.getValue();
            try {
                /*
                 * 是默认加载的话那就直接加载(比如设置页入口注入)
                 * (前提不是可开关的功能类 不然关闭了还加载就...)
                 * Class.isAssignableFrom判断左边是不是右边的父类
                 */
                if (hookItemInstance.isLoadedByDefault() && !BaseSwitchFunctionHookItem.class.isAssignableFrom(hookItemClass)) {
                    hookItemInstance.loadHook(ClassUtils.getHostLoader());
                }
            } catch (Exception e) {
                hookItemInstance.getExceptionCollectionToolInstance().addException(e);
            }
        }
        SettingLoader.loadSetting();//加载本地数据
    }

    public static void startFindAllMethod(Activity activity) {
        if (isMethodFindPeriod.getAndSet(true)) return;
        SimpleLoadingDialog loadingDialog = new SimpleLoadingDialog(activity);
        loadingDialog.setCanceledOnTouchOutside(false);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {  //处理线程中handler发送的消息
                super.handleMessage(msg);
                String op = (String) msg.obj;
                switch (op) {
                    case "END":
                        loadingDialog.dismiss();
                        break;
                    case "START":
                        loadingDialog.show();
                        break;
                    default:
                        loadingDialog.setTitle(op);
                }

            }
        };
        loadingDialog.setTitle("开始初始化");
        new Thread(() -> {
            sendMsgToDialog("START");
            sendMsgToDialog("读取旧数据");
            SimpleConfig config = new SimpleConfig("BaseConfig");
            try {
                config.put("startTime", LogUtils.getTime());
                JSONObject json = new JSONObject();
                sendMsgToDialog("初始化中(LDexParser)...");

                DexFinder dexFinder = new DexFinder.Builder(ClassUtils.getHostLoader(), HookEnv.getHostApkPath())
                        .setCachePath(PathTool.getModuleDataPath() + "/MethodFinderCache")
                        .setOnProgress(new DexFinder.OnProgress() {
                            @Override
                            public void init(int dexSize) {
                                new Handler(Looper.getMainLooper()).post(() -> loadingDialog.progressBar.setMax(dexSize));
                            }
                            @Override
                            public void parse(int progress,String dexName) {
                                new Handler(Looper.getMainLooper()).post(() -> loadingDialog.progressBar.setProgress(progress));
                            }
                        })
                        .build();
                sendMsgToDialog("初始化完成 开始查找方法...");
                AtomicInteger progress = new AtomicInteger();
                new Handler(Looper.getMainLooper()).post(() -> loadingDialog.progressBar.setMax(HookInstance.size()));
                for (BaseHookItem hookItem : HookInstance.values()) {

                    new Handler(Looper.getMainLooper()).post(() -> loadingDialog.progressBar.setProgress(progress.getAndIncrement()));
                    sendMsgToDialog("当前处理的类 : " + hookItem.getClass().getName());

                    if (!BaseSwitchFunctionHookItem.class.isAssignableFrom(hookItem.getClass())) {
                        continue;
                    }
                    if (hookItem instanceof IMethodFinder iMethodFinder) {
                        //start find method
                        MethodFinder finder = new MethodFinder(hookItem.getClass(), dexFinder);
                        iMethodFinder.startFind(finder);//收集想要查找的方法信息
                        json.put(hookItem.getClass().getName(), finder.getResults());
                    }
                }
                sendMsgToDialog("所有方法查找完成 准备保存与重启");

                FileUtils.writeTextToFile(PathTool.getModuleDataPath() + "/data/MethodCache", json.toString(), false);
                //find end
                dexFinder.close();

                isMethodFindPeriod.set(false);
                methodDataUpdate.set(true);
                config.put("moduleVersionAndHostAppVersion", ModuleBuildInfo.moduleVersionName + ":" + HookEnv.getVersionName() + ":" + BuildConfig.BUILD_TYPE);
                config.put("time", LogUtils.getTime());
                sendMsgToDialog("END");
            } catch (Exception e) {
                XposedBridge.log(e);
                LogUtils.addError(e);
            } finally {
                config.submit();
            }
            ActivityTools.killAppProcess(HookEnv.getHostAppContext());
        }).start();

    }

    private static void sendMsgToDialog(String s) {
        Message message = new Message();
        message.obj = s;
        mHandler.sendMessage(message);
    }

    public static void scanMethod() throws Exception {
        //从本地扫描方法
        JSONObject methodData = new JSONObject(FileUtils.readFileText(PathTool.getModuleDataPath() + "/data/MethodCache"));
        for (BaseHookItem hookItem : HookInstance.values()) {
            if (!BaseSwitchFunctionHookItem.class.isAssignableFrom(hookItem.getClass())) continue;
            if (hookItem instanceof IMethodFinder iMethodFinder) {
                try {
                    //再运行一次方法查找器来让项可以得到方法
                    JSONObject classMethodData = methodData.getJSONObject(hookItem.getClass().getName());
                    MethodFinder finder = new MethodFinder(hookItem.getClass(), null);
                    finder.loadAllMethod(classMethodData);
                    iMethodFinder.startFind(finder);
                } catch (Exception e) {
                    hookItem.getExceptionCollectionToolInstance().addException(e);
                }
            }
        }
    }

    public static boolean methodDataIsOutOfDate() {
        if (methodDataUpdate.get()) {
            return methodDataUpdate.get();
        }
        SimpleConfig config = new SimpleConfig("BaseConfig");
        String moduleVersionAndHostAppVersion = ModuleBuildInfo.moduleVersionName + ":" + HookEnv.getVersionName() + ":" + BuildConfig.BUILD_TYPE;
        String oldData = config.get("moduleVersionAndHostAppVersion");
        boolean dataIsOutOfDate = moduleVersionAndHostAppVersion.equals(oldData);
        methodDataUpdate.set(dataIsOutOfDate);
        return dataIsOutOfDate;
    }

    public static class SettingLoader {
        public static final String IS_ENABLED = "是否开启";
        public static final String BYPASS_DEFAULT_LOAD = "绕过默认加载";
        //字符流可以节省一定的性能 并且文件肉眼可读性也比序列化高
        private static JSONObject dataList;

        private static String getDataPath() {
            return PathTool.getModuleDataPath() + "/data/item";
        }

        public static void saveData(String itemName) {
            if (dataList == null) dataList = new JSONObject();
            for (Map.Entry<Class<?>, BaseHookItem> hookItemEntry : HookInstance.entrySet()) {
                BaseHookItem itemInstance = hookItemEntry.getValue();
                //只保存能开关的数据
                if (BaseSwitchFunctionHookItem.class.isAssignableFrom(itemInstance.getClass())) {
                    try {
                        BaseSwitchFunctionHookItem hookItem = (BaseSwitchFunctionHookItem) itemInstance;
                        JSONObject data = new JSONObject();
                        data.put(IS_ENABLED, hookItem.isEnabled());
                        dataList.put(hookItem.getItemPath(), data);
                    } catch (Exception e) {
                        LogUtils.addError(e);
                    }
                }
            }
            FileUtils.writeTextToFile(getDataPath(), dataList.toString(), false);
        }


        public static void loadSetting() {
            //文件不存在那么保存一次
            if (!new File(getDataPath()).exists()) {
                saveData(null);
            }
            //读取设置
            try {
                dataList = new JSONObject(FileUtils.readFileText(getDataPath()));
            } catch (Exception e) {
                saveData(null);
                LogUtils.addError(e);
                ToastTool.show("[QStory]加载设置失败qwq " + e);
                return;
            }
            //加载设置
            for (Map.Entry<Class<?>, BaseHookItem> hookItemEntry : HookInstance.entrySet()) {
                BaseHookItem itemInstance = hookItemEntry.getValue();
                //只保存能开关的数据
                if (BaseSwitchFunctionHookItem.class.isAssignableFrom(itemInstance.getClass())) {
                    BaseSwitchFunctionHookItem hookItem = (BaseSwitchFunctionHookItem) itemInstance;
                    //没有可能是新加的功能 略过
                    if (dataList.isNull(hookItem.getItemPath())) continue;
                    try {
                        JSONObject data = dataList.getJSONObject(hookItem.getItemPath());
                        if (data.getBoolean(IS_ENABLED)) {
                            boolean enabled = data.getBoolean(IS_ENABLED);
                            hookItem.setEnabled(enabled);
                            hookItem.loadHook(ClassUtils.getHostLoader());
                        }
                    } catch (Exception e) {
                        hookItem.getExceptionCollectionToolInstance().addException(e);
                    }

                }
            }
        }

    }


}

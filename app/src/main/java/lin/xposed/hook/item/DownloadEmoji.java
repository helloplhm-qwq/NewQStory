package lin.xposed.hook.item;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import lin.util.ReflectUtils.ClassUtils;
import lin.util.ReflectUtils.ConstructorUtils;
import lin.util.ReflectUtils.FieIdUtils;
import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.R;
import lin.xposed.common.utils.ActivityTools;
import lin.xposed.common.utils.HttpUtils;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.load.methodfind.IMethodFinder;
import lin.xposed.hook.load.methodfind.MethodFinder;
import lin.xposed.hook.util.LogUtils;
import lin.xposed.hook.util.PathTool;
import lin.xposed.hook.util.qq.ToastTool;

@HookItem("辅助功能/表情/在新的QQ中依然可以下载表情")
public class DownloadEmoji extends BaseSwitchFunctionHookItem implements IMethodFinder {

    private final String privateTAG = "保存本地";
    Class<?> emojiInfoClass;
    private String emojiUrl;

    private String emojiMD5;
    @Override
    public String getTips() {
        return "在版本大于等于8.9.80时QQ关闭了表情保存 此功能可以继续下载表情";
    }

    @Override
    public void loadHook(ClassLoader classLoader) throws Exception {

        Class<?> AIOEmotionFragmentClass = ClassUtils.getClass("com.tencent.mobileqq.emotionintegrate.AIOEmotionFragment");
        Method method = AIOEmotionFragmentClass.getMethod("onCreate", Bundle.class);
        hookAfter(method,param -> {
            Object emojiInfo = null;
            for (Field field : param.thisObject.getClass().getDeclaredFields()) {
                if (field.getType() == emojiInfoClass) {
                    emojiInfo = field.get(param.thisObject);
                    break;
                }
            }
            for (Method m : emojiInfo.getClass().getDeclaredMethods()) {
                if (m.getReturnType() == String.class) {
                    m.setAccessible(true);
                    String result = (String) m.invoke(emojiInfo);
                    if (result == null) continue;
                    if (result.length() > 16) {
                        emojiMD5 = result.toUpperCase();
                        emojiUrl = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+result.toUpperCase()+"/0?term=2";
                        break;
                    }
                }
            }
            Activity activity = MethodUtils.callUnknownReturnTypeNoParamMethod(param.thisObject, "getActivity");
            ActivityTools.injectResourcesToContext(activity);
        });

        Class<?> ActionSheetItemAdapterClass = classLoader.loadClass("com.tencent.mobileqq.widget.share.ShareActionSheetV2$ActionSheetItemAdapter");
        hookAfter(ActionSheetItemAdapterClass.getMethod("getView",int.class, android.view.View.class, android.view.ViewGroup.class),param -> {
            ViewGroup resultView = (ViewGroup) param.getResult();
            for (int i = 0; i < resultView.getChildCount(); i++) {
                View view = resultView.getChildAt(i);
                if (view instanceof TextView textView) {
                    String text = textView.getText().toString();
                    if (text.equals(privateTAG)) {
                        resultView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new Thread(()->{
                                    try {
                                        HttpUtils.fileDownload(emojiUrl, PathTool.getStorageDirectory() + "/Pictures/QQ/" + emojiMD5 +".png");
                                        ToastTool.show("保存成功~");
                                    } catch (Exception e) {
                                        ToastTool.show("下载失败 原因 : " + LogUtils.getStackTrace(e));
                                    }
                                }).start();
                            }
                        });
                    }
                }
            }
        });
        Class<?> ShareActionSheetV2Class = classLoader.loadClass("com.tencent.mobileqq.widget.share.ShareActionSheetV2");
        Method m3 = ShareActionSheetV2Class.getMethod("setActionSheetItems", List[].class);
        hookBefore(m3,param -> {
            List[] params = (List[]) param.args[0];
            for (List list : params) {
                for (Object item : list) {
                    String label = FieIdUtils.getField(item, "label", String.class);
                    if (label != null && label.equals("添加到表情")) {
                        Object mItem = ConstructorUtils.newInstance(item.getClass());
                        FieIdUtils.setField(mItem, "icon", R.drawable.download_icon);
                        FieIdUtils.setField(mItem, "reportID", "QStory_DownLoadEmoji");
                        FieIdUtils.setField(mItem, "label", privateTAG);
                        list.add(0, mItem);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void startFind(MethodFinder methodFinder) throws Exception {
        Method m = methodFinder.findMethodString("restoreSaveInstanceState execute")[0];
        methodFinder.putMethod("restoreSaveInstanceState execute", m);
    }

    @Override
    public void getMethod(MethodFinder finder) {
        emojiInfoClass = finder.getMethod("restoreSaveInstanceState execute").getDeclaringClass();
    }
}

package lin.xposed.hook.item;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import lin.util.ReflectUtils.MethodUtils;
import lin.xposed.hook.load.base.BaseSwitchFunctionHookItem;
import lin.xposed.hook.HookItem;
import lin.xposed.hook.util.LogUtils;
import lin.xposed.hook.util.qq.QQEnvTool;

@HookItem("辅助功能/实验功能/获取游戏好友QQ")
public class GetGameFriends extends BaseSwitchFunctionHookItem {

    @Override
    public String getTips() {
        return "主页搜索-游戏消息-聊天界面右上角";
    }

    public static boolean checkQQ(String qq) {
        if (qq == null) return false;
        //先验证是否为5—12位数字
        if (qq.length() < 5 || qq.length() > 12) {
            return false;
        }
        //首位不能是0
        if (qq.charAt(0) == '0') {
            return false;
        }
        //验证每一位数字都在1-9内
        for (int x = 0; x < qq.length(); x++) {
            char ch = qq.charAt(x);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }
    @Override
    public void loadHook(ClassLoader loader) throws Exception {
        LogUtils.addRunLog("TheUin", QQEnvTool.getCurrentUin());
        Method method = MethodUtils.findMethod("com.tencent.mobileqq.activity.ChatSettingActivity", "doOnCreate", boolean.class, new Class[]{Bundle.class});
        hookAfter(method, new HookBehavior() {
            @Override
            public void execute(XC_MethodHook.MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                Button addFriendsButton = null;
                String qq = null;
                for (Field field : param.thisObject.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    //获取按钮
                    if (field.getType() == Button.class) {
                        Button button = (Button) field.get(param.thisObject);
                        if (button == null) continue;
                        if (button.getVisibility() != View.GONE && button.getText().toString().contains("加为QQ好友")) {
                            addFriendsButton = button;
                        }
                    }
                    //分析出QQ
                    if (field.getType() == String.class) {
                        String uin = (String) field.get(param.thisObject);
                        if (checkQQ(uin)) {
                            qq = uin;
                        }
                    }
                }
                //更改事件
                if (addFriendsButton != null && qq != null) {
                    addFriendsButton.setText("[原]加为QQ好友");
                    Button button = new Button(activity);
                    button.setText("[QS]跳转主页");
                    button.setBackground(addFriendsButton.getBackground());
                    button.setTextSize(TypedValue.COMPLEX_UNIT_PX, addFriendsButton.getTextSize());
                    button.setTextColor(addFriendsButton.getTextColors());
                    String finalQq = qq;
                    button.setOnClickListener(v -> {
                        String urlQQ = "mqq://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=" + finalQq;
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlQQ)));
                    });
                    LinearLayout rootView = (LinearLayout) addFriendsButton.getParent();
                    rootView.addView(button, rootView.getChildCount() - 2, addFriendsButton.getLayoutParams());

                    Intent intent = activity.getIntent();
                    Bundle bundle = intent.getExtras();
                    Set<String> KeyList = bundle.keySet();

                    TextView textView = new TextView(activity);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(15);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setPadding(20, 20, 20, 20);
                    textView.setText("""
                            如果你看到了这里 可能你也是开发者或从事相关行业人员 很高兴你使用此模块和此功能\s
                            此功能原本不会被设计与公开 由@梓开发并添加
                            但是本功能在用户层并不合法 QQ并没有打算对普通用户出示其游戏好友的QQ号 因为这将导致游戏好友的隐私泄露 !
                            在你使用此功能时需要意识到 这可能并不是合法行为 最好也不要打扰您游戏好友的正常生活
                            使用此功能可能需要独自承担风险 请您悉知!

                            (已分析整体业务逻辑 简述-> 获取游戏好友列表(roleid(明文上发后下发密文),openid等) ->通过roleid获取到好友的信息(uin,头像url等)->加游戏好友全程使用openid+appid通讯)""");
                    rootView.addView(textView);
                }
            }
        });
    }
}

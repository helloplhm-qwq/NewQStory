package lin.xposed.main;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lin.xposed.BuildConfig;
import lin.xposed.R;
import lin.xposed.databinding.ActivityMainBinding;
import lin.xposed.view.main.itemview.AddTelegramChannel;
import top.linl.annotationprocessor.AnnotationClassNameTools;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        this.setContentView(binding.getRoot());

        requestTranslucentStatusBar();

        // 修改状态栏字体颜色，用AndroidX官方兼容API
        WindowInsetsControllerCompat wic = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (wic != null) {
            // true表示Light Mode，状态栏字体呈黑色，反之呈白色
            wic.setAppearanceLightStatusBars(true);
        }

        initView();
    }

    private void initView() {
        try {
            String buildTime = (String) MainActivity.class.getClassLoader().loadClass(AnnotationClassNameTools.getClassName()).getField("BUILD_TIME").get(null);
            String versionAndBuildTimeInfo = String.format("Current version : %s%nBuild time : %s", BuildConfig.VERSION_NAME, buildTime);
            binding.versionAndBuildtime.setText(versionAndBuildTimeInfo);
        } catch (Exception e) {

        }
        View telegram_channel = (View) binding.telegramChannel.getParent();
        telegram_channel.setOnClickListener(new AddTelegramChannel(this).getOnClick());

        for (int i = 0; i < binding.getRoot().getChildCount(); i++) {
            try {
                View item = binding.getRoot().getChildAt(i);
                item.setBackground(getDrawable(R.drawable.item_shape));

                Method listenerInfoMethod = View.class.getDeclaredMethod("getListenerInfo");
                listenerInfoMethod.setAccessible(true);
                Object listenerInfo = listenerInfoMethod.invoke(item);
                //要获取单击监听器则在前面加个m 其他同理 例如长按监听器就是mOnLongClickListener
                Class<?> listenerInfoClass = listenerInfo.getClass();
                Field mOnClickListenerField = listenerInfoClass.getDeclaredField("mOnClickListener");
                mOnClickListenerField.setAccessible(true);
                //通过字段获取此监听器对象
                View.OnClickListener onClick =(View.OnClickListener) mOnClickListenerField.get(listenerInfo);
                if (onClick == null) {
                   item.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {

                       }
                   });
                }
            } catch (Exception e) {

            }
        }
    }

    protected void requestTranslucentStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.透明));
        window.setNavigationBarColor(getColor(R.color.透明));
    }

}

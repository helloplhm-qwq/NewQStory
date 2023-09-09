package lin.xposed.view.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import lin.xposed.common.utils.ViewUtils;
import lin.xposed.R;
import lin.xposed.view.main.itemListView.SettingViewFragment;
import lin.xposed.view.main.itemview.info.ItemUiInfoManager;
import top.linl.activity.BaseActivity;


/**
 *
 * @author 言子 @163.com
 * @see MainAdapter
 * @see MainLayoutManager
 * @see LinearSpacingItemDecoration
 *
 * 这个包下写了所有的开关 主要的界面逻辑 比较复杂 很难阅读
 */

@SuppressLint("StaticFieldLeak")
public class MainSettingActivity extends BaseActivity {


    public static View titleLayout;
    public static TextView leftText, centerText;
    public static final int ITEM_LIST_CONTAINER = R.id.itemList_container;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root_layout);
        requestTranslucentStatusBar();
        ViewUtils.titleBarAdaptsToStatusBar(findViewById(R.id.setting_title_bar));
        initView();

    }

    public static void setTitleLeftText(String text) {
        leftText.setText(text);
    }

    public static void setTitleCenterText(String text) {
        centerText.setText(text);
    }

    private void initView() {
        ItemUiInfoManager.init();

        titleLayout = findViewById(R.id.setting_title_bar);
        leftText = titleLayout.findViewById(R.id.title_left_text);
        centerText = titleLayout.findViewById(R.id.title_center_text);
        setTitleLeftText("<");
        setTitleCenterText(getString(R.string.app_name));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SettingViewFragment fragment = new SettingViewFragment();

        fragmentTransaction.add(ITEM_LIST_CONTAINER, fragment);
        fragmentTransaction.commit();

    }


}

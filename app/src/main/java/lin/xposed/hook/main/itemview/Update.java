package lin.xposed.hook.main.itemview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import lin.xposed.R;
import lin.xposed.common.utils.ActivityTools;
import lin.xposed.common.utils.HttpUtils;
import lin.xposed.common.utils.ViewUtils;
import lin.xposed.hook.main.itemview.base.OtherViewItemInfo;
import lin.xposed.hook.util.qq.ToastTool;
import lin.app.main.ModuleBuildInfo;
import lin.xposed.widget.MDialog;

public class Update extends OtherViewItemInfo {
    private static JSONObject updateInfo;
    private ProgressBar progressBar;
    private TextView progressText;


    public Update(Context context) {
        super(context);
    }



    public static void jumpToBrowserUpdates(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("https://linl.top/getNewApk");
        intent.setData(content_url);
        ActivityTools.getActivity().startActivity(intent);
    }

    private static boolean hasUpdate() {
        try {
            JSONObject info = updateInfo;
            int newVersion = info.getInt("newVersion");
            boolean isUpdate = info.getBoolean("isUpdate");
            String updateUrl = info.getString("url");
            String log = info.getString("log");
            if (isUpdate || ModuleBuildInfo.moduleVersion < newVersion) return true;
        } catch (Exception e) {

        }
        return false;
    }

    private static void DetectUpdates() {
        String url = "https://linl.top/update";
        JSONObject results = null;
        // 开始时间
        long stime = System.currentTimeMillis();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("version", ModuleBuildInfo.moduleVersion);
            String result = HttpUtils.sendPost(url, jsonObject);
            results = new JSONObject(result);
            long etime = System.currentTimeMillis();
            // 计算执行时间
            ToastTool.show("请求耗时 " + (etime - stime) + "ms");
        } catch (Exception e) {
            ToastTool.show("服务器连接异常 " + e);
        }
        updateInfo = results;
    }

    @Override
    public String getLeftText() {
        return "检测更新";
    }

    @Override
    public String getTips() {
        return "服务器原因暂时无法提供此功能 请前往TG频道更新";
    }

    @Override
    public View.OnClickListener getOnClick() {
        return new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) v.getContext();
                //初始化控件
                MDialog dialog = new MDialog(activity);
                View root = LayoutInflater.from(activity).inflate(R.layout.update_dialog_layout, null);
                progressBar = root.findViewById(R.id.DownloadProgress);
                progressText = root.findViewById(R.id.DownloadProgressText);

                //为控件设置初始化的属性
                Drawable background = ViewUtils.BackgroundBuilder.createBaseBackground(activity.getColor(R.color.white), 25);
                root.setBackground(background);

            }
        };
    }

}

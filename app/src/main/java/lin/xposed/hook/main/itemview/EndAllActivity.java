package lin.xposed.hook.main.itemview;

import android.content.Context;
import android.view.View;

import lin.xposed.common.utils.ActivityTools;
import lin.xposed.hook.main.itemview.base.OtherViewItemInfo;
import top.linl.activity.util.ActivityUtils;

public class EndAllActivity extends OtherViewItemInfo {
    public EndAllActivity(Context context) {
        super(context);
    }

    @Override
    public String getLeftText() {
        return "重启QQ";
    }

    @Override
    public View.OnClickListener getOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.killAppProcess(ActivityUtils.getActivity());
            }
        };
    }
}

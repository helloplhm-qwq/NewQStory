package lin.xposed.hook.util.qq;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class QQViewTool {

    public static GradientDrawable createQQGradientColorSettingViewTitleBackground() {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#00E4FF"), Color.parseColor("#14CDFF"), Color.parseColor("#00B1FF")});

        return drawable;
    }
}

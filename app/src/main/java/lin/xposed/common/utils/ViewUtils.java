package lin.xposed.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;

/**
 * @author 言子楪世suzhelan asmclk@163.com
 */
public class ViewUtils {

    /**
     * 标题适配状态栏 在进行此设置前需要将当前Activity的状态栏设置为透明
     *
     * @param titleBar 标题布局
     */
    public static void titleBarAdaptsToStatusBar(ViewGroup titleBar) {
        Context context = titleBar.getContext();
        //获取状态栏高度
        int statusBarHeight = 0;
        @SuppressLint({"DiscouragedApi", "InternalInsetResource"}) int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        //适配高度
        ViewGroup.LayoutParams params = titleBar.getLayoutParams();
        params.height += statusBarHeight;
        //模拟setFitsSystemWindows(ture)填充
        titleBar.setPadding(titleBar.getPaddingLeft(), titleBar.getPaddingTop() + statusBarHeight, titleBar.getPaddingRight(), titleBar.getPaddingBottom());
    }

    public static class BackgroundBuilder {

        /**
         * 创建背景
         *
         * @param color  填充色
         * @param radius 圆角角度
         */
        public static GradientDrawable createBaseBackground(@ColorInt int color, int radius) {
            return createRoundedBackground(color, 0, 0, radius);
        }

        /**
         * 创建背景
         *
         * @param color       填充色
         * @param strokeColor 线条颜色
         * @param strokeWidth 线条宽度  单位px
         * @param radius      圆角角度
         */
        public static GradientDrawable createRoundedBackground(@ColorInt int color, @ColorInt int strokeColor, int strokeWidth, int radius) {
            GradientDrawable radiusBg = new GradientDrawable();
            //设置Shape类型
            radiusBg.setShape(GradientDrawable.RECTANGLE);
            //设置填充颜色
            radiusBg.setColor(color);
            //设置线条粗心和颜色,px
            if (strokeColor != 0 && strokeWidth != 0) radiusBg.setStroke(strokeWidth, strokeColor);
            radiusBg.setInnerRadius(radius);
            return radiusBg;
        }

        /**
         * 创建背景颜色
         *
         * @param color       填充色
         * @param strokeColor 线条颜色
         * @param strokeWidth 线条宽度  单位px
         * @param radius      角度  px,长度为4,分别表示左上,右上,右下,左下的角度
         */
        public static GradientDrawable createRectangleDrawable(@ColorInt int color, @ColorInt int strokeColor, int strokeWidth, float[] radius) {
            try {
                GradientDrawable radiusBg = new GradientDrawable();
                //设置Shape类型
                radiusBg.setShape(GradientDrawable.RECTANGLE);
                //设置填充颜色
                radiusBg.setColor(color);
                //设置线条粗心和颜色,px
                if (strokeColor != 0 && strokeWidth != 0)
                    radiusBg.setStroke(strokeWidth, strokeColor);
                //每连续的两个数值表示是一个角度,四组:左上,右上,右下,左下
                if (radius != null && radius.length == 4) {
                    radiusBg.setCornerRadii(new float[]{radius[0], radius[0], radius[1], radius[1], radius[2], radius[2], radius[3], radius[3]});
                }
                return radiusBg;
            } catch (Exception e) {
                return new GradientDrawable();
            }
        }
    }


}

package lin.xposed.hook.main;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lin.xposed.common.utils.ScreenParamUtils;
import lin.xposed.hook.main.itemview.base.OtherViewItemInfo;
import lin.xposed.hook.main.itemview.info.ItemUiInfoGroupWrapper;

public class LinearSpacingItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 设置item边距
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //获取索引
        int position = parent.getChildAdapterPosition(view);
        //获取此索引的信息
        Object info = MainAdapter.getDataList().get(position);
        if (info instanceof ItemUiInfoGroupWrapper
                || position == 0
                || (info instanceof OtherViewItemInfo && !(MainAdapter.getDataList().get(position-1) instanceof OtherViewItemInfo)) ) {
            //如果是组类型的那么设置上边距 不用设置位于顶部的view 因为顶部已经和标题栏保持了距离
            outRect.top = ScreenParamUtils.dpToPx(parent.getContext(), 16);
        }
        //左右边距
        int leftAndRightMargins = ScreenParamUtils.dpToPx(parent.getContext(), 16);
        outRect.left = leftAndRightMargins;//左边距
        outRect.right = leftAndRightMargins;//右边距
    }

}

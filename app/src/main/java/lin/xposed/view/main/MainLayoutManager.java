package lin.xposed.view.main;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class MainLayoutManager extends RecyclerView.LayoutManager {
    private int mSumDy;//垂直滑动的总距离
    private int mItemTotalHeight = 0;//item总高度

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    //处理每个子View的数据
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int offSetY = 0;//垂直方向的偏移量
        for (int i = 0; i < getItemCount(); i++) {
            View itemView = recycler.getViewForPosition(i);//从缓存取出
            addView(itemView);//将itemView加入到RecyclerView中
            //对子View进行测量
            measureChildWithMargins(itemView, 0, 0);
            //拿到宽高（包括ItemDecoration）
            int width = getDecoratedMeasuredWidth(itemView);
            int height = getDecoratedMeasuredHeight(itemView);

            //布局，将itemView列出并摆放对应的位置在RecyclerView坐标中
            layoutDecorated(itemView, 0, offSetY, width, offSetY + height);
            offSetY += height;
        }

        mItemTotalHeight = Math.max(offSetY, getRecyclerViewRealHeight());
    }

    //获取RecyclerView的真实高度
    private int getRecyclerViewRealHeight() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    //能否垂直滑动
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    //垂直滑动的距离
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int offSetDy = dy;
        //如果滑动到最顶部
        if (mSumDy + offSetDy < 0) {
            offSetDy = -mSumDy;
        } else if (mSumDy + offSetDy > mItemTotalHeight - getRecyclerViewRealHeight()) {//如果滑动到底部
            offSetDy = mItemTotalHeight - getRecyclerViewRealHeight() - mSumDy;
        }

        mSumDy += offSetDy;
        offsetChildrenVertical(-offSetDy);//偏移RecyclerView内的item
        return dy;
    }
}

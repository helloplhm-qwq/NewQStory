package lin.xposed.view.main.itemview.base;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import lin.xposed.R;


/**
* 主页面base item开发规范
* 继承此类 例如 {@link lin.xposed.view.main.itemview.Update}
* 如果需要自己
*/
public class DefaultItemView extends RecyclerView.ViewHolder {

    public TextView leftText,tipsText;

    public DefaultItemView(View view) {
        super(view);
        onCreate();
    }

    protected void onCreate() {
        leftText = itemView.findViewById(R.id.base_item_left_text);
        tipsText = itemView.findViewById(R.id.base_item_left_tips_text);
    }

}

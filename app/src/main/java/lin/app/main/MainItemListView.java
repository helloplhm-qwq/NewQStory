package lin.app.main;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import lin.xposed.R;

public class MainItemListView extends ArrayAdapter<MainItemListView.ItemInfo> {


    public MainItemListView(@NonNull Context context, int resource, @NonNull List<ItemInfo> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemInfo itemInfo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.main_item, parent, false);
        TextView textView = view.findViewById(R.id.main_item_text);
        textView.setText(itemInfo.text);
        itemInfo.onTextViewCreate(textView);
        view.setOnClickListener(itemInfo.onClickListener);
        return view;
    }


    public static class ItemInfo {

        public String text;
        public View.OnClickListener onClickListener;

        public ItemInfo(String text, View.OnClickListener onClickListener) {
            this.text = text;
            if (onClickListener == null) {
                this.onClickListener = v -> {
                };
            } else {
                this.onClickListener = onClickListener;
            }
        }
        public void onTextViewCreate(TextView textView) {

        }
    }

    /**
     * 设置整个字符串中的指定字符串的为指定颜色
     * @param origin 原字符串
     * @param color 高亮的颜色
     * @return
     */
    public static SpannableString setStringColor(String origin,int start,int end, int color){
        SpannableString spannableString = new SpannableString(origin);
        if (!TextUtils.isEmpty(origin)) {
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            spannableString.setSpan(span,start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }


}

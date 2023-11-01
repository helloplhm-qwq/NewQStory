package lin.app.main;

import android.content.Context;
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
    }

}

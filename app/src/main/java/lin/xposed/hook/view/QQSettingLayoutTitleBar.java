package lin.xposed.hook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import lin.xposed.R;

public class QQSettingLayoutTitleBar extends ConstraintLayout {
    private final TextView leftText;
    private final TextView center;
    private CustomerClick customerClick;

    public QQSettingLayoutTitleBar(Context context) {
        this(context, null);
    }

    public QQSettingLayoutTitleBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public QQSettingLayoutTitleBar(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        //bing view
        View.inflate(context, R.layout.setting_title_bar, this);
        leftText = findViewById(R.id.title_left_text);
        center = findViewById(R.id.title_center_text);
        //bing view end


    }


    private void initViewAnd() {
        leftText.setOnClickListener(v -> {
            if (customerClick != null) customerClick.onLeftClick(v);
        });
        center.setOnClickListener(v -> {
            if (customerClick != null) customerClick.onCenterClick(v);
        });
    }

    public void setViewClick(CustomerClick newClick) {
        this.customerClick = newClick;
    }

    interface CustomerClick {
        void onLeftClick(View view);

        default void onCenterClick(View view) {

        }
    }
}

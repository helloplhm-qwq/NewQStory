package lin.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lin.widget.dialog.base.MDialog;
import lin.xposed.R;

public class SimpleTextDialog extends MDialog {

    private final TextView textView;
    private final TextView titleTextView;

    private SimpleTextDialog() {
        this(null);
    }

    @SuppressLint("ResourceType")
    public SimpleTextDialog(Context context) {
        super(context,R.style.dialog_backgroundDimEnabled);
        Activity activity = (Activity) context;
        View rootView = LayoutInflater.from(context).inflate(R.layout.simple_text_diaog_layout,null,false);
        textView = rootView.findViewById(R.id.simple_text_dialog_content);
        titleTextView = rootView.findViewById(R.id.simple_text_dialog_title);
        setContentView(rootView);
        //限制宽不限制高
        setWidthAndWidth((int) (getScreenWidth() * 0.7), ViewGroup.LayoutParams.WRAP_CONTENT);
    }



    public static class Builder {
        private final SimpleTextDialog dialog;

        public Builder(Context context) {
            this.dialog = new SimpleTextDialog(context);
        }
        public Builder setTitle(CharSequence text) {
            dialog.titleTextView.setText(text);
            return this;
        }

        public Builder setContent(CharSequence text) {
            dialog.textView.setText(text);
            return this;
        }

        public SimpleTextDialog get() {
            return this.dialog;
        }
    }
}

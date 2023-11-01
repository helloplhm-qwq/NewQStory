package lin.widget.dialog.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class BaseSimpleDialog extends MDialog {

    public BaseSimpleDialog(Context context) {
        super(context);
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        setWidthAndWidth((int) (getScreenWidth() * 0.7), ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}

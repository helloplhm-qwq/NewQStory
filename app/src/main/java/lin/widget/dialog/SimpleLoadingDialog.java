package lin.widget.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;

import lin.widget.dialog.base.MDialog;
import lin.xposed.R;

public class SimpleLoadingDialog extends MDialog {
    public ProgressBar progressBar;
    private final TextView titleView;
    private final TextView runTimeView;
    private boolean isStop = false;
    private long startTime;
    private final Handler time = new Handler(Looper.getMainLooper()){
        final SimpleDateFormat ft = new SimpleDateFormat("ss.SSS");
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            long msgTime = (long) msg.obj;
            long runTime = startTime - msgTime;
            long second =  runTime / 1000;
            long millisecond = runTime - second * 1000;

            runTimeView.setText(Math.abs(second)+"."+Math.abs(millisecond)+"s");
        }
    };
    @SuppressLint("MissingInflatedId")
    public SimpleLoadingDialog(Context context) {
        super(context, R.style.dialog_backgroundDimEnabled);
        View rootView = LayoutInflater.from(context).inflate(R.layout.simple_loading_dialog_layout, null, false);
        titleView = rootView.findViewById(R.id.simple_loading_title_text);
        runTimeView = rootView.findViewById(R.id.simple_loading_runtime);
        progressBar = rootView.findViewById(R.id.simple_loading);
        setContentView(rootView);
        setWidthAndWidth((int) (getScreenWidth() * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void show() {
        super.show();
        startTime = System.currentTimeMillis();
        new Thread(()->{
            while (!isStop) {
                Message message = new Message();
                message.obj = System.currentTimeMillis();
                time.sendMessage(message);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
    }

    public void setTitle(String text) {
        titleView.setText(text);
    }
}

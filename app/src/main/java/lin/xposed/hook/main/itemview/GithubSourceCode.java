package lin.xposed.hook.main.itemview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import lin.xposed.hook.main.itemview.base.OtherViewItemInfo;

public class GithubSourceCode extends OtherViewItemInfo {
    public GithubSourceCode(Context context) {
        super(context);
    }

    @Override
    public String getLeftText() {
        return "Github开源地址";
    }

    @Override
    public View.OnClickListener getOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content = Uri.parse("https://github.com/Suzhelan/NewQStory");
                intent.setData(content);
                view.getContext().startActivity(intent);
            }
        };
    }
}

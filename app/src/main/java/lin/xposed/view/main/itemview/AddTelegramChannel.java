package lin.xposed.view.main.itemview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import lin.xposed.view.main.itemview.base.OtherViewItemInfo;

public class AddTelegramChannel extends OtherViewItemInfo {
    public AddTelegramChannel(Context context) {
        super(context);
    }

    @Override
    public String getLeftText() {
        return "TG频道";
    }

    @Override
    public View.OnClickListener getOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content = Uri.parse("https://t.me/WhenFlowersAreInBloom");
                intent.setData(content);
                v.getContext().startActivity(intent);
            }
        };
    }
}

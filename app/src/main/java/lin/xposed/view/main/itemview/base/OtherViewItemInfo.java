package lin.xposed.view.main.itemview.base;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lin.xposed.view.main.itemview.AddTelegramGroup;
import lin.xposed.view.main.itemview.Update;
import lin.xposed.view.main.itemview.AddTelegramChannel;


/**
 * 其他在主页面的视图都应该继承此类并添加到下面的static{ }代码块中
 * 例如Update类 {@link Update}
 */
public abstract class OtherViewItemInfo {

    public static final List<Class<? extends OtherViewItemInfo>> OTHER_VIEW_ITEM_INFO_LIST = new ArrayList<>();

    static {
        OTHER_VIEW_ITEM_INFO_LIST.add(Update.class);
        OTHER_VIEW_ITEM_INFO_LIST.add(AddTelegramChannel.class);
        OTHER_VIEW_ITEM_INFO_LIST.add(AddTelegramGroup.class);
    }

    public OtherViewItemInfo(Context context) {

    }

    public abstract String getLeftText();

    public abstract View.OnClickListener getOnClick() ;

    public String getTips() {
        return null;
    }



}

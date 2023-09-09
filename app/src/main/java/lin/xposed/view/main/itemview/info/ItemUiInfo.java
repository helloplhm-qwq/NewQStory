package lin.xposed.view.main.itemview.info;

import lin.xposed.hook.load.base.BaseHookItem;

public class ItemUiInfo extends BaseItemUiInfo {
    public BaseHookItem item;
    public String tips;//提示

    public ItemUiInfo(String[] paths) {
        super(paths);
    }

    @Override
    public String toString() {
        return "ItemUiInfo{" +
                "item=" + item +
                ", tips='" + tips + '\'' +
                '}';
    }
}

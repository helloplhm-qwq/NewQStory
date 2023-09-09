package lin.xposed.view.main.itemview.info;


import java.util.ArrayList;
import java.util.List;

/**
 * 目录UI
 */
public class DirectoryUiInfo extends BaseItemUiInfo {
    //右文本
    public CharSequence rightText;

    public List<BaseItemUiInfo> uiInfoList = new ArrayList<>();

    public List<ItemUiInfoGroupWrapper> groupWrapperList = new ArrayList<>();

    public DirectoryUiInfo(String[] paths) {
        super(paths);
        if (paths.length >= 3) this.paths = new String[]{paths[0], paths[1]};
    }


    //项目有唯一性 不会重复
    public void addItemUiInfo(BaseItemUiInfo info) {
        uiInfoList.add(info);
    }

    public void addGroupWrapper(ItemUiInfoGroupWrapper wrapper) {
        for (ItemUiInfoGroupWrapper infoGroupWrapper : this.groupWrapperList) {
            if (infoGroupWrapper.getGroupName().equals(wrapper.getGroupName())) return;
        }
        this.groupWrapperList.add(wrapper);
    }

    @Override
    public String toString() {
        return "DirectoryUiInfo{" +
                "rightText=" + rightText +
                ", uiInfoList=" + uiInfoList +
                ", groupWrapperList=" + groupWrapperList +
                '}';
    }
}

package lin.xposed.hook.main.itemview.info;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

//item组包装器 里面是item
public class ItemUiInfoGroupWrapper {
    private String groupName;

    private final List<BaseItemUiInfo> directoryUIInfoList = new ArrayList<>();


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<BaseItemUiInfo> getDirectoryUIInfoList() {
        return directoryUIInfoList;
    }


    public void addDirectoryUIInfo(BaseItemUiInfo directoryUIInfo) {
        for (BaseItemUiInfo dirUiInfo : directoryUIInfoList) {
            if (dirUiInfo.getItemName().equals(directoryUIInfo.getItemName())) return;
        }
        directoryUIInfoList.add(directoryUIInfo);
    }

    @NonNull
    @Override
    public String toString() {
        return "ItemUiInfoGroupWrapper{" +
                "groupName='" + groupName + '\'' +
                ", directoryUIInfoList=" + directoryUIInfoList +
                '}';
    }
}

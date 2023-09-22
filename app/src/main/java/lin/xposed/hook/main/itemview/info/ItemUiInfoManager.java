package lin.xposed.hook.main.itemview.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import lin.xposed.hook.load.HookItemLoader;
import lin.xposed.hook.load.base.BaseHookItem;
import lin.xposed.hook.HookItem;

/*
 * 别看了 我也看不懂(写完就忘)
 */
public class ItemUiInfoManager {

    public static final List<Object> rootContainer = new ArrayList<>();//["groupInfo{"dirInfo"}","groupInfo{"DirInfo"}","item"]

    private static final AtomicBoolean isInit = new AtomicBoolean();
    public static void init() {
        if (isInit.getAndSet(true)) return;
        for (Map.Entry<Class<?>, BaseHookItem> hookItemEntry : HookItemLoader.HookInstance.entrySet()) {
            BaseHookItem hookItem = hookItemEntry.getValue();
            if (hookItem.getItemPath().equals(HookItem.NoPath)) continue;

            String[] paths = hookItem.getItemPath().split("/");
            ItemUiInfo uiInfo = new ItemUiInfo(paths);
            uiInfo.item = hookItem;

            switch (paths.length) {
                //group/dir/group/name
                case 4 -> {
                    //将不存在的内容添加到根容器
                    if (!isContainsGroup(rootContainer, paths[0])) {
                        ItemUiInfoGroupWrapper infoGroupWrapper = new ItemUiInfoGroupWrapper();
                        infoGroupWrapper.setGroupName(paths[0]);
                        rootContainer.add(infoGroupWrapper);
                    }
                    if (!isContainsDir(paths[0], paths[1])) {
                        DirectoryUiInfo directoryUIInfo = new DirectoryUiInfo(paths);
                        addDirUiInfo(paths[0], directoryUIInfo);
                    }

                    //查找目录索引
                    int[] index = findInfoIndex(paths[0], paths[1]);
                    if (index != null) {
                        DirectoryUiInfo directoryUIInfo = (DirectoryUiInfo) ((ItemUiInfoGroupWrapper) rootContainer.get(index[0])).getDirectoryUIInfoList().get(index[1]);
                        //在目录中查找并传入第二级group名称
                        List<ItemUiInfoGroupWrapper> groupWrapperList = directoryUIInfo.groupWrapperList;
                        if (!isContainsGroup(groupWrapperList, paths[2])) {
                            ItemUiInfoGroupWrapper infoGroupWrapper = new ItemUiInfoGroupWrapper();
                            infoGroupWrapper.setGroupName(paths[2]);
                            directoryUIInfo.addGroupWrapper(infoGroupWrapper);
                        }
                        //查找二级目录
                        for (ItemUiInfoGroupWrapper wrapper : groupWrapperList) {
                            if (wrapper.getGroupName().equals(paths[2])) {
                                wrapper.getDirectoryUIInfoList().add(uiInfo);
                            }
                        }
                    }
                }
                //group/dir/item
                case 3 -> {
                    //将不存在的内容添加到根容器
                    if (!isContainsGroup(rootContainer, paths[0])) {
                        ItemUiInfoGroupWrapper infoGroupWrapper = new ItemUiInfoGroupWrapper();
                        infoGroupWrapper.setGroupName(paths[0]);
                        rootContainer.add(infoGroupWrapper);
                    }
                    if (!isContainsDir(paths[0], paths[1])) {
                        DirectoryUiInfo directoryUIInfo = new DirectoryUiInfo(paths);
                        addDirUiInfo(paths[0], directoryUIInfo);
                    }
                    //查找到合适的索引的目录信息
                    int[] indexs = findInfoIndex(paths[0], paths[1]);
                    if (indexs != null) {
                        DirectoryUiInfo directoryUIInfo = (DirectoryUiInfo) ((ItemUiInfoGroupWrapper) rootContainer.get(indexs[0]))
                                .getDirectoryUIInfoList().get(indexs[1]);
                        directoryUIInfo.addItemUiInfo(uiInfo);
                    }
                }
            }
        }
    }

    private static <T> boolean isContainsGroup(List<T> list, String GroupName) {
        for (Object wrapper : list) {
            if (wrapper instanceof ItemUiInfoGroupWrapper infoGroupWrapper) {
                if (infoGroupWrapper.getGroupName().equals(GroupName)) return true;
            }
        }
        return false;
    }

    private static boolean isContainsDir(String GroupName, String dirName) {
        //三角形具有稳定性
        for (Object wrapper : ItemUiInfoManager.rootContainer) {
            if (wrapper instanceof ItemUiInfoGroupWrapper infoGroupWrapper) {
                if (infoGroupWrapper.getGroupName().equals(GroupName)) {
                    for (BaseItemUiInfo baseItemUiInfo : infoGroupWrapper.getDirectoryUIInfoList()) {
                        if (baseItemUiInfo instanceof DirectoryUiInfo directoryUIInfo)
                            if (directoryUIInfo.getItemName().equals(dirName)) {
                                return true;
                            }
                    }
                }
            }
        }
        return false;
    }

    private static void addDirUiInfo(String GroupName, BaseItemUiInfo uiInfo) {
        for (Object wrapper : ItemUiInfoManager.rootContainer) {
            if (wrapper instanceof ItemUiInfoGroupWrapper infoGroupWrapper) {
                if (infoGroupWrapper.getGroupName().equals(GroupName)) {
                    ((ItemUiInfoGroupWrapper) wrapper).addDirectoryUIInfo(uiInfo);
                    break;
                }
            }
        }
    }

    public static int[] findInfoIndex(String group, String dirName) {

        for (int i = 0; i < rootContainer.size(); i++) {
            if (rootContainer.get(i) instanceof ItemUiInfoGroupWrapper wrapper) {
                if (wrapper.getGroupName().equals(group)) {
                    for (int j = 0; j < wrapper.getDirectoryUIInfoList().size(); j++) {
                        if (wrapper.getDirectoryUIInfoList().get(j) instanceof DirectoryUiInfo directoryUIInfo) {
                            if (directoryUIInfo.getItemName().equals(dirName)) {
                                return new int[]{i, j};
                            }
                        }
                    }
                }
            }
        }
        return null;
    }


}

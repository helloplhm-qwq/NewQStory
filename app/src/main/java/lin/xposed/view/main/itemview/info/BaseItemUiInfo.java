package lin.xposed.view.main.itemview.info;

/**
 * item封装后的Ui信息 (
 */
public abstract class BaseItemUiInfo {


    //路径列表
    protected String[] paths;

    public BaseItemUiInfo(String[] paths) {
        this.paths = paths;
    }

    public String getItemName() {
        return paths[paths.length - 1];
    }

    //获取这个Item的类型
    //可能是可开关的项
    //可能是目录项
    //可能只是单纯的可点击项
    //根据paths数量 这里有明确规范几个是什么项
    public final int getType() {
        int length = paths.length;
        if (length == 4) return 0;//功能
        if (length == 3) return 0;//功能
        if (length == 2) return 1;//目录
        if (length == 1) return -1;//最外层
        else return -2;//非法
    }

}

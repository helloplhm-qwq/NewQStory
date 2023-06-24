package lin.xposed.hook;

import lin.xposed.hook.item.HookSensorEventListener;


public class HookInit {

    /*
     * 第二步 结构初始化 这里项目简单 不做结构化的设计模式
     */
    public static void loadHook() {
        HookSensorEventListener test = new HookSensorEventListener();
        test.startHook();

    }
}

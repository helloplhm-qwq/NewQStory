package lin.xposed.hook;


import java.util.concurrent.atomic.AtomicBoolean;

import lin.util.ReflectUtils.ClassUtils;

public class QQVersion {
    private static AtomicBoolean isQQNT ;
    public static final int QQ_8_9_35 = 3814;
    public static final int QQ_8_9_38 = 3856;
    public static final int QQ_8_9_50 = 3898;
    public static final int QQ_8_9_58 = 4106;
    public static final int QQ_8_9_73 = 4416;
    public static final int QQ_8_9_78 = 4548;
    public static boolean isQQNT() {
        if (isQQNT != null) return isQQNT.get();
        isQQNT = new AtomicBoolean();
        try {
            ClassUtils.getClass("com.tencent.mobileqq.startup.step.LoadData");
            isQQNT.set(false);
        } catch (Exception e) {
            isQQNT.set(true);
        }
        return isQQNT.get();
    }
}

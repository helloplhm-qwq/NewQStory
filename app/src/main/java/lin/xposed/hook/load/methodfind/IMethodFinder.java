package lin.xposed.hook.load.methodfind;

/**
 * 实现该接口以可以反混淆查找方法
 *
 * 2023.10.15
 */
public interface IMethodFinder {
    /**
     * 在方法查找期的时候会调用 用来查找方法
     */
    void startFind(MethodFinder finder) throws Exception;

    /**
     * 不在方法查找期的时候会调用 用来得到方法
     */
    void getMethod(MethodFinder finder);
}

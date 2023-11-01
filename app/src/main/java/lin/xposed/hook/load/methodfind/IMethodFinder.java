package lin.xposed.hook.load.methodfind;

/**
 * 实现该接口以可以反混淆查找方法
 *
 * 2023.10.15
 */
public interface IMethodFinder {
    void startFind(MethodFinder methodFinder) throws Exception;

}

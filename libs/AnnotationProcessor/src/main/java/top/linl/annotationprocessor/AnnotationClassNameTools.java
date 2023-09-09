package top.linl.annotationprocessor;

public class AnnotationClassNameTools {

    /*生成的类名*/
    static final String CLASS_NAME = "NeedsTobeLoadHookProject";
    /*所处路径名*/
    static final String CLASS_PACKAGE = "lin.xposed.hook.load";

    public static String getClassName() {
        return CLASS_PACKAGE + "." + CLASS_NAME;
    }
}

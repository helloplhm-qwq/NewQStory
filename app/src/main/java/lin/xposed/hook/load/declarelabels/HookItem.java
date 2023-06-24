package lin.xposed.hook.load.declarelabels;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//声明某个类是hook项目 等待被扫描
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HookItem {
    String h = "h";
}

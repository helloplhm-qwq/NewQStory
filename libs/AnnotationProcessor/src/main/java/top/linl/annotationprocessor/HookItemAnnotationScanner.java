package top.linl.annotationprocessor;


import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * 用来自动处理注解
 * 以便不用手动添加需要加载的Hook类
 * 在运行时会自动扫描并加载
 */
@AutoService(Processor.class)//自动创建\resources\META-INF\services\javax.annotation.processing.Processor写入当前类类名
@SupportedSourceVersion(SourceVersion.RELEASE_17)//版本
@SupportedAnnotationTypes("lin.xposed.hook.HookItem")//指定只处理哪个注解 如果要处理所有的注解填*
public class HookItemAnnotationScanner extends AbstractProcessor {
    private Map<String, String> AnnotatedList;

    private void addAsClassArray(StringBuilder builder) {
        //写文件头
        builder.append("package " + AnnotationClassNameTools.CLASS_PACKAGE + ";\n\n");
        //写import(其实没有import全类名也可以导)
        for (Map.Entry<String, String> entry : AnnotatedList.entrySet()) {
            builder.append("import ").append(entry.getKey()).append(";\n");
        }
        builder.append("public class " + AnnotationClassNameTools.CLASS_NAME + " {\n\n");

        //array
        builder.append("\tpublic static final Class<?>[] allHookItemClass = {\n\t\t\t");
        for (Map.Entry<String, String> entry : AnnotatedList.entrySet()) {
            builder.append(entry.getKey()).append(".class,\n\t\t\t");
        }
        builder.append("};");
        //array end

        //build time
        builder.append("\n\tpublic static final String BUILD_TIME =\"").append(getTime()).append("\";");

        builder.append("\n}\n");

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("[start] Start building annotated Hook project class index");
        StringBuilder builder = new StringBuilder();
        for(TypeElement element : annotations)
            AnnotatedList = getAnnotatedClassList(element,roundEnv);
        addAsClassArray(builder);

        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile(AnnotationClassNameTools.CLASS_PACKAGE + "." + AnnotationClassNameTools.CLASS_NAME);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
        System.out.println("[End]Index classes have been built for all Hook projects");
        return true;
    }

    private Map<String, String> getAnnotatedClassList(TypeElement elements,RoundEnvironment roundEnv) {
        HashMap<String, String> map = new HashMap<>();
        Set<? extends Element> typeElements = roundEnv.getElementsAnnotatedWith(elements);
        for (Element element : typeElements) {
            //取到这个注解元素的包
            String packageName = element.getEnclosingElement().toString();
            //获取并拼接被注解的类名
            String className = packageName + "." + element.getSimpleName();
            System.out.println("[HookItem]" + className);
            map.put(className, null);
        }
        return map;
    }


    public static String getTime() {
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd"),
                df3 = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        String TimeMsg1 = df1.format(calendar.getTime()),
                TimeMsg3 = df3.format(calendar.getTime());
        if (TimeMsg1.contains("-0")) {
            TimeMsg1 = TimeMsg1.replace("-0", "-");
        }
        return TimeMsg1 + " " + TimeMsg3;
    }

}

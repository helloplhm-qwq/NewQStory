package lin.xposed.javaibrary;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * 用来自动处理注解
 */
@AutoService(Process.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)

//只处理这个注解
@SupportedAnnotationTypes("lin.xposed.hook.load.declarelabels.HookItem")
public class HookAnnotationScanner extends AbstractProcessor {
    private static final String SUFFIX = "AutoClass";
    //文件类 用于生成文件
    private Filer filer;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        filer = environment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //循环所有注解类型元素

        for (TypeElement typeElements : annotations) {
            StringBuffer fileHeader = new StringBuffer();
            //获取使用了typeElements这个注解的元素
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElements)) {
                //获取被注解的包名
                PackageElement packageElement = (PackageElement) element.getEnclosingElement();
                String className = packageElement.getQualifiedName().toString();
                System.out.println("add Item " + className);
                analysisAnnotated(element);
            }
        }
        return true;
    }

    /**
     * 生成java文件
     *
     * @param classElement 注解
     */
    private void analysisAnnotated(Element classElement) {

    }
}

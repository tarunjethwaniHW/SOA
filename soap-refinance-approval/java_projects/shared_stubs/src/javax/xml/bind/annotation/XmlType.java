package javax.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XmlType {
    String name() default "##default";
    String namespace() default "##default";
    String[] propOrder() default {};
}

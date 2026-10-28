package javax.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElement {
    boolean required() default false;
    String namespace() default "##default";
}

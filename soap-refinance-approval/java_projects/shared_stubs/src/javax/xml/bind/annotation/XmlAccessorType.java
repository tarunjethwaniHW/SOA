package javax.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XmlAccessorType {
    XmlAccessType value();
}

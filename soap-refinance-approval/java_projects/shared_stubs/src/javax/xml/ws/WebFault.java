package javax.xml.ws;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WebFault {
    String name() default "";
    String targetNamespace() default "";
}

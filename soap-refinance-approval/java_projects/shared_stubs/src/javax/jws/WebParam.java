package javax.jws;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WebParam {
    String name() default "";
    String targetNamespace() default "";
    String partName() default "";
}

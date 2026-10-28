package javax.xml.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Retention(RUNTIME)
@Target({FIELD, METHOD, PACKAGE})
public @interface XmlSchemaType {
    String name();
    String namespace() default "http://www.w3.org/2001/XMLSchema";
}

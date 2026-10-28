package javax.jws.soap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SOAPBinding {
    public enum ParameterStyle { BARE, WRAPPED }
    ParameterStyle parameterStyle() default ParameterStyle.WRAPPED;
}

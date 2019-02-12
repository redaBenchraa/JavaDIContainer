package BonjourDI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface Inject {
    Class defaultImplementation() default Class.class;
    Class[] defaultImplementations() default {};
}
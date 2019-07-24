package br.com.rooting.liquid.mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD , TYPE})
@Retention(RUNTIME)
public @interface Alias {

    String value();

    boolean root() default false;

}

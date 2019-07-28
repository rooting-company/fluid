package br.com.rooting.liquid.converter.annotation;

import br.com.rooting.liquid.converter.LiquifyConverter;
import br.com.rooting.liquid.converter.SolidifyConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
public @interface Converter {

    Class<? extends LiquifyConverter> liquify() default LiquifyConverter.class;

    Class<? extends SolidifyConverter> solidify() default SolidifyConverter.class;

}


package com.hf.healthfriend.global.util.mapping;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MappingAttribute {

    String target();

    boolean setNull() default true;

    boolean reverseSetNull() default true;
}

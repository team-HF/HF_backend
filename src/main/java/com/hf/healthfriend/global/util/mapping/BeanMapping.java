package com.hf.healthfriend.global.util.mapping;

import java.lang.annotation.*;

/**
 * 서로 다른 attribute를 가진 두 Bean을 매핑하기 위한 어노테이션
 *
 * @author PGD
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface BeanMapping {

    Class<?> value();
}

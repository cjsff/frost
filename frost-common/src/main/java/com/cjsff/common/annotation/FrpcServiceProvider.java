package com.cjsff.common.annotation;

import java.lang.annotation.*;

/**
 * @author rick
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FrpcServiceProvider {
  Class<?> interfaceClass() default void.class;
}

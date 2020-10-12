package com.cjsff.common.annotation;

import java.lang.annotation.*;

/**
 * @author rick
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface FrpcServiceConsumer {
  String url();
}

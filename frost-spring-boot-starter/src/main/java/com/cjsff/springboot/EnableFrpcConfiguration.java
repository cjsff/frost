package com.cjsff.springboot;

import java.lang.annotation.*;

/**
 * @author rick
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableFrpcConfiguration {
}

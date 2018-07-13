package com.iot.nero.middleware.dfs.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/13
 * Time   10:57 AM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface Service {

    String name() default "";

    String description() default "";
}

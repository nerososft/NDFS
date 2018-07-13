package com.iot.nero.middleware.dfs.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/6/27
 * Time   2:36 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Inject {

}

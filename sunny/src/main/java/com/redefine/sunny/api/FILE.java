package com.redefine.sunny.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ningdai on 16/7/21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface FILE {
    String value() default "";
    boolean optional() default false;
    String fileType() default "application/octet-stream";
}

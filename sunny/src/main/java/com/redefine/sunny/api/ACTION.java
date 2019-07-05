package com.redefine.sunny.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * if HTTP_METHOD is GET, set the post is false
 * Created by ning.dai on 16/7/21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ACTION {
    String value() default "";

    HttpMethod method() default HttpMethod.POST;
}

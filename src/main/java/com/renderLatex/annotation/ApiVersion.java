package com.renderLatex.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add API versioning information to a controller or controller method.
 *
 * The specified version(s) are appended to the controller's base path (i.e.
 * before the individual route paths).
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    String[] value();
}

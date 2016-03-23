package com.shinado.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.SOURCE)
@Target({TYPE, METHOD})
public @interface TargetVersion {

    public int value();

}

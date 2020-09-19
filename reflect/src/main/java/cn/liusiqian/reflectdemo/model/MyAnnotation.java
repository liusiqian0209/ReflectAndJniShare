package cn.liusiqian.reflectdemo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MyAnnotation {
}

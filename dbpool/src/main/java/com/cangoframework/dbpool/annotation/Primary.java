package com.cangoframework.dbpool.annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.FIELD)
public @interface Primary {
	public boolean value() default true;
	public boolean insert() default true;
	public boolean select() default true;
}

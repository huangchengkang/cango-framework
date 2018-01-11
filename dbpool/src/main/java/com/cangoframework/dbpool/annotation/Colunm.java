package com.cangoframework.dbpool.annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.FIELD)
public @interface Colunm {
	public String value();//数据库字段名称
	public boolean is() default true;//是否是数据库对应表字段
}

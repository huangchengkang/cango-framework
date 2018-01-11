package com.cangoframework.dbpool.annotation;
import java.lang.annotation.*;
@Inherited
@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface Table {
	public String value();//对应的表名
	public String[] primary() default {"id"};//主键
	public boolean all() default true;//该类中是否所有字段都是表字段（包括没有@Colunm标识的，此时表字段名对应类的字段名）
}

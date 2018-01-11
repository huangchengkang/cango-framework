package com.cangoframework.task;

import java.lang.annotation.*; 


@Retention(RetentionPolicy.RUNTIME)  
public @interface ExecuteProperty {
	String name();
	boolean allowNull();
}
package com.cangoframework.task.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cangoframework.task.ExecuteUnit;
import com.cangoframework.task.TaskBuilder;

/**
 * Task自定义工具类
 * @author kancy
 * create at 2016/09/21
 */
public class Tools {
	
	/**
	 * 获取输入流
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getInputStream(String filePath) throws Exception {
		InputStream in = null;
		if(StringX.isEmpty(filePath))
			return in;
		if(filePath.startsWith("classpath:")){
			in = TaskBuilder.class.getClassLoader().getResourceAsStream(
					filePath.replace("classpath:", "").trim());
		}else{
			in = new FileInputStream(filePath);
		}
		return in;
		
	}
	  /**
	   * 获取ExecuteUnit的字段值 
	   * add by kancy at 2016/09/20
	   */
	public static void autoSetFieldValue(ExecuteUnit unit) {
		try {
			Class<? extends ExecuteUnit> c = unit.getClass();
			ArrayList<String> fList = new ArrayList<String>();
			Field[] fields = c.getDeclaredFields();
			for (Field field : fields) {
				fList.add(field.getName());
			}
			
			Method[] methods = c.getDeclaredMethods();
			String mName =null;
			String fName =null;
			for (int i = 0; i < methods.length; i++) {
				mName = methods[i].getName();
				if(mName.startsWith("set")){
					fName = mName.substring(3,4).toLowerCase()+mName.substring(4);
					if(!fList.contains(fName)) break;
					Class<?>[] parameterTypes = methods[i].getParameterTypes();
					if(parameterTypes.length==1){
						switch (parameterTypes[0].getName()) {
						case "java.lang.String":methods[i].invoke(unit, unit.getProperty(fName));
							break;
						case "java.lang.Double":methods[i].invoke(unit, unit.getProperty(fName,0.0d));
							break;
						case "double":methods[i].invoke(unit, unit.getProperty(fName,0.0d));
							break;
						case "java.lang.Float":methods[i].invoke(unit, unit.getProperty(fName,0.0f));
							break;
						case "float":methods[i].invoke(unit, unit.getProperty(fName,0.0f));
							break;
						case "java.lang.Integer":methods[i].invoke(unit, unit.getProperty(fName,0));
							break;
						case "int":methods[i].invoke(unit, unit.getProperty(fName,0));
							break;
						case "java.lang.Long":methods[i].invoke(unit, unit.getProperty(fName,0L));
							break;
						case "long":methods[i].invoke(unit, unit.getProperty(fName,0L));
							break;
						case "java.lang.Boolean":methods[i].invoke(unit, unit.getProperty(fName,false));
							break;
						case "boolean":methods[i].invoke(unit, unit.getProperty(fName,false));
							break;
						case "java.util.Date":methods[i].invoke(unit, unit.getProperty(fName,new java.util.Date()));
							break;
						case "java.math.BigDecimal":methods[i].invoke(unit, new BigDecimal(unit.getProperty(fName,"0.0")));
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean setPropertyX(Object paramObject, String paramString1,
			String paramString2, boolean paramBoolean) {
		if (paramBoolean) {
			String str = paramObject.getClass().getName() + ".";
			if (!(paramString1.startsWith(str)))
				return false;
		}
		try {
			setObjectProperty(paramObject, paramString1, String.class,
					paramString2, paramBoolean);
			return true;
		} catch (Exception localException8) {
			try {
				setObjectProperty(paramObject, paramString1, Date.class,
						StringX.parseDate(paramString2), paramBoolean);
				return true;
			} catch (Exception localException7) {
				try {
					setObjectProperty(paramObject, paramString1, Boolean.TYPE,
							new Boolean(StringX.parseBoolean(paramString2)),
							paramBoolean);
					return true;
				} catch (Exception localException3) {
					if ((paramString2 == null) || (paramString2.equals("")))
						return false;
				}
				try {
					setObjectProperty(paramObject, paramString1, Integer.TYPE,
							Integer.valueOf(paramString2), paramBoolean);
					return true;
				} catch (Exception localException6) {
					try {
						setObjectProperty(paramObject, paramString1, Long.TYPE,
								Long.valueOf(paramString2), paramBoolean);
						return true;
					} catch (Exception localException5) {
						try {
							setObjectProperty(paramObject, paramString1,
									Byte.TYPE, Byte.valueOf(paramString2),
									paramBoolean);
							return true;
						} catch (Exception localException4) {
							try {
								setObjectProperty(paramObject, paramString1,
										Character.TYPE, new Character(
												paramString2.charAt(0)),
										paramBoolean);
								return true;
							} catch (Exception localException1) {
								try {
									setObjectProperty(paramObject,
											paramString1, Double.TYPE,
											Double.valueOf(paramString2),
											paramBoolean);
									return true;
								} catch (Exception localException2) {
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private static void setObjectProperty(Object paramObject1,
			String paramString, Class paramClass, Object paramObject2,
			boolean paramBoolean) throws SecurityException,
			IllegalArgumentException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		String str1 = null;
		Class localClass = paramObject1.getClass();
		if (paramBoolean) {
			if (!(paramString.startsWith(localClass.getName() + ".")))
				throw new IllegalArgumentException("Class not match: "
						+ paramString);
			str1 = paramString.substring(localClass.getName().length() + 1);
		} else {
			str1 = paramString;
		}
		String str2 = "set" + str1.substring(0, 1).toUpperCase()
				+ str1.substring(1);
		Method localMethod = localClass.getMethod(str2,
				new Class[] { paramClass });
		localMethod.invoke(paramObject1, new Object[] { paramObject2 });
	}
	
	public static boolean isEmpty(String str){
		return str==null||"".equals(str);
	}
	
}
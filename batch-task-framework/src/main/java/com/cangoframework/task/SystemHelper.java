package com.cangoframework.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemHelper {
	
	private static Pattern varPattern = Pattern.compile("\\$\\{(?:)([^\\{\\}][^\\}]*)\\}");
	private static String replaceVar(String paramString) {
		Matcher localMatcher = null;
		StringBuffer localStringBuffer = new StringBuffer();
		localMatcher = varPattern.matcher(paramString);
		while (localMatcher.find()) {
			String str = null;
			if (localMatcher.group(1) != null)
				str = System.getProperty(localMatcher.group(1).trim());

			if (str == null)
				str = "";
			localMatcher.appendReplacement(localStringBuffer, str);
		}
		localMatcher.appendTail(localStringBuffer);
		return localStringBuffer.toString();
	}
	
	private static String replaceEnvVar(String paramString) {
		return replaceVar(paramString);
	}
	private static String replaceComment(String paramString) {
		String str = "\\{#[^\\}]*\\}";
		return paramString.replaceAll(str, "");
	}
	public static String replacePropertyTags(String paramString) {
		return replaceEnvVar(replaceComment(paramString));
	}
	public static void setProperty(String name,String value){
		System.setProperty(name, replacePropertyTags(value));
	}
	public static String getProperty(String key){
		return System.getProperty(key);
	}
	public static String getProperty(String key,String def){
		return System.getProperty(key, def);
	}
	public static boolean getProperty(String paramName,boolean def){
		String booleanValue = getProperty(paramName,String.valueOf(def));
		return Boolean.parseBoolean(booleanValue);
	}
	public static int getProperty(String paramName,int def){
		String intValue = getProperty(paramName,String.valueOf(def));
		return Integer.parseInt(intValue);
	}
	public static double getProperty(String paramName,double def){
		String doubleValue = getProperty(paramName,String.valueOf(def));
		return Double.parseDouble(doubleValue);
	}

}
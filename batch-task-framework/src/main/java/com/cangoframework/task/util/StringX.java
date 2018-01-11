package com.cangoframework.task.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringX {
	public static Date parseDate(String paramString) {
		if (paramString == null)
			return null;
		Date localDate = null;
		String str = null;
		char[] arrayOfChar = { '/', '-', '.' };
		if (paramString.length() < 8)
			return localDate;
		if (paramString.length() > 10)
			paramString = paramString.substring(0, 10);
		for (int i = 0; i < arrayOfChar.length; ++i) {
			int j = paramString.indexOf(arrayOfChar[i]);
			if (j < 0)
				continue;
			if (j == 4) {
				str = "yyyy" + arrayOfChar[i] + "M" + arrayOfChar[i] + "d";
				break;
			}
			if (j < 4) {
				str = "M" + arrayOfChar[i] + "d" + arrayOfChar[i] + "y";
				break;
			}
			return null;
		}
		if (str == null)
			if (paramString.substring(0, 2).compareTo("12") > 0)
				str = "yyyyMMdd";
			else
				str = "MMddyyyy";
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(str);
		try {
			localDate = localSimpleDateFormat.parse(paramString);
		} catch (ParseException localParseException) {
			localDate = null;
		}
		return localDate;
	}

	public static boolean parseBoolean(String paramString) {
		if(paramString != null){
			return "true".equalsIgnoreCase(paramString);
		}
		return false;
	}

	public static String trimStart(String paramString) {
		String str = null;
		int i = 0;
		if (paramString != null) {
			for (i = 0; i < paramString.length(); ++i)
				if (!(Character.isWhitespace(paramString.charAt(i))))
					break;
			str = (i < paramString.length()) ? paramString.substring(i) : "";
		}
		return str;
	}

	public static String trimEnd(String paramString) {
		String str = null;
		int i = 0;
		if (paramString != null) {
			for (i = paramString.length() - 1; i >= 0; --i)
				if (!(Character.isWhitespace(paramString.charAt(i))))
					break;
			str = (i >= 0) ? paramString.substring(0, i + 1) : "";
		}
		return str;
	}

	public static String trimAll(String paramString) {
		String str = null;
		if (paramString != null)
			str = trimEnd(trimStart(paramString));
		return str;
	}

	public static boolean isEmpty(String paramString) {
		return ((paramString == null) || (paramString.equals("")));
	}

	public static boolean isSpace(String paramString) {
		if (paramString == null)
			return true;
		int i = 1;
		for (int j = 0; j < paramString.length(); ++j) {
			if (Character.isSpaceChar(paramString.charAt(j)))
				continue;
			i = 0;
			break;
		}
		return i==1;
	}
	
	public static String[] getArray(String str) {
		String[] arr = null;
		if(isEmpty(str)){
			return arr;
		}
		arr = str.split(",");
		return arr;
	}

}
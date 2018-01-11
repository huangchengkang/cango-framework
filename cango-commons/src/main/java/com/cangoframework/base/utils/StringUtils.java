package com.cangoframework.base.utils;

/**
 * Created by cango on 2018/1/11.
 */
public class StringUtils {

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

}

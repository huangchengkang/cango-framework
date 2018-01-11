package com.cangoframework.base.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by cango on 2018/1/11.
 */
public class Tools {

    /**
     * 获取输入流
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getInputStream(String filePath) throws FileNotFoundException {
        InputStream in = null;
        if (StringUtils.isEmpty(filePath))
            return in;
        if (filePath.startsWith("classpath:")) {
            in = Tools.class.getClassLoader().getResourceAsStream(
                    filePath.replace("classpath:", "").trim());
        } else {
            in = new FileInputStream(filePath);
        }
        return in;
    }
}

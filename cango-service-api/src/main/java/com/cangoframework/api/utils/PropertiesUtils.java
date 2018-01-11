package com.cangoframework.api.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by cango on 2018/1/10.
 */
public class PropertiesUtils {

    public static InputStream getInputStream(String filepath) throws FileNotFoundException {
        if(filepath.startsWith("classpath:")){
            return PropertiesUtils.class.getClassLoader()
                    .getResourceAsStream(filepath.replace("classpath:","").trim());
        }else{
            return new FileInputStream(filepath);
        }
    }

    public static Properties load(String filepath) throws IOException {
        InputStream inputStream = getInputStream(filepath);
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }
}

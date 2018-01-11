package com.cangoframework.api.common;

import com.cangoframework.api.utils.PropertiesUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by cango on 2018/1/10.
 */
public class ServiceApiProperties {

    private static Properties properties;
    private static String configPath = "classpath:service-api.properties";

    static{
        loadProperties();
    }

    public static void loadProperties(){
        try {
            properties = PropertiesUtils.load(configPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }

    public static String getProperty(String name,String defaultValue) {
        return properties.getProperty(name,defaultValue);
    }

    public static Integer getProperty(String name,Integer defaultValue) {
        String v = properties.getProperty(name);
        if(v==null||"".equals(v)){
            return defaultValue;
        }
        return Integer.parseInt(v);
    }
    public static Double getProperty(String name,Double defaultValue) {
        String v = properties.getProperty(name);
        if(v==null||"".equals(v)){
            return defaultValue;
        }
        return Double.parseDouble(v);
    }
    public static Boolean getProperty(String name,Boolean defaultValue) {
        String v = properties.getProperty(name);
        if(v==null||"".equals(v)){
            return defaultValue;
        }
        return "true".equalsIgnoreCase(v.trim());
    }

    public static void setConfigPath(String configPath) {
        ServiceApiProperties.configPath = configPath;
    }
}

package com.cangoframework.api.service;

import com.cangoframework.api.apps.sms.SimpleSmsSender;
import com.cangoframework.api.common.ServiceApiProperties;

import java.util.List;

/**
 * Created by cango on 2018/1/10.
 */
public class SmsServiceApi {
    private static SimpleSmsSender smsSender;
    static{
        smsSender = new SimpleSmsSender();
        String url = ServiceApiProperties.getProperty("sms.url", "https://tech.autohomefinance.com/msgcenter/msg/send?type=30&mobile={phone}&content={content}");
        smsSender.setEncoding(ServiceApiProperties.getProperty("sms.encoding","UTF-8"));
        smsSender.setRetryTimes(ServiceApiProperties.getProperty("sms.retry",0));
        smsSender.setPhoneSplitChar(ServiceApiProperties.getProperty("sms.split",","));
        smsSender.setSmsStoreFilePath(ServiceApiProperties.getProperty("sms.store","tech.autohomefinance.com.cer"));
        smsSender.setSmsUrl(url);
    }
    public static void sendSms(String content, String phones){
        smsSender.sendSms(content,phones);
    }

    public static void sendSms(String content, String... phones){
        smsSender.sendSms(content,phones);
    }

    public static void sendSms(String content, List<String> phones){
        smsSender.sendSms(content,phones);
    }

    public static boolean send(String content, String phones) throws Exception{
        return smsSender.send(content,phones);
    }
}

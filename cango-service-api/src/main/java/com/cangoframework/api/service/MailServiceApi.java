package com.cangoframework.api.service;

import com.cangoframework.api.apps.email.AuthConfig;
import com.cangoframework.api.apps.email.SimpleMailSender;
import com.cangoframework.api.common.ServiceApiProperties;

/**
 * Created by cango on 2018/1/10.
 */
public class MailServiceApi {

    public static final String TYPE_TXT= "1";
    public static final String TYPE_HTML= "2";

    public static void sendEmail(String type , String subject ,String content , String toAddresses) {
        try {
            send(type, subject, content, toAddresses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void send(String type , String subject ,String content , String toAddresses) throws Exception {
        String host = ServiceApiProperties.getProperty("email.host", "smtp.mxhichina.com");
        String port = ServiceApiProperties.getProperty("emial.port", "25");
        String username = ServiceApiProperties.getProperty("emial.username", "fintech@cangoonline.com");
        String password = ServiceApiProperties.getProperty("email.password", "cangoZHU1234");
        AuthConfig authConfig = new AuthConfig(host, port, username, password);
        sendEmail(type,subject,content,toAddresses,authConfig);

    }
    public static void sendEmail(String type , String subject ,String content , String toAddresses ,
                                 AuthConfig config) {
        try {
            send(type, subject, content, toAddresses, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void send(String type , String subject ,String content , String toAddresses ,
                                 AuthConfig config) throws Exception {
        SimpleMailSender.sendEmail(config.getMailServerHost(),config.getMailServerPort(),
                config.getUserName(),config.getPassword(),toAddresses,subject,content,type);
    }
}

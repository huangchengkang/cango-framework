package com.cangoframework.api.apps.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cangoframework.api.utils.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by kancy on 2017\12\12 0012.
 */
public class SimpleSmsSender {
    private static Logger logger = LoggerFactory.getLogger(SimpleSmsSender.class);
    private int retryTimes = 0;
    private int timeout = 1000;
    private int waitTime = 1000;
    private String phoneSplitChar = ",";
    private String encoding = "UTF-8";
    private String smsUrl = "https://tech.autohomefinance.com/msgcenter/msg/send?type=30&mobile={phone}&content={content}";
    private String smsStoreFilePath = "classpath:tech.autohomefinance.com.cer";

    public void sendSms(String content , String phones) {
        setSmsStore();
        if(phones==null||"".equals(phones)){
            logger.warn("电话号码不能是空的!");
            return;
        }
        String[] phoneArray = phones.split(phoneSplitChar);
        for (String phone: phoneArray) {
            sendOneByRetry(content, phone);
        }
    }

    public void sendSms(String content, String... phones) {
        if(phones == null||phones.length <= 0){
            logger.warn("电话号码不能是空的!");
            return;
        }
        for (String phone: phones){
            sendSms(content, phone);
        }
    }

    public void sendSms(String content, List<String> phones) {
        if(phones == null||phones.size() <= 0){
            logger.warn("电话号码不能是空的!");
            return;
        }
        for (String phone: phones){
            sendSms(content, phone);
        }
    }

    public boolean send(String content, String phones) throws Exception {
        setSmsStore();
        if(phones==null||"".equals(phones)){
            logger.warn("电话号码不能是空的!");
            return false;
        }
        String[] phoneArray = phones.split(phoneSplitChar);
        for (String phone: phoneArray) {
            //如果有异常，return false
            sendOneThrowException(content, phone);
        }
        return true;
    }

    private void sendOneByRetry(String content, String phone){
        for (int index = 0; index < (retryTimes+1) ; index++) {
            boolean flag = sendOne(content, phone);
            if(flag){
                break;
            }
            //延迟等待时间
            sleep(waitTime);
        }
    }

    /**
     * 发送一条短信 - 抛出异常
     * @param content
     * @param phone
     * @return
     * @throws Exception
     */
    private boolean sendOneThrowException(String content, String phone) throws Exception {
        boolean isSucceed = false;
        String encodeContent = URLEncoder.encode(content, "UTF-8");
        String requestUrl = smsUrl.replace("{phone}",phone).replace("{content}",encodeContent);
        String result = getRequest(requestUrl, timeout);
        JSONObject jsonObject = JSON.parseObject(result);
        isSucceed = jsonObject.getBooleanValue("body");
        if (isSucceed) {
            logger.info("短信[{},{}]发送成功...",phone,content);
        }else{
            logger.info("短信[{},{}]发送失败...",phone,content);
        }
        return isSucceed;
    }

    /**
     * 发送一条短信 - 没有异常
     * @param content
     * @param phone
     * @return
     */
    private boolean sendOne(String content, String phone) {
        boolean isSucceed = false;
        try {
            isSucceed = sendOneThrowException(content, phone);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("短信[{},{}]发送失败...",phone,content);
        }
        return isSucceed;
    }

    private String getRequest(String url, int timeOut) throws Exception {
        URL u = new URL(url);
        if("https".equalsIgnoreCase(u.getProtocol())){
            SslUtils.ignoreSsl();
        }
        URLConnection conn = u.openConnection();
        conn.setConnectTimeout(timeOut);
        conn.setReadTimeout(timeOut);
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringWriter sw = new StringWriter();
        String line = null;
        while((line = br.readLine())!=null){
            sw.write(line+System.lineSeparator());
            sw.flush();
        }
        if(br!=null) br.close();
        if(sw!=null) sw.close();

        return sw.toString();
    }


    /**
     * 设置证书
     */
    private void setSmsStore() {
        try {
            String property = System.getProperty("javax.net.ssl.trustStore");
            if(property==null||"".equals(property)){
                String filepath = null;
                if(smsStoreFilePath.startsWith("classpath:")){
                    filepath = PathUtils.getClassFilePath(smsStoreFilePath.replace("classpath:","").trim());
                    File file = new File(filepath);
                    if(file.exists()&&!file.isDirectory()) {
                        filepath = file.getAbsolutePath();
                        System.setProperty("javax.net.ssl.trustStore", filepath);
                    }else{
                        logger.warn("指定的短信证书["+smsStoreFilePath+"]不存在.");
                    }
                }else{
                    File file = new File(smsStoreFilePath);
                    if(file.exists()&&!file.isDirectory()) {
                        filepath = file.getAbsolutePath();
                        System.setProperty("javax.net.ssl.trustStore", filepath);
                    }else{
                        logger.warn("指定的短信证书["+smsStoreFilePath+"]不存在.");
                    }
                }

            }
        } catch (Exception e) {
        }
    }

    private void sleep(int waitTime) {
        try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        SimpleSmsSender.logger = logger;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public String getPhoneSplitChar() {
        return phoneSplitChar;
    }

    public void setPhoneSplitChar(String phoneSplitChar) {
        this.phoneSplitChar = phoneSplitChar;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getSmsUrl() {
        return smsUrl;
    }

    public void setSmsUrl(String smsUrl) {
        this.smsUrl = smsUrl;
    }

    public String getSmsStoreFilePath() {
        return smsStoreFilePath;
    }

    public void setSmsStoreFilePath(String smsStoreFilePath) {
        this.smsStoreFilePath = smsStoreFilePath;
    }
}

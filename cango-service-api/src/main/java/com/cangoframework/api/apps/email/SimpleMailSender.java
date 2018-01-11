package com.cangoframework.api.apps.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

/**
 * 邮件发送器
 *
 * @author FH QQ 313596790[青苔]
 */
public class SimpleMailSender {
    private static Logger logger = LoggerFactory.getLogger(SimpleMailSender.class);

    /**
     * 以文本格式发送邮件
     *
     * @param mailInfo 待发送的邮件的信息
     */
    public static boolean sendTextMail(MailSenderInfo mailInfo) throws Exception {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        logger.info("构造一个发送邮件的session");
        // 根据session创建一个邮件消息
        Message mailMessage = new MimeMessage(sendMailSession);
        // 创建邮件发送者地址
        Address from = new InternetAddress(mailInfo.getFromAddress());
        // 设置邮件消息的发送者
        mailMessage.setFrom(from);
        // 创建邮件的接收者地址，并设置到邮件消息中
        String[] toAddresses = mailInfo.getToAddress();
        Address[] tos = new Address[toAddresses.length];
        for (int i = 0; i < toAddresses.length; i++) {
            tos[i] = new InternetAddress(toAddresses[i]);
        }
        Address to = new InternetAddress();
        mailMessage.addRecipients(Message.RecipientType.TO, tos);
        // 设置邮件消息的主题
        mailMessage.setSubject(mailInfo.getSubject());
        // 设置邮件消息发送的时间
        mailMessage.setSentDate(new Date());
        // 设置邮件消息的主要内容
        String mailContent = mailInfo.getContent();
        mailMessage.setText(mailContent);
        // 发送邮件
        Transport.send(mailMessage);
        logger.info("发送成功！");
        return true;
    }

    /**
     * 以HTML格式发送邮件
     *
     * @param mailInfo 待发送的邮件信息
     */
    public static boolean sendHtmlMail(MailSenderInfo mailInfo) throws Exception {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        //如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        // 根据session创建一个邮件消息
        Message mailMessage = new MimeMessage(sendMailSession);
        // 创建邮件发送者地址
        Address from = new InternetAddress(mailInfo.getFromAddress());
        // 设置邮件消息的发送者
        mailMessage.setFrom(from);
        // 创建邮件的接收者地址，并设置到邮件消息中
        // 创建邮件的接收者地址，并设置到邮件消息中
        String[] toAddresses = mailInfo.getToAddress();
        Address[] tos = new Address[toAddresses.length];
        for (int i = 0; i < toAddresses.length; i++) {
            tos[i] = new InternetAddress(toAddresses[i]);
        }
        Address to = new InternetAddress();
        mailMessage.addRecipients(Message.RecipientType.TO, tos);
        // 设置邮件消息的主题
        mailMessage.setSubject(mailInfo.getSubject());
        // 设置邮件消息发送的时间
        mailMessage.setSentDate(new Date());
        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
        Multipart mainPart = new MimeMultipart();
        // 创建一个包含HTML内容的MimeBodyPart
        BodyPart html = new MimeBodyPart();
        // 设置HTML内容
        html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
        mainPart.addBodyPart(html);
        // 将MiniMultipart对象设置为邮件内容
        mailMessage.setContent(mainPart);
        // 发送邮件
        Transport.send(mailMessage);
        return true;
    }

    /**
     * @param SMTP    邮件服务器
     * @param PORT    端口
     * @param EMAIL   本邮箱账号
     * @param PAW     本邮箱密码
     * @param toEMAIL 对方箱账号
     * @param TITLE   标题
     * @param CONTENT 内容
     * @param TYPE    1：文本格式;2：HTML格式
     */
    public static void sendEmail(String SMTP, String PORT, String EMAIL, String PAW, String toEMAIL, String TITLE, String CONTENT, String TYPE) throws Exception {
        //这个类主要是设置邮件   
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost(SMTP);
        mailInfo.setMailServerPort(PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(EMAIL);
        mailInfo.setPassword(PAW);
        mailInfo.setFromAddress(EMAIL);
        mailInfo.setToAddress(toEMAIL);
        mailInfo.setSubject(TITLE);
        mailInfo.setContent(CONTENT);
        //这个类主要来发送邮件
        if ("1".equals(TYPE)) {
            sendTextMail(mailInfo);
        } else if("2".equals(TYPE))  {
            sendHtmlMail(mailInfo);
        }

    }
}
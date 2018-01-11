import com.cangoframework.api.apps.email.MailSenderInfo;
import com.cangoframework.api.apps.email.SimpleMailSender;
import com.cangoframework.api.apps.sms.SimpleSmsSender;
import com.cangoframework.api.service.SftpServiceApi;
import com.cangoframework.api.service.SmsServiceApi;
import com.cangoframework.api.utils.PathUtils;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Created by cango on 2018/1/10.
 */
public class TestMain {

    /*@Test
    public void testSendTextEmail(){
        //这个类主要是设置邮件
        MailSenderInfo mailInfo = new MailSenderInfo();
        mailInfo.setMailServerHost("smtp.mxhichina.com");
        mailInfo.setMailServerPort("25");
        mailInfo.setValidate(true);
        mailInfo.setUserName("fintech@cangoonline.com");
        mailInfo.setPassword("cangoZHU1234");//您的邮箱密码
        mailInfo.setFromAddress("fintech@cangoonline.com");
        mailInfo.setToAddress("793272861@qq.com","huangchengkang@cangoonline.com");
        mailInfo.setSubject("设置邮箱标题");
        mailInfo.setContent("<img src='http://image.uczzd.cn/14557541730805271374.jpg?id=0&from=export'/>");

        //这个类主要来发送邮件
        SimpleMailSender sms = new SimpleMailSender();

        try {
            sms.sendTextMail(mailInfo);//发送文体格式
            sms.sendHtmlMail(mailInfo);//发送html格式
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendSms2(){
        SmsServiceApi.sendSms("你好","18079637336");
    }

    @Test
    public void testSftpDownloadFile(){
        SftpServiceApi.downloadFile(
         "D:\\develop\\idea\\workspace\\cango\\cango-framework\\cango-service-api\\src\\test\\java\\test\\34\\1.xlsx"
        ,"/home/cango/testSftp/vab.xlsx");
    }

    @Test
    public void testSftpUploadFile(){
          SftpServiceApi.uploadFile("C:\\Users\\cango\\Desktop\\使用Excel VBA.xlsx",
                "/home/cango/testSftp/vab.xlsx");
    }

    @Test
    public void testInputStream(){
        InputStream inputStream = TestMain.class.getClassLoader().getResourceAsStream("log4j.properties");

        URL resource = TestMain.class.getClassLoader().getResource("log4j.properties");

        System.out.println(resource.getPath());
        System.out.println(PathUtils.getClassFilePath("log4j.properties"));

        System.out.println(inputStream);

        System.out.println(PathUtils.getProjectPath());
        System.out.println(PathUtils.getClassPath());

    }*/

}

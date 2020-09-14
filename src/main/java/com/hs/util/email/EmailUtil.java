package com.hs.util.email;

import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;


/**
 * @Author: songhao
 * @Date: 2020/9/14
 * @Description: 类说明
 */
public class EmailUtil {

    static Logger logger = Logger.getLogger(EmailUtil.class);

    public static final String FROM_PERSONAL = "spiderMan";
    public static final String TO_PERSONAL = "蜘蛛";

    public static void main(String[] args) {
        // 封装邮件发送信息
        EmailAccount myEmailAccount = null;
        String receiveMailAccount = "425008064@qq.com";

        //发送简单邮件
        sendSimpleMessage(myEmailAccount,receiveMailAccount,"网易邮箱","您的账号注册成功！~");

        //发送复杂邮件
        //sendMixMessage(myEmailAccount,receiveMailAccount,"工作报告","20180409的数据统计报表如下","C:\\Users\\songhao\\Desktop\\Capture001.png","C:\\Users\\songhao\\Desktop\\MinisoRepairDataCli.log");
    }

    /**
     * 发送一封带图片和附件的邮件
     * @param sendAccount
     * @param receiveMail
     * @param subject
     * @param content
     * @param imagepath
     * @param attachpath
     * @return
     */
    public static boolean sendMixMessage(EmailAccount sendAccount,String receiveMail,String subject,String content,String imagepath,String attachpath){
        boolean isSend = true;
        try{
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties();                    // 参数配置
            props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
            props.setProperty("mail.smtp.host", sendAccount.getMyEmailSMTPHost());   // 发件人的邮箱的 SMTP 服务器地址
            props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

            // 开启 SSL 连接, 以及更详细的发送步骤请看上一篇: 基于 JavaMail 的 Java 邮件发送：简单邮件发送

            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getInstance(props);
            session.setDebug(false);

            // 3. 创建一封邮件
            MimeMessage message = createMixMessage(session, sendAccount.getMyEmailAccount(), receiveMail,subject,content,imagepath,attachpath);

            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();

            // 5. 使用 邮箱账号 和 密码 连接邮件服务器
            //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
            transport.connect(sendAccount.getMyEmailAccount(), sendAccount.getMyEmailPassword());

            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());

            // 7. 关闭连接
            transport.close();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            isSend = false;
        }
        return isSend;
    }

    /**
     * 发送一封简单的邮件信息
     * @param emailAccount
     * @param receiveMail
     * @param subject
     * @param content
     * @throws Exception
     */
    public static boolean sendSimpleMessage(EmailAccount emailAccount,String receiveMail,String subject,String content){
        boolean isSend = true;
        try{
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties();                    // 参数配置
            props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
            props.setProperty("mail.smtp.host", emailAccount.getMyEmailSMTPHost());   // 发件人的邮箱的 SMTP 服务器地址
            props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
            props.setProperty("mail.smtp.port","465");//这个端口可能需要修改
            props.setProperty("mail.smtp.ssl.enable", "true");//如果不行，这里需要设置一下

            // 开启 SSL 连接, 以及更详细的发送步骤请看上一篇: 基于 JavaMail 的 Java 邮件发送：简单邮件发送

            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getInstance(props);
            session.setDebug(false);

            // 3. 创建一封邮件
            MimeMessage message = createSimpleMessage(session, emailAccount.getMyEmailAccount(), receiveMail, subject, content);

            // 4. 根据 Session 获取邮件传输对象
            Transport transport = session.getTransport();

            // 5. 使用 邮箱账号 和 密码 连接邮件服务器
            //    这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错
            transport.connect(emailAccount.getMyEmailAccount(), emailAccount.getMyEmailPassword());

            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());

            // 7. 关闭连接
            transport.close();
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            isSend = false;
        }
        return isSend;
    }

    /**
     * 创建最简单的一封邮件
     * @param sendMail 发送者的信息
     * @param receiveMail 接收者的信息
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return
     * @throws Exception
     */
    private static MimeMessage createSimpleMessage(Session session, String sendMail, String receiveMail, String subject, String content) throws Exception {
        //3.创建对应的邮件信息
        MimeMessage message = new MimeMessage(session);     // 创建邮件对象

        //4.设置发送人和接收人
        // From: 发件人
        //    其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        //    真正要发送时, 邮箱必须是真实有效的邮箱。
        message.setFrom(new InternetAddress(sendMail, FROM_PERSONAL, "UTF-8"));
        // To: 收件人
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, TO_PERSONAL, "UTF-8"));
       /* //    To: 增加收件人（可选）
        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("dd@receive.com", "USER_DD", "UTF-8"));
        //    Cc: 抄送（可选）
        message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("ee@receive.com", "USER_EE", "UTF-8"));
        //    Bcc: 密送（可选）
        message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("ff@receive.com", "USER_FF", "UTF-8"));*/

        //5.Subject: 邮件主题
        message.setSubject(subject, "UTF-8");

        //6.Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");

        //7.设置显示的发件时间
        message.setSentDate(new Date());

        //8.保存前面的设置
        message.saveChanges();

        return message;
    }

    /**
     * 创建复杂的邮件
     * @param session
     * @param sendMail 发送者信息
     * @param receiveMail 接收者信息
     * @param subject 主题
     * @param content 内容
     * @param imagepath 图片本地地址
     * @param attachpath 附件本地地址
     * @return
     * @throws Exception
     */
    private static MimeMessage createMixMessage(Session session, String sendMail, String receiveMail,String subject,String content,String imagepath,String attachpath) throws Exception {
        //1.创建邮件对象
        MimeMessage message = new MimeMessage(session);

        //2.From: 发件人
        message.setFrom(new InternetAddress(sendMail, FROM_PERSONAL, "UTF-8"));

        //3.To: 收件人（可以增加多个收件人、抄送、密送）
        message.addRecipient(RecipientType.TO, new InternetAddress(receiveMail, TO_PERSONAL, "UTF-8"));

        //4.Subject: 邮件主题
        message.setSubject(subject, "UTF-8");

        //5.创建图片“节点”
        MimeBodyPart image = new MimeBodyPart();
        DataHandler dh = new DataHandler(new FileDataSource(imagepath)); // 读取本地文件
        image.setDataHandler(dh);                   // 将图片数据添加到“节点”
        image.setContentID("image_fairy_tail");     // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）

        // 6. 创建文本“节点”
        MimeBodyPart text = new MimeBodyPart();
        //    这里添加图片的方式是将整个图片包含到邮件内容中, 实际上也可以以 http 链接的形式添加网络图片
        text.setContent(content + "<br/><img src='cid:image_fairy_tail'/>", "text/html;charset=UTF-8");

        // 7. （文本+图片）设置 文本 和 图片 “节点”的关系（将 文本 和 图片 “节点”合成一个混合“节点”）
        MimeMultipart mm_text_image = new MimeMultipart();
        mm_text_image.addBodyPart(text);
        mm_text_image.addBodyPart(image);
        mm_text_image.setSubType("related");    // 关联关系

        // 8. 将 文本+图片 的混合“节点”封装成一个普通“节点”
        //    最终添加到邮件的 Content 是由多个 BodyPart 组成的 Multipart, 所以我们需要的是 BodyPart,
        //    上面的 mm_text_image 并非 BodyPart, 所有要把 mm_text_image 封装成一个 BodyPart
        MimeBodyPart text_image = new MimeBodyPart();
        text_image.setContent(mm_text_image);

        // 9. 创建附件“节点”
        MimeBodyPart attachment = new MimeBodyPart();
        DataHandler dh2 = new DataHandler(new FileDataSource(attachpath));  // 读取本地文件
        attachment.setDataHandler(dh2);                                             // 将附件数据添加到“节点”
        attachment.setFileName(MimeUtility.encodeText(dh2.getName()));              // 设置附件的文件名（需要编码）

        // 10. 设置（文本+图片）和 附件 的关系（合成一个大的混合“节点” / Multipart ）
        MimeMultipart mm = new MimeMultipart();
        mm.addBodyPart(text_image);
        mm.addBodyPart(attachment);     // 如果有多个附件，可以创建多个多次添加
        mm.setSubType("mixed");         // 混合关系

        // 11. 设置整个邮件的关系（将最终的混合“节点”作为邮件的内容添加到邮件对象）
        message.setContent(mm);

        // 12. 设置发件时间
        message.setSentDate(new Date());

        // 13. 保存上面的所有设置
        message.saveChanges();

        return message;

    }
}

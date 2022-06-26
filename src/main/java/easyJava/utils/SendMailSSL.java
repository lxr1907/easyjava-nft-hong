package easyJava.utils;

import com.sun.mail.smtp.SMTPTransport;
import easyJava.controller.websocket.TexasWS;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailSSL {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SendMailSSL.class);
    //        public static final String mail = "lijuede6197@gmail.com";
//    public static final String mailPass = "arpltillllqwztba";
//    public static final String host = "smtp.gmail.com";
//    public static final String port = "587";
    public static final String mail = "tokenwix@tokenwix.com";
    public static final String mailPass = "tangpeng918";
    public static final String host = "mail.gandi.net";
    public static final String port = "465";
//    public static final String mail = "418982099@qq.com";
//    public static final String mailPass = "kvxrylmawoqgbicc";
//    public static final String host = "smtp.qq.com";
//public static final String port = "587";

    public static void main(String[] args) {
        send("418982099@qq.com", "test", "test");
    }

    /**
     * 该方式qq已经成功验证
     */
    public static void send(String to, String subject, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.user", "tokenwix");
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.URLName.dontencode", false);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mail, mailPass);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);
            var transport = new SMTPTransport(session, new URLName("smtp", host, 465, null, "tokenwix", mailPass));
            logger.info("start send email subject:" + subject + ",to:" + to);
            transport.connect();
            transport.sendMessage(message, (Address[]) List.of(new InternetAddress(to)).toArray());
            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
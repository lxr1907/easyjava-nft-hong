package easyJava.utils;

import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailTLS {
    public static final String mail = "lijuede6197@gmail.com";
    public static final String mailPass = "arpltillllqwztba";
    public static final String host = "smtp.gmail.com";
    public static final String port = "587";

    public static void main(String[] args) {
        gmailSender("418982099@qq.com",
                "title", "content", "ssl");
    }


    /* * gmail邮箱SSL方式 */
    public static void gmailssl(Properties props) {
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.debug", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
    }

    //gmail邮箱的TLS方式
    public static void gmailtls(Properties props) {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
    }
    /* * 通过gmail邮箱发送邮件 */

    public static void gmailSender(String to, String title, String content, String ssl) { // Get a Properties object
        Properties props = new Properties(); //选择ssl方式
        if (ssl == null || ssl.equals("ssl")) {
            gmailssl(props);
        } else {
            gmailtls(props);
        }
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mail, mailPass);
            }
        }); // -- Create a new message --
        Message msg = new MimeMessage(session); // -- Set the FROM and TO fields
        try {
            msg.setFrom(new InternetAddress(mail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(title);
            msg.setText(content);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("Message sent.");
    }
}


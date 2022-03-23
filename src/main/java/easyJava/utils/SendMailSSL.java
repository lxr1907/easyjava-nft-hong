package easyJava.utils;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailSSL {
        public static final String mail = "lijuede6197@gmail.com";
    public static final String mailPass = "arpltillllqwztba";
    public static final String host = "smtp.gmail.com";
    public static final String port = "587";
//    public static final String mail = "418982099@qq.com";
//    public static final String mailPass = "kvxrylmawoqgbicc";
//    public static final String host = "smtp.qq.com";
//public static final String port = "587";

    public static void main(String[] args) {
        send("lijuede6197@gmail.com", "test", "test");
    }

    /**
     * 该方式qq已经成功验证
     */
    public static void send(String to, String subject, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);

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

            Transport.send(message);
            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
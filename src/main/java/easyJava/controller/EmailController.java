package easyJava.controller;

import easyJava.dao.master.HelloDao;
import easyJava.entity.ResponseEntity;
import jodd.mail.Email;
import jodd.mail.MailServer;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RestController
public class EmailController {
    @Autowired
    private HelloDao helloDao;
    @Autowired
    private easyJava.dao.second.Hello2Dao helloDao2;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    public static final String mail = "lijuede6197@gmail.com";
    public static final String mailPass = "lijue6197de";

    @RequestMapping("/emailTest")
    public ResponseEntity<?> helloRedis() {
//        var key = "helloRedis";
//        ResponseEntity<?> ret = (ResponseEntity<?>) redisTemplate.opsForValue().get(key);
//        if (ret == null) {
//            var str = helloDao.getHello();
//            ret = new ResponseEntity(str);
//            redisTemplate.opsForValue().set(key, ret, 10, TimeUnit.DAYS);
//        }
//        return ret;
        send();
        return new ResponseEntity();
    }

    public static void main(String args[]) {
        send();
    }

    public static void send() {

        SmtpServer smtpServer = MailServer.create()
                .ssl(true)
//                .port(587)

                .host("smtp.gmail.com")
                .auth(mail, mailPass)
//                .auth("lijuede6197@gmail.com", "lijue6197de")
                .buildSmtpMailServer();

        Email email = Email.create()
                .from("lijuede6197@gmail.com")
                .to("418982099@qq.com")
                .subject("hello")
                .textMessage("Hello world!");

        SendMailSession session = smtpServer.createSession();
        session.open();
        session.sendMail(email);
        session.close();
    }

}

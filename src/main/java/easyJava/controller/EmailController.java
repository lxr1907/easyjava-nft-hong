package easyJava.controller;

import easyJava.dao.master.HelloDao;
import easyJava.entity.ResponseEntity;
import easyJava.utils.SendMailSSL;
import jodd.mail.*;
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
        SendMailSSL.send();
        return new ResponseEntity();
    }

}

package easyJava.controller;

import easyJava.dao.master.HelloDao;
import easyJava.entity.ResponseEntity;
import easyJava.utils.SendMailSSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    @Autowired
    private HelloDao helloDao;
    @Autowired
    private easyJava.dao.second.Hello2Dao helloDao2;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("/emailTest")
    public ResponseEntity<?> helloRedis(@RequestParam String to,
                                        @RequestParam String subject,
                                        @RequestParam String text) {
//        var key = "helloRedis";
//        ResponseEntity<?> ret = (ResponseEntity<?>) redisTemplate.opsForValue().get(key);
//        if (ret == null) {
//            var str = helloDao.getHello();
//            ret = new ResponseEntity(str);
//            redisTemplate.opsForValue().set(key, ret, 10, TimeUnit.DAYS);
//        }
//        return ret;
        SendMailSSL.send(to, subject, text);
        return new ResponseEntity();
    }

}

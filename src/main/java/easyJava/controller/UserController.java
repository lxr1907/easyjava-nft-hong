package easyJava.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import easyJava.utils.GenerateUtils;
import easyJava.utils.KryoRedisSerializer;
import easyJava.utils.SendMailSSL;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.utils.TokenProccessor;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    public static final String USER_TABLE = "user";
    public static final String CODE_PRE = "email_code_";

    /**
     * 登录
     */
    @RequestMapping("/user/register")
    public ResponseEntity register(@RequestParam Map<String, Object> map) {
        if (map.get("account") == null || map.get("account").toString().length() == 0) {
            return new ResponseEntity(400, "账号不能为空！");
        }
        if (map.get("password") == null || map.get("password").toString().length() == 0) {
            return new ResponseEntity(400, "密码不能为空！");
        }
        if (map.get("code") == null || map.get("code").toString().length() == 0) {
            return new ResponseEntity(400, "验证码不能为空！");
        }
        if (checkEmailCode(map) == 0) {
            return new ResponseEntity(400, "验证码错误！");
        }
        map.remove("code");
        map.put("tableName", USER_TABLE);
        map.put("password", DigestUtils.md5Hex(map.get("password").toString()));
        map.put("my_invite_code", GenerateUtils.getRandomNickname(8));
        try {
            baseDao.insertBase(map);
        } catch (Exception e) {
            logger.error("注册失败", e);
            return new ResponseEntity(400, "邮箱已注册！");
        }

        String token = TokenProccessor.makeToken();
        map.remove("password");
        map.put("token", token);
        // token有效期1小时，存入redis
        redisTemplate.opsForValue().set(token, map, 365, TimeUnit.DAYS);
        return new ResponseEntity(map);
    }

    /**
     * 登录
     */
    @RequestMapping("/user/login")
    public ResponseEntity login(@RequestParam Map<String, Object> map) {
        if (map.get("account") == null || map.get("account").toString().length() == 0) {
            return new ResponseEntity(400, "账号不能为空！");
        }
        if (map.get("password") == null || map.get("password").toString().length() == 0) {
            return new ResponseEntity(400, "密码不能为空！");
        }
        if (map.get("code") == null || map.get("code").toString().length() == 0) {
            return new ResponseEntity(400, "验证码不能为空！");
        }
        if (checkEmailCode(map) == 0) {
            return new ResponseEntity(400, "验证码错误！");
        }
        map.remove("code");
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(1);
        baseModel.setPageNo(1);
        map.put("tableName", USER_TABLE);
        map.put("password", DigestUtils.md5Hex(map.get("password").toString()));
        List<Map> list = baseDao.selectBaseList(map, baseModel);
        if (list == null || list.size() == 0) {
            return new ResponseEntity(400, "账号或密码错误！");
        }
        String token = TokenProccessor.makeToken();
        list.get(0).remove("password");
        list.get(0).put("token", token);
        // token有效期1小时，存入redis
        redisTemplate.opsForValue().set(token, list.get(0), 365, TimeUnit.DAYS);
        return new ResponseEntity(list.get(0));
    }

    /**
     * 登录
     */
    @RequestMapping("/user/getEmailCode")
    public ResponseEntity getEmailCode(@RequestParam Map<String, Object> map) {
        if (map.get("account") == null || map.get("account").toString().length() == 0) {
            return new ResponseEntity(400, "账号不能为空！");
        }
        String code = GenerateUtils.getRandomNickname(6);
        // code存入redis
        redisTemplate.opsForValue().set(CODE_PRE + map.get("account").toString(),
                code, 10, TimeUnit.MINUTES);

        SendMailSSL.send(map.get("account").toString(), "登录注册验证码", code);
        return new ResponseEntity(1, "验证码已经发送至邮箱" + map.get("account").toString() +
                ",10分钟内有效！");
    }

    public int checkEmailCode(Map<String, Object> map) {
        if (map.get("account") == null || map.get("code") == null) {
            return 0;
        }
        // code存入redis
        Object code = redisTemplate.opsForValue().get(CODE_PRE + map.get("account").toString());
        if (code != null && code.equals(map.get("code").toString())) {
            return 1;
        }
        return 0;
    }

    public Map getUserByToken(String token) {
        return (Map) redisTemplate.opsForValue().get(token);
    }

    public boolean checkToken(String token) {
        return redisTemplate.hasKey(token);
    }
}

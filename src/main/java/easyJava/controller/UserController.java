package easyJava.controller;

import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(NFTScanController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    public static final String USER_TABLE = "user";
    public static final String USER_WALLET_TABLE = "user_wallet";
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
        if (map.get("password").toString().length() > 20) {
            return new ResponseEntity(400, "密码过长！");
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
            Map countMap = new HashMap<>();
            countMap.put("tableName", USER_TABLE);
            Integer count = baseDao.selectMaxId(countMap);
            //需求从20万开始，后面随机+1到+30
            if (count == null || count < 200000) {
                count = 200000;
            }
            var uniqueId = count + GenerateUtils.getRandomOneToMax(30);
            map.put("id", uniqueId);
            baseDao.insertBase(map);
            var walletMap = generateWallet(uniqueId);
            baseDao.insertBase(walletMap);
            map.put("wallet", walletMap);

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
     * 初次注册生成，默认status=1是主钱包
     *
     * @param uniqueId
     * @return
     */
    private Map generateWallet(int uniqueId) {
        String privateKey = KlayController.generatePrivate();
        Map map = generateWallet(uniqueId, privateKey);
        //此时生成主钱包
        map.put("status", 1);
        return map;
    }

    private Map generateWallet(int uniqueId, String privateKey) {
        String address = KlayController.getWalletAddress(privateKey);
        Map walletMap = new HashMap<>();
        walletMap.put("tableName", USER_WALLET_TABLE);
        walletMap.put("user_id", uniqueId);
        walletMap.put("address", address);
        int encrypt_key = GenerateUtils.getRandomOneToMax(40000);
        walletMap.put("encrypt_key", encrypt_key);
        walletMap.put("encrypted_private", DESUtils.encrypt(privateKey, encrypt_key));
        return walletMap;
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
        Map user = list.get(0);
        user.remove("password");
        user.put("token", token);
        // token有效期1小时，存入redis
        redisTemplate.opsForValue().set(token, user, 365, TimeUnit.DAYS);

        Map walletMap = new HashMap<>();
        walletMap.put("tableName", USER_WALLET_TABLE);
        walletMap.put("user_id", user.get("id"));
        baseModel.setPageNo(1);
        baseModel.setPageSize(10);
        List<Map> userWalletList = baseDao.selectBaseList(walletMap, baseModel);
        user.put("userWalletList", userWalletList);
        userWalletList.forEach(wallet -> {
            wallet.remove("encrypted_private");
        });
        return new ResponseEntity(user);

    }

    /**
     * 修改密码
     */
    @RequestMapping("/user/editPassword")
    public ResponseEntity editPassword(@RequestHeader("token") String token, @RequestParam Map<String, Object> map) {
        if (map.get("account") == null || map.get("account").toString().length() == 0) {
            return new ResponseEntity(400, "账号不能为空！");
        }
        if (map.get("password") == null || map.get("password").toString().length() == 0) {
            return new ResponseEntity(400, "密码不能为空！");
        }
        if (map.get("newPassword") == null || map.get("newPassword").toString().length() == 0) {
            return new ResponseEntity(400, "新密码不能为空！");
        }
        if (map.get("newPassword").toString().length() > 20) {
            return new ResponseEntity(400, "新密码过长！");
        }
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
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
            return new ResponseEntity(400, "原密码错误！");
        }

        Map updateUserMap = new HashMap<>();
        updateUserMap.put("tableName", USER_TABLE);
        updateUserMap.put("user_id", user.get("id"));
        updateUserMap.put("password", DigestUtils.md5Hex(map.get("newPassword").toString()));
        baseDao.updateBaseByPrimaryKey(updateUserMap);
        //token删除
        redisTemplate.opsForValue().getAndDelete(token);
        return new ResponseEntity(200, "密码修改成功");

    }

    /**
     * 重新获取钱包等信息
     */
    @RequestMapping("/user/getLoginInfo")
    public ResponseEntity getLoginInfo(@RequestHeader("token") String token, @RequestParam Map<String, Object> map) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }

//        if (map.get("code") == null || map.get("code").toString().length() == 0) {
//            return new ResponseEntity(400, "验证码不能为空！");
//        }
//        if (checkEmailCode(map) == 0) {
//            return new ResponseEntity(400, "验证码错误！");
//        }
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(1);
        baseModel.setPageNo(1);
        map.clear();
        map.put("tableName", USER_TABLE);
        map.put("id", user.get("id"));
        List<Map> list = baseDao.selectBaseList(map, baseModel);
        if (list == null || list.size() == 0) {
            return new ResponseEntity(400, "账号错误！");
        }
        user.remove("password");
        Map walletMap = new HashMap<>();
        walletMap.put("tableName", USER_WALLET_TABLE);
        walletMap.put("user_id", user.get("id"));
        baseModel.setPageNo(1);
        baseModel.setPageSize(10);
        List<Map> userWalletList = baseDao.selectBaseList(walletMap, baseModel);
        user.put("userWalletList", userWalletList);
        userWalletList.forEach(wallet -> {
            wallet.remove("encrypted_private");
        });
        return new ResponseEntity(user);

    }

    /**
     * 登录
     */
    @RequestMapping("/user/getEmailCode")
    public ResponseEntity getEmailCode(@RequestParam Map<String, Object> map) {
        if (map.get("account") == null || map.get("account").toString().length() == 0) {
            return new ResponseEntity(400, "账号不能为空！");
        }
        String code = "";
        var hasCode = redisTemplate.opsForValue().get(CODE_PRE + map.get("account").toString());
        if (hasCode == null || hasCode.toString().length() == 0) {
            code = GenerateUtils.getRandomNickname(6);
        } else {
            code = hasCode.toString();
        }
        logger.info("mail 邮箱验证code：" + code);
        // code存入redis
        redisTemplate.opsForValue().set(CODE_PRE + map.get("account").toString(),
                code, 10, TimeUnit.MINUTES);
        if (map.get("qq") != null) {
            //使用qq邮箱
            SendMailSSL.send(map.get("account").toString(), "登录注册验证码", code);
        } else {
            if (map.get("ssl") == null) {
                map.put("ssl", "ssl");
            }
            SendMailTLS.gmailSender(map.get("account").toString(), "登录注册验证码", code, map.get("ssl").toString());
        }
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

    @RequestMapping("/user/getPrivate")
    public ResponseEntity getPrivate(@RequestHeader("token") String token, @RequestParam Map<String, Object> map) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("code") == null || map.get("code").toString().length() == 0) {
            return new ResponseEntity(400, "验证码不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        map.put("account", user.get("account"));
        if (checkEmailCode(map) == 0) {
            return new ResponseEntity(400, "验证码错误！");
        }
        Map walletMap = new HashMap<>();
        walletMap.put("tableName", USER_WALLET_TABLE);
        walletMap.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        baseModel.setPageNo(1);
        baseModel.setPageSize(10);
        List<Map> userWalletList = baseDao.selectBaseList(walletMap, baseModel);

        return new ResponseEntity(userWalletList);
    }

    @RequestMapping("/user/importPrivate")
    public ResponseEntity importPrivate(@RequestHeader("token") String token, @RequestBody String privateKey) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);
        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        Map walletMapQuery = new HashMap<>();
        walletMapQuery.put("tableName", USER_WALLET_TABLE);
        walletMapQuery.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        baseModel.setPageNo(1);
        baseModel.setPageSize(10);
        int count = baseDao.selectBaseCount(walletMapQuery);
        if (count >= 10) {
            return new ResponseEntity(400, "最多只能创建或导入10个钱包");
        }
        var walletMap = generateWallet(Integer.parseInt(user.get("id").toString()), privateKey);
        baseDao.insertBase(walletMap);
        return new ResponseEntity(walletMap.get("address"));
    }

    @RequestMapping("/user/generatePrivate")
    public ResponseEntity generatePrivate() {
        Map wallet = new HashMap();
        String privateKey = KlayController.generatePrivate();
        String address = KlayController.getWalletAddress(privateKey);
        wallet.put("private", privateKey);
        wallet.put("address", address);
        return new ResponseEntity(wallet);
    }
}

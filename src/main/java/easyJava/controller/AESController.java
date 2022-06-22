package easyJava.controller;

import easyJava.utils.AESUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AESController {
    private static final Logger logger = LoggerFactory.getLogger(AESController.class);

    @RequestMapping("/aes/decode")
    public String decode(@RequestParam Map<String, Object> map) {
        if (map.get("key") == null || map.get("key").toString().length() == 0) {
            return "key不能为空！";
        }
        if (map.get("str") == null || map.get("str").toString().length() == 0) {
            return "str不能为空！";
        }
        String decodedStr = AESUtils.decryptByHexString(map.get("str").toString(), map.get("key").toString());
        logger.info(map.get("key").toString() + "," +
                map.get("str").toString() + "," + decodedStr);
        return decodedStr;
    }
}

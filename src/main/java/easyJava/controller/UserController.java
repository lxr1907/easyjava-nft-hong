package easyJava.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
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
	@Autowired
	BaseDao baseDao;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 登录
	 */
	@RequestMapping("/login")
	public ResponseEntity login(@RequestParam Map<String, Object> map) {
		if (map.get("account") == null || map.get("account").toString().length() == 0) {
			return new ResponseEntity(400, "账号不能为空！");
		}
		if (map.get("password") == null || map.get("password").toString().length() == 0) {
			return new ResponseEntity(400, "密码不能为空！");
		}
		BaseModel baseModel = new BaseModel();
		baseModel.setPageSize(1);
		baseModel.setPageNo(1);
		map.put("tableName", "user");
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

	public Map getUserByToken(String token) {
		return (Map) redisTemplate.opsForValue().get(token);
	}

	public boolean checkToken(String token) {
		return redisTemplate.hasKey(token);
	}
}

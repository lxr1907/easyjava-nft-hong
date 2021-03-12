package easyJava.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.utils.GenerateUtils;

@RestController
public class UserCoinController {
	@Autowired
	BaseDao baseDao;
	@Autowired
	UserController userController;
	@Autowired
	GenerateUtils generateUtils;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	public static final String USER_COIN = "accounts_coin_balance";

	@RequestMapping("/getUserCoin")
	public ResponseEntity login(@RequestParam Map<String, Object> map) {
		if (map.get("uuid") == null || map.get("uuid").toString().length() == 0) {
			return new ResponseEntity(400, "uuid不能为空！");
		}
		map.put("tableName", USER_COIN);
		BaseModel baseModel = new BaseModel();
		baseModel.setPageSize(1);
		baseModel.setPageNo(1);
		HashMap retmap = new HashMap();
		List list = baseDao.selectBaseList(map, baseModel);
		int count = baseDao.selectBaseCount(map);
		retmap.put("list", list);
		return new ResponseEntity(retmap, count, baseModel);
	}


	public Map getUserByToken(String token) {
		return (Map) redisTemplate.opsForValue().get(token);
	}

	public boolean checkToken(String token) {
		return redisTemplate.hasKey(token);
	}
}

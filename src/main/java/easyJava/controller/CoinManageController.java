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
public class CoinManageController {
	@Autowired
	BaseDao baseDao;
	@Autowired
	UserController userController;
	@Autowired
	GenerateUtils generateUtils;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	public static final String COIN_MANAGE = "coin_manage";

	@RequestMapping("/getCoinList")
	public ResponseEntity login(@RequestParam Map<String, Object> map) {
		if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
			return new ResponseEntity(400, "pageSize不能为空！");
		}
		if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
			return new ResponseEntity(400, "pageNo不能为空！");
		}
		map.put("tableName", COIN_MANAGE);
		BaseModel baseModel = new BaseModel();
		baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
		baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
		HashMap retmap = new HashMap();
		List list = baseDao.selectBaseList(map, baseModel);
		int count = baseDao.selectBaseCount(map);
		retmap.put("list", list);
		return new ResponseEntity(retmap, count, baseModel);
	}

	@RequestMapping("/insertCoin")
	public ResponseEntity insertCoin(@RequestParam Map<String, Object> map) {
		if (map.get("token") == null || map.get("token").toString().length() == 0) {
			return new ResponseEntity(400, "token不能为空！");
		}
		if (!userController.checkToken(map.get("token").toString())) {
			return new ResponseEntity(400, "token已经失效！");
		}
		map.remove("token");
		map.put("tableName", COIN_MANAGE);
		map.put("id", generateUtils.getUniqueId(COIN_MANAGE));
		int count = baseDao.insertBase(map);
		return new ResponseEntity(count);
	}

	@RequestMapping("/updateCoinByPrimaryKey")
	public ResponseEntity updateBaseByPrimaryKey(@RequestParam Map<String, Object> map) {
		if (map.get("id") == null || map.get("id").toString().trim().length() == 0) {
			return new ResponseEntity(400, "id不能为空！");
		}
		if (map.get("token") == null || map.get("token").toString().length() == 0) {
			return new ResponseEntity(400, "token不能为空！");
		}
		if (!userController.checkToken(map.get("token").toString())) {
			return new ResponseEntity(400, "token已经失效！");
		}
		map.remove("token");
		map.put("tableName", COIN_MANAGE);
		int count = baseDao.updateBaseByPrimaryKey(map);
		return new ResponseEntity(count);
	}

	public Map getUserByToken(String token) {
		return (Map) redisTemplate.opsForValue().get(token);
	}

	public boolean checkToken(String token) {
		return redisTemplate.hasKey(token);
	}
}

package easyJava.controller;

import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.utils.GenerateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NFTController {
	@Autowired
	BaseDao baseDao;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public static final String AddressInvite_MANAGE = "address_invite";

	@RequestMapping("/getAddressInviteList")
	public ResponseEntity<?> login(@RequestParam Map<String, Object> map) {
		if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
			return new ResponseEntity(400, "pageSize不能为空！");
		}
		if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
			return new ResponseEntity(400, "pageNo不能为空！");
		}
		map.put("tableName", AddressInvite_MANAGE);
		BaseModel baseModel = new BaseModel();
		baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
		baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
		var retmap = new HashMap();
		var list = baseDao.selectBaseList(map, baseModel);
		int count = baseDao.selectBaseCount(map);
		retmap.put("list", list);
		return new ResponseEntity(retmap, count, baseModel);
	}

	@RequestMapping("/insertAddressInvite")
	public ResponseEntity insertAddressInvite(@RequestParam Map<String, Object> map) {
		if (map.get("address") == null || map.get("address").toString().length() == 0) {
			return new ResponseEntity(400, "address不能为空！");
		}
		map.put("tableName", AddressInvite_MANAGE);
		int count = baseDao.insertIgnoreBase(map);
		return new ResponseEntity(count);
	}

	@RequestMapping("/updateAddressInvite")
	public ResponseEntity updateBaseByPrimaryKey(@RequestParam Map<String, Object> map) {
		if (map.get("id") == null || map.get("id").toString().trim().length() == 0) {
			return new ResponseEntity(400, "id不能为空！");
		}
		map.put("tableName", AddressInvite_MANAGE);
		int count = baseDao.updateBaseByPrimaryKey(map);
		return new ResponseEntity(count);
	}

	@RequestMapping("/getAddressInvite")
	public ResponseEntity getAddressInvite(@RequestParam Map<String, Object> map) {
		if (map.get("address") == null || map.get("address").toString().length() == 0) {
			return new ResponseEntity(400, "address不能为空！");
		}
		map.put("tableName", AddressInvite_MANAGE);
		BaseModel baseModel = new BaseModel();
		baseModel.setPageSize(1);
		baseModel.setPageNo(1);
		HashMap retmap = new HashMap();
		List list = baseDao.selectBaseList(map, baseModel);
		retmap.put("list", list);
		return new ResponseEntity(retmap, 1, baseModel);
	}
}
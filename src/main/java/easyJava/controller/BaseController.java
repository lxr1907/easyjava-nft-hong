package easyJava.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;

@RestController
public class BaseController {

	@Autowired
	BaseDao baseDao;
	@Autowired
	UserController userController;

	/**
	 * 针对任意表进行分页查询
	 * 
	 * @param map
	 * @return
	 */
	@GetMapping("/getBaseList")
	public ResponseEntity getBaseList(@RequestParam Map<String, Object> map) {
		if (map.get("tableName") == null || map.get("tableName").toString().length() == 0) {
			return new ResponseEntity(400, "tableName不能为空！");
		}
		if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
			return new ResponseEntity(400, "pageSize不能为空！");
		}
		if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
			return new ResponseEntity(400, "pageNo不能为空！");
		}
		BaseModel baseModel = new BaseModel();
		baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
		baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
		HashMap retmap = new HashMap();
		List list = baseDao.selectBaseList(map, baseModel);
		int count = baseDao.selectBaseCount(map);
		retmap.put("list", list);
		return new ResponseEntity(retmap, count, baseModel);
	}

	/**
	 * 针对任意表查询列信息
	 * 
	 * @param map
	 * @return
	 */
	@GetMapping("/getBaseColumns")
	public ResponseEntity getBaseColumns(@RequestParam Map<String, Object> map) {
		if (map.get("tableName") == null || map.get("tableName").toString().length() == 0) {
			return new ResponseEntity(400, "tableName不能为空！");
		}
		HashMap retmap = new HashMap();
		List list = baseDao.selectBaseShowTableColumns(map);
		int count = list.size();
		retmap.put("list", list);
		BaseModel baseModel = new BaseModel();
		return new ResponseEntity(retmap, count, baseModel);
	}

	/**
	 * 查询所有表名
	 * 
	 * @param map
	 * @return
	 */
	@GetMapping("/getBaseTableName")
	public ResponseEntity selectBaseTableName() {
		HashMap retmap = new HashMap();
		List list = baseDao.selectBaseTableName();
		int count = list.size();
		retmap.put("list", list);
		BaseModel baseModel = new BaseModel();
		return new ResponseEntity(retmap, count, baseModel);
	}

	@RequestMapping("/insertBase")
	public ResponseEntity insertBase(@RequestParam Map<String, Object> map) {
		if (map.get("tableName") == null || map.get("tableName").toString().length() == 0) {
			return new ResponseEntity(400, "tableName不能为空！");
		}
		if (map.get("token") == null || map.get("token").toString().length() == 0) {
			return new ResponseEntity(400, "token不能为空！");
		}
		if (!userController.checkToken(map.get("token").toString())) {
			return new ResponseEntity(400, "token已经失效！");
		}
		map.remove("token");
		int count = baseDao.insertBase(map);
		return new ResponseEntity(count);
	}

	@RequestMapping("/updateBaseByPrimaryKey")
	public ResponseEntity updateBaseByPrimaryKey(@RequestParam Map<String, Object> map) {
		if (map.get("tableName") == null || map.get("tableName").toString().length() == 0) {
			return new ResponseEntity(400, "tableName不能为空！");
		}
		if (map.get("id") == null || map.get("id").toString().length() == 0) {
			return new ResponseEntity(400, "id不能为空！");
		}
		if (map.get("token") == null || map.get("token").toString().length() == 0) {
			return new ResponseEntity(400, "token不能为空！");
		}
		if (!userController.checkToken(map.get("token").toString())) {
			return new ResponseEntity(400, "token已经失效！");
		}
		map.remove("token");
		int count = baseDao.updateBaseByPrimaryKey(map);
		return new ResponseEntity(count);
	}
}

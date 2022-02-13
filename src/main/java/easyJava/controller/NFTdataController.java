package easyJava.controller;

import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NFTdataController {
    @Autowired
    BaseDao baseDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String NFTdata_MANAGE = "nft_data";
    //public static final String IMAGE_BASE_URL = "https://nftrobbi.oss-us-west-1.aliyuncs.com/";
    public static final String IMAGE_BASE_URL = "https://robbbbbi.oss-us-west-1.aliyuncs.com/Final_full/";
    //https://robbbbbi.oss-us-west-1.aliyuncs.com/Final_full/
    @RequestMapping("/getNFTdataList")
    public ResponseEntity<?> login(@RequestParam Map<String, Object> map) {
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize不能为空！");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo不能为空！");
        }
        map.put("tableName", NFTdata_MANAGE);
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
        baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
        var retmap = new HashMap();
        var list = baseDao.selectBaseList(map, baseModel);
        int count = baseDao.selectBaseCount(map);
        retmap.put("list", list);
        return new ResponseEntity(retmap, count, baseModel);
    }

    @RequestMapping("/insertNFTdata")
    public ResponseEntity insertNFTdata(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        map.put("tableName", NFTdata_MANAGE);
        int count = baseDao.insertIgnoreBase(map);
        return new ResponseEntity(count);
    }

    @RequestMapping("/updateNFTdata")
    public ResponseEntity updateBaseByPrimaryKey(@RequestParam Map<String, Object> map) {
        if (map.get("id") == null || map.get("id").toString().trim().length() == 0) {
            return new ResponseEntity(400, "id不能为空！");
        }
        map.put("tableName", NFTdata_MANAGE);
        int count = baseDao.updateBaseByPrimaryKey(map);
        return new ResponseEntity(count);
    }

    @RequestMapping("/getNFTdata")
    public Map getNFTdata(@RequestParam Map<String, Object> map) {
        Map mapRet = new HashMap();
        if (map.get("id") == null || map.get("id").toString().length() == 0) {
            mapRet.put("error", "id不能为空！");
            return mapRet;
        }
        map.put("tableName", NFTdata_MANAGE);
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(1);
        baseModel.setPageNo(1);
        HashMap retmap = new HashMap();
        List<Map> list = baseDao.selectBaseList(map, baseModel);
        list.forEach(retMap -> {
            String name = retMap.get("name").toString();
            retMap.put("image", IMAGE_BASE_URL + name+".png");
            //https://nftrobbi.oss-us-west-1.aliyuncs.com/SSME01_0001.png
        });
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            mapRet.put("error", "未查询到！");
            return mapRet;
        }
    }
}

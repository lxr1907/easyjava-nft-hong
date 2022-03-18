package easyJava.controller;

import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NFTdataController {
    @Autowired
    BaseDao baseDao;
    boolean lootBox = true;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String NFTdata_MANAGE = "nft_data";
    //public static final String IMAGE_BASE_URL = "https://nftrobbi.oss-us-west-1.aliyuncs.com/";
    //    public static final String IMAGE_BASE_URL = "https://robbbbbi.oss-us-west-1.aliyuncs.com/Final_full/";
    public static final String IMAGE_INIT_URL = "https://robbbbbi.oss-us-west-1.aliyuncs.com/Final_full/";

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

    @RequestMapping("/{id}")
    public Map getNFTdataTest(@PathVariable String id) {
        Map mapRet = new HashMap();
        if (id == null || id.length() == 0) {
            mapRet.put("error", "id不能为空！");
            return mapRet;
        }
        var map = new HashMap<>();
        Integer idInt = Integer.parseInt(id) + 1;
        map.put("id", idInt);
        map.put("tableName", NFTdata_MANAGE);
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(1);
        baseModel.setPageNo(1);
        List<Map> list = baseDao.selectBaseList(map, baseModel);
        list.forEach(retMap -> {
            String name = retMap.get("name").toString();
            var attrList = new ArrayList<Map>();

            String namePre = "ROBBi ";
            for (var attr : retMap.entrySet()) {
                String key = ((Map.Entry) attr).getKey().toString();
                if (key.equals("id")
                        || key.equals("name") || key.equals("main_material")) {
                    continue;
                }
                Map attrMap = new HashMap<>();
                attrMap.put("trait_type", ((Map.Entry) attr).getKey());
                attrMap.put("value", ((Map.Entry) attr).getValue());
                attrList.add(attrMap);
                if (((Map.Entry) attr).getKey().equals("Planet")) {
                    namePre = namePre + ((Map.Entry) attr).getValue() + " # " + id;
                }
            }

            retMap.put("name", namePre);
            retMap.put("attributes", attrList);
            retMap.put("image", IMAGE_INIT_URL + name + ".png");
            //https://nftrobbi.oss-us-west-1.aliyuncs.com/SSME01_0001.png
        });
        if (list != null && list.size() > 0) {
            Map retPre = list.get(0);
            Map retMap = new HashMap();

            retMap.put("name", retPre.get("name"));
            retMap.put("image", retPre.get("image"));

            retMap.put("attributes", retPre.get("attributes"));
            retMap.put("description", "ROBBi Hero, Solar System Series by ROBBi X MCG");

            return retMap;
        } else {
            mapRet.put("error", "未查询到！");
            return mapRet;
        }
    }
}
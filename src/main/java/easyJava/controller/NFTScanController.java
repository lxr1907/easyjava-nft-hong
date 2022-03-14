package easyJava.controller;

import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.etherScan.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableScheduling
public class NFTScanController {
    @Autowired
    BaseDao baseDao;
    @Autowired
    ScanService scanService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String NFT_OWNER = "nft_owner";

    @Scheduled(cron = "*/15 * * * * ?")
    @RequestMapping("/scanNftTransfer")
    public ResponseEntity<?> scanNftTransfer() {
        //这个方法要在代码里写个定时器， 每隔 5或10秒 扫一次
        List<Map> retList = scanService.doScan();
        retList.forEach(map -> {
            map.put("tableName", NFT_OWNER);
            baseDao.insertUpdateBase(map);
        });
        return new ResponseEntity();
    }

    @RequestMapping("/getOwnerNFTList")
    public ResponseEntity<?> getOwnerNFTList(@RequestParam Map<String, Object> map) {
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize不能为空！");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo不能为空！");
        }
        map.put("tableName", NFT_OWNER);
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
        baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
        var retmap = new HashMap();
        var list = baseDao.selectBaseList(map, baseModel);
        int count = baseDao.selectBaseCount(map);
        retmap.put("list", list);
        return new ResponseEntity(retmap, count, baseModel);
    }
}

package easyJava.controller;

import com.alibaba.fastjson.JSON;
import easyJava.dao.master.BaseDao;
import easyJava.dao.master.EthScanDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.etherScan.ScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NFTScanController {
    public static final Logger logger = LoggerFactory.getLogger(NFTScanController.class);

    @Autowired
    BaseDao baseDao;
    @Autowired
    EthScanDao ethScanDao;

    @Autowired
    ScanService scanService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String ETH_LOG_TABLE = "eth_log";
    public static final String ETH_USDT_CONTRACT_ADDRESS = "0xebe3a081bb66cc23fcc014ef856c512966bf6708";
    public static final BigInteger decimals18 = BigInteger.valueOf(Double.valueOf(Math.pow(10, 18)).longValue());

    //    @Scheduled(cron = "*/30 * * * * ?")
    public ResponseEntity<?> scanETHLogJob() {
        //这个方法要在代码里写个定时器， 每隔 5或10秒 扫一次

        List<Map> retList = scanService.doScanETH();
        retList.forEach(map -> {
            map.put("tableName", ETH_LOG_TABLE);
            baseDao.insertIgnoreBase(map);
        });
        return new ResponseEntity();
    }

    @Scheduled(cron = "*/30 * * * * ?")
    @Transactional
    public void scanUSDTLogJob() {
        //这个方法要在代码里写个定时器， 每隔 5或10秒 扫一次
        List<Map> retList = scanService.doScanToken();
        logger.info("-----------scanUSDTLogJob retList------ size:" + retList.size());
        retList.forEach(map -> {
            map.put("tableName", ETH_LOG_TABLE);
            logger.info("------------map:" + JSON.toJSONString(map));
            if (map.get("to").toString().equalsIgnoreCase(KlayController.USDT_ADDRESS_ERC20_ROPSTEN)) {
                String amountStr = getDecimal18(map.get("value").toString());
                logger.info("--------scanUSDTLogJob amountStr:" + amountStr
                        + ",value" + map.get("value").toString() + "-------");
                Map queryOrderMap = new HashMap();
                queryOrderMap.put("tableName", KlayController.ORDER_TABLE);
                queryOrderMap.put("send_value", amountStr);
                queryOrderMap.put("status", 1);
                BaseModel baseModel = new BaseModel();
                baseModel.setPageSize(1);
                baseModel.setPageNo(1);
                List<Map> list = baseDao.selectBaseList(queryOrderMap, baseModel);
                if (list.size() != 0) {
                    //匹配到了订单金额完全相符的，认为是该用户的订单成功支付
                    Map matchOrder = list.get(0);
                    logger.info("-----------匹配到订单：" + JSON.toJSONString(matchOrder) + "----------");
                    String user_id = matchOrder.get("user_id").toString();
                    Map userQuery = new HashMap();
                    userQuery.put("tableName", UserController.USER_TABLE);
                    userQuery.put("id", Long.parseLong(user_id));
                    Map user = baseDao.selectBaseByPrimaryKey(userQuery);
                    String chr_address = user.get("chr_address").toString();
                    logger.info("-----------匹配到订单user:" + JSON.toJSONString(user));
                    long buy_amount = Long.parseLong(matchOrder.get("buy_amount").toString());
                    long price = Long.parseLong(matchOrder.get("price").toString());
                    BigInteger chrVal = BigInteger.valueOf(buy_amount * price);//.multiply(decimals18);
                    matchOrder.put("status", 2);

                    matchOrder.put("tableName", KlayController.ORDER_TABLE);
                    baseDao.updateBaseByPrimaryKey(matchOrder);
                    try {
                        logger.info("-----------sendingKLAY to user chr_address:------"
                                + chr_address + ",val:" + chrVal);
                        KlayController.sendingCHR(KlayController.SYSTEM_PRIVATE, chr_address, chrVal);
                        matchOrder.put("status", 3);
                        baseDao.updateBaseByPrimaryKey(matchOrder);
                        logger.info("-----------sendingKLAY to user chr_address:"
                                + chr_address + ",val:" + chrVal + ",success-----");
                    } catch (Exception e) {
                        logger.error("sendingKLAY error", e);
                    }
                }
            }
            baseDao.insertIgnoreBase(map);
        });
        logger.info("----end scanUSDTLogJob----------------------------");
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("scanUSDTLogJob:");
        logger.info(getDecimal18("1003000000000000000"));
    }

    private static String getDecimal18(String amountStr) {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr)).divide(BigDecimal.valueOf(Math.pow(10, 18)));
        return amount.toPlainString().replaceAll("(0)+$", "");
    }

    @RequestMapping("/scanETH")
    public ResponseEntity<?> scanETH() {
        List<Map> retList = scanService.doScanETH();
        return new ResponseEntity(retList);
    }

    @RequestMapping("/scanUSDT")
    public ResponseEntity<?> scanUSDT() {
        List<Map> retList = scanService.doScanToken();
        return new ResponseEntity(retList);
    }

    @RequestMapping("/ethScan/getAddressTx")
    public ResponseEntity<?> getAddressTx(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize不能为空！");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo不能为空！");
        }
        map.put("tableName", ETH_LOG_TABLE);
        String address = map.get("address").toString();
        map.put("from", address);
        map.put("to", address);
        map.remove("address");
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
        baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
        var list = ethScanDao.selectBaseList(map, baseModel);
        return new ResponseEntity(list);
    }
}

package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.datatypes.StaticStruct;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.ContractMethod;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.wallet.keyring.KeyStore;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import easyJava.controller.websocket.TexasWS;
import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseEntity;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.utils.DESUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * chrToken和gamecoin挂单交易相关
 */
@RestController
public class SCNGameCoinController {
    private static final Logger logger = LoggerFactory.getLogger(SCNGameCoinController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    UserController userController;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String MY_SCN_HOST = "http://13.213.135.231:7551";
    public static volatile BigInteger gas = BigInteger.valueOf(8000000);
    public static final String SCN_CHILD_OPERATOR = "{\"address\":\"56c8cb5daf329fc8613112b51e359b2dbae4fd97\",\"keyring\":[[{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"1a6d4aac70114be5b4eb54bf8cc11c58f23c4e8e97b2235cf6a9d0bfcc478a55\",\"cipherparams\":{\"iv\":\"24a74d100afae38093f7a5267ee17626\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"17ff967beb4c3e98c4d63c3b78c9a721a2fc5906c5d3ab43f81ec0a305c7e4c6\"},\"mac\":\"000d9abe5cd71085e4789abd1a604d77cdc31aef05eae5b1bcfbed364a94fbfb\"}]],\"id\":\"7de1963a-e59d-496b-bf34-029d50b76ab3\",\"version\":4}";
    public static final String SCN_CHILD_OPERATOR_PASSWORD = "cbor{@b9b1__#+#}";
    public static final String SCN_CHILD_OPERATOR_ADDRESS = "0x56c8cb5daf329fc8613112b51e359b2dbae4fd97";
    public static final int priceScale = 4;

    public static ObjectMapper mapper = new ObjectMapper();
    public static ConcurrentHashMap<String, String> privateKeyFromJsonMap = new ConcurrentHashMap<>();

    public static String getPrivateKeyFromJson(String keyStoreJSON, String pwd) {
        String fromPrivateKey = "";
        String key = keyStoreJSON + "_" + pwd;
        try {
            if (privateKeyFromJsonMap.containsKey(key)) {
                return privateKeyFromJsonMap.get(key);
            }
//            logger.info(keyStoreJSON);
            KeyStore keyStore = JSON.parseObject(keyStoreJSON, KeyStore.class);
//            logger.info(keyStore.getKeyring().get(0).toString());
            List<KeyStore.Crypto> crypto = mapper.readValue(keyStore.getKeyring().get(0).toString(), new TypeReference<List<KeyStore.Crypto>>() {
            });
            fromPrivateKey = KeyStore.Crypto.decryptCrypto(crypto.get(0), pwd);
//            logger.info(fromPrivateKey);
        } catch (Exception e) {
            logger.error("sendingSCN 失败 KeyStore.Crypto.decryptCrypto:" + e.getMessage() + ",p:" + fromPrivateKey);
            e.printStackTrace();
            return null;
        }
        privateKeyFromJsonMap.put(key, fromPrivateKey);
        return fromPrivateKey;
    }

    /**
     * 查询余额
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/gameCoin/balanceOf")
    public ResponseEntity<?> balanceOf(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        return new ResponseEntity(balanceOf(map.get("address").toString()));
    }

    /**
     * 出售gameCoin
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/gameCoin/addSaleOrder")
    public ResponseEntity<?> addSaleOrder(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("amount") == null || map.get("amount").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("price") == null || map.get("price").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);
        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        Map useWallet = getUserWallet(user, map.get("address").toString());

        if (useWallet == null) {
            return new ResponseEntity(400, "address不属于自己！");
        }
        try {
            var result = addSaleOrder(getSingleKeyring(useWallet),
                    //数量也保留4位小数
                    getPriceScale(map.get("amount").toString()),
                    getPriceScale(map.get("price").toString()));
            new ClearOrdersRedisThread(0).start();
            return new ResponseEntity(result);
        } catch (Exception e) {
            logger.error("addSaleOrder error!", e);
            return new ResponseEntity(400, "addSaleOrder失败:" + e.getMessage());
        }
    }


    /**
     * 购买gameCoin挂单
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/gameCoin/addBuyOrder")
    public ResponseEntity<?> addBuyOrder(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("amount") == null || map.get("amount").toString().length() == 0) {
            return new ResponseEntity(400, "amount不能为空！");
        }
        if (map.get("price") == null || map.get("price").toString().length() == 0) {
            return new ResponseEntity(400, "price不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        Map useWallet = getUserWallet(user, map.get("address").toString());

        if (useWallet == null) {
            return new ResponseEntity(400, "address不属于自己！");
        }
        try {
            var result = addBuyOrder(getSingleKeyring(useWallet),
                    //数量也保留4位小数
                    getPriceScale(map.get("amount").toString()),
                    getPriceScale(map.get("price").toString()));
            new ClearOrdersRedisThread(0).start();
            return new ResponseEntity(result);
        } catch (Exception e) {
            logger.error("addSaleOrder error!", e);
            return new ResponseEntity(400, "addSaleOrder失败:" + e.getMessage());
        }
    }

    private static BigInteger getPriceScale(String priceStr) {
        BigInteger price = new BigDecimal(priceStr).setScale(4, RoundingMode.DOWN).multiply(new BigDecimal(10000)).toBigInteger();
        return price;
    }

    private static String getPriceScaleDecimal(String priceStr) {
        if (priceStr.equals("0")) {
            return "0";
        }
        BigDecimal price = new BigDecimal(priceStr).setScale(4, RoundingMode.DOWN).divide(new BigDecimal(10000));
        return price.toPlainString();
    }

    @RequestMapping("/gameCoin/cancelBuyOrder")
    public ResponseEntity<?> cancelBuyOrder(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("time") == null || map.get("time").toString().length() == 0) {
            return new ResponseEntity(400, "time不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        Map useWallet = getUserWallet(user, map.get("address").toString());

        if (useWallet == null) {
            return new ResponseEntity(400, "address不属于自己！");
        }
        try {
            var result = cancelBuyOrder(getSingleKeyring(useWallet), new BigInteger(map.get("time").toString()));
            new ClearOrdersRedisThread(2).start();
            return new ResponseEntity(result);
        } catch (Exception e) {
            logger.error("cancelBuyOrder error!", e);
            return new ResponseEntity(400, "cancelBuyOrder失败:" + e.getMessage());
        }
    }

    @RequestMapping("/gameCoin/cancelSaleOrder")
    public ResponseEntity<?> cancelSaleOrder(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("time") == null || map.get("time").toString().length() == 0) {
            return new ResponseEntity(400, "time不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        Map useWallet = getUserWallet(user, map.get("address").toString());

        if (useWallet == null) {
            return new ResponseEntity(400, "address不属于自己！");
        }
        try {
            var result = cancelSaleOrder(getSingleKeyring(useWallet), new BigInteger(map.get("time").toString()));
            new ClearOrdersRedisThread(1).start();
            return new ResponseEntity(result);
        } catch (Exception e) {
            logger.error("cancelSaleOrder error!", e);
            return new ResponseEntity(400, "cancelSaleOrder失败:" + e.getMessage());
        }
    }


    class ClearOrdersRedisThread extends Thread {
        int type;

        public ClearOrdersRedisThread(int type) {
            this.type = type;
        }

        /**
         * 类型0全部，1sale，2buy，3history
         *
         * @param type
         */
        public void clearOrdersRedis(int type) {
            String key = "getOrders:getSaleOrders";
            if (type == 0 || type == 1) {
                redisTemplate.opsForValue().set(key, new ArrayList<>());
            }
            if (type == 0 || type == 2) {
                key = "getOrders:getBuyOrders";
                redisTemplate.opsForValue().set(key, new ArrayList<>());
            }
            if (type == 0 || type == 3) {
                key = "getOrders:getHistoryOrders";
                redisTemplate.opsForValue().set(key, new ArrayList<>());
            }
        }

        public void sendNotification(String methodName, String secondIntervalStr, int pageSize, int order) {
            var ordersRedis = getOrdersList(methodName, null, secondIntervalStr, pageSize, order);
            BaseEntity entity = new BaseEntity();
            entity.setType(methodName);
            entity.setList(ordersRedis);
            TexasWS.sendToAllText(JSON.toJSONString(entity));
        }

        @Override
        public void run() {
            clearOrdersRedis(type);
            if (type == 0) {
                sendNotification("getHistoryOrders", "1", 15, 2);
                sendNotification("getKline", "21600", 100, 1);
            }
            if (type == 0 || type == 2) {
                sendNotification("getBuyOrders", "1", 5, 1);
            }
            if (type == 0 || type == 1) {
                sendNotification("getSaleOrders", "1", 5, 1);
            }
        }
    }

    @RequestMapping("/gameCoin/test/addGameCoin")
    public ResponseEntity<?> addGameCoin(@RequestParam Map<String, Object> map) throws Exception {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("amount") == null || map.get("amount").toString().length() == 0) {
            return new ResponseEntity(400, "amount不能为空！");
        }
        testTransfer(map.get("address").toString(), getPriceScale(map.get("amount").toString()).toString());
        return new ResponseEntity();
    }

    /**
     * 查询挂单列表，买单，卖单，历史，折线图采样
     *
     * @param map
     * @return
     */
    @RequestMapping("/gameCoin/getOrders/{methodName}")
    public ResponseEntity<?> getOrders(@RequestParam Map<String, Object> map, @PathVariable String methodName) {
        if (methodName == null || methodName.length() == 0) {
            return new ResponseEntity(400, "methodName不能为空,可选：getBuyOrders,getSaleOrders,getHistoryOrders，getSamplingOrders，getKline！");
        }
        //显示前5条
        int pageSize = 5;
        if (map.get("pageSize") != null && map.get("pageSize").toString().length() != 0) {
            pageSize = Integer.parseInt(map.get("pageSize").toString());
        }
        int order = 1;
        if (map.get("order") != null && map.get("order").toString().length() != 0) {
            order = Integer.parseInt(map.get("order").toString());
        }
        var ordersRedis = getOrdersList(methodName, map.get("address"), map.get("secondInterval"), pageSize, order);
        //价格翻转为chrToken计价
        priceTypeTransfer(ordersRedis);
        return new ResponseEntity(ordersRedis);
    }

    private void priceTypeTransfer(List<List> orders) {
        orders.forEach(order -> {
            var price = order.get(2);
            BigDecimal priceInt = BigDecimal.valueOf(Long.parseLong(price.toString()));
            var chrTokenPrice = new BigDecimal(10).pow(priceScale).setScale(priceScale).
                    divide(priceInt, RoundingMode.HALF_DOWN);
            chrTokenPrice.setScale(priceScale);
            String chrTokenPriceStr = chrTokenPrice.toPlainString();
            order.add(chrTokenPriceStr);


            var amount = order.get(0);
            BigDecimal amountDecimal = BigDecimal.valueOf(Long.parseLong(amount.toString()));
            BigDecimal priceDecimal = BigDecimal.valueOf(Long.parseLong(price.toString()));
            var chrTokenAmount = amountDecimal.setScale(priceScale).
                    divide(priceDecimal, RoundingMode.HALF_DOWN);
//            order.add(chrTokenAmount.toPlainString());
            order.set(0, chrTokenAmount.toPlainString());
        });
    }

    private List<List> getOrdersList(String methodName, Object address, Object secondIntervalStr, int pageSize, int order) {
        String key = "getOrders:" + methodName;
        List<List> ordersRedis = null;
        try {
            ordersRedis = (List<List>) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("error:", e);
        }
        if (ordersRedis == null || ordersRedis.size() == 0) {
            if (methodName.equalsIgnoreCase("getSamplingOrders")
                    || methodName.equalsIgnoreCase("getKline")) {
                ordersRedis = getOrders("getHistoryOrders");
            } else {
                ordersRedis = getOrders(methodName);
            }
            redisTemplate.opsForValue().set(key, ordersRedis);
        }
        if (!methodName.equals("getHistoryOrders")) {
            //合并价格相同订单
            ordersRedis = getSortedCombined(ordersRedis);
        }
        if (ordersRedis == null) {
            return new ArrayList();
        }

        //按地址过滤
        if (address != null && address.toString().length() != 0) {
            List<List> addressOrders = new ArrayList<>();
            for (int i = 0; i < ordersRedis.size(); i++) {
                if (ordersRedis.get(i).get(4).toString().equals(address) || ordersRedis.get(i).get(6).toString().equals(address)) {
                    addressOrders.add(ordersRedis.get(i));
                }
            }
            ordersRedis = addressOrders;
        }
        //按时间采样抽取
        if (methodName.equals("getSamplingOrders")) {
            //按时间抽取，6小时一个
            int secondInterval = 60 * 60 * 6;
            if (secondIntervalStr != null && secondIntervalStr.toString().length() != 0) {
                secondInterval = Integer.parseInt(secondIntervalStr.toString());
            }
            ordersRedis = getSampling(ordersRedis, secondInterval, pageSize);
        } else if (methodName.equals("getKline")) {
            //按时间抽取，6小时一个
            int secondInterval = 60 * 60 * 6;
            if (secondIntervalStr != null && secondIntervalStr.toString().length() != 0) {
                secondInterval = Integer.parseInt(secondIntervalStr.toString());
            }
            ordersRedis = getKline(ordersRedis, secondInterval, pageSize);
        } else if (methodName.equals("getBuyOrders")) {
            //将数量统一为gamecoin的数量
            for (var buyOrder : ordersRedis) {
                var gamecoinAmount = new BigInteger(buyOrder.get(1).toString())
                        .multiply(new BigInteger(buyOrder.get(2).toString())).divide(new BigInteger("10000"));
                buyOrder.set(0, gamecoinAmount);
            }
        }

        if (ordersRedis.size() < pageSize) {
            pageSize = ordersRedis.size();
        }
        //排序
        if (order == 2) {
            List<List> rankedOrders = new ArrayList<>();
            for (int i = 0; i < pageSize; i++) {
                rankedOrders.add(ordersRedis.get(ordersRedis.size() - 1 - i));
            }
            ordersRedis = rankedOrders;
        }
        //分页
        if (ordersRedis.size() > pageSize) {
            ordersRedis = ordersRedis.subList(0, pageSize);
        }
        return ordersRedis;
    }

    public static List<List> getSortedCombined(List<List> list) {
        List<List> newList = new ArrayList<>();
        Map<Object, List> map = new LinkedHashMap<>();
        for (var order : list) {
            var price = order.get(2);
            if (map.containsKey(price)) {
                Object amount = map.get(price).get(0);
                BigInteger addAmount = new BigInteger(amount.toString()).add(new BigInteger(order.get(0).toString()));
                map.get(price).set(0, addAmount);
                Object amount2 = map.get(price).get(2);
                BigInteger addAmount2 = new BigInteger(amount2.toString()).add(new BigInteger(order.get(2).toString()));
                map.get(price).set(0, addAmount2);
            } else {
                map.put(price, order);
            }
        }
        for (var entry : map.entrySet()) {
            newList.add(entry.getValue());
        }
        return newList;
    }

    //按时间采样抽取
    public static List<List> getSampling(List<List> list, int secondInterval, int pageSize) {
        List<List> newList = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return newList;
        }
        long timeNow = new Date().getTime() / 1000 / secondInterval * secondInterval;

        List lastOrder = new ArrayList(list.get(0).size());
        lastOrder.addAll(list.get(0));
        for (int i = 0; i < pageSize; i++) {
            var timeEnd = timeNow - secondInterval * (pageSize - i - 1);
            var timeBegin = timeNow - secondInterval * (pageSize - i);
            var hasOrder = false;
            for (var order : list) {
                var time = Long.parseLong(order.get(3).toString());
                if (time <= timeEnd && time > timeBegin) {
                    lastOrder = new ArrayList(order.size());
                    lastOrder.addAll(order);
                    lastOrder.set(3, timeEnd);
                    newList.add(lastOrder);
                    hasOrder = true;
                }
            }
            if (!hasOrder) {
                var lastOrderNew = new ArrayList(lastOrder.size());
                lastOrderNew.addAll(lastOrder);
                lastOrderNew.set(3, timeEnd);
                newList.add(lastOrderNew);
            }
        }

        return newList;
    }

    //按时间getKline
    public static List<List> getKline(List<List> list, int secondInterval, int pageSize) {
        List<List> newList = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return newList;
        }
        long timeNow = new Date().getTime() / 1000 / secondInterval * secondInterval;

        List lastOrder = new ArrayList();
        lastOrder.addAll(list.get(0));
        var price = lastOrder.get(2);
        lastOrder.add(price);
        lastOrder.add(price);
        lastOrder.add(price);
        lastOrder.add(price);
        for (int i = 0; i < pageSize; i++) {
            var timeEnd = timeNow - secondInterval * (pageSize - i - 1);
            var timeBegin = timeNow - secondInterval * (pageSize - i);
            List orderI = new ArrayList();
            for (var order : list) {
                var time = Long.parseLong(order.get(3).toString());
                if (time <= timeEnd && time > timeBegin) {
                    var orderPrice = Integer.parseInt(order.get(2).toString());
                    if (orderI.size() == 0) {
                        orderI.addAll(order);
                        orderI.set(3, timeEnd);
                        orderI.add(orderPrice);
                        orderI.add(orderPrice);
                        orderI.add(orderPrice);
                        orderI.add(orderPrice);
                    } else {
                        orderI.set(9, orderPrice);
                        if (Integer.parseInt(orderI.get(10).toString()) < orderPrice) {
                            orderI.set(10, orderPrice);
                        }
                        if (Integer.parseInt(orderI.get(11).toString()) > orderPrice) {
                            orderI.set(11, orderPrice);
                        }
                    }
                }
            }
            if (orderI.size() != 0) {
                newList.add(orderI);
            } else {
                var lastOrderNew = new ArrayList();
                lastOrderNew.addAll(lastOrder);
                lastOrderNew.set(3, timeEnd);
                newList.add(lastOrderNew);
            }
        }

        return newList;
    }

    class OrderEntity {
        //gamecoin数量
        String amount;
        //chr数量
        String chr;
        //价格
        String price;
        //时间
        String time;
        //先挂单的用户地址
        String sender;
        //1用chrToken换gamecoin，反之2
        String sale;
        //后挂单的用户地址
        String to;
        //手续费
        String taxFee;
        //以chrToken计价价格，=1除以price
        String chrTokenPirce;
        //以chrToken数量，=amount除以price
        String chrTokenAmount;
    }

    class OrderKlineEntity {
        //gamecoin数量
        String amount;
        //chr数量
        String chr;
        //价格
        String price;
        //时间
        String time;
        //先挂单的用户地址
        String sender;
        //1用chrToken换gamecoin，反之2
        String sale;
        //后挂单的用户地址
        String to;
        //手续费
        String taxFee;
        //开盘价
        String beginPrice;
        //收盘价
        String endPrice;
        //最高价
        String maxPrice;
        //最低价
        String minPrice;
    }

    @RequestMapping("/gameCoin/getOrdersByAddress/{methodName}/{address}")
    public ResponseEntity<?> getOrdersByAddress(@PathVariable String methodName, @PathVariable String address) {
        if (methodName == null || methodName.length() == 0) {
            return new ResponseEntity(400, "methodName不能为空,可选：getBuyOrders,getSaleOrders！");
        }
        if (address == null || address.length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        String key = "getOrders:" + methodName;
        List<List> ordersRedis = null;
        try {
            ordersRedis = (List<List>) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            logger.error("error:", e);
        }
        List myOrders = new ArrayList();
        if (ordersRedis == null || ordersRedis.size() == 0) {
            ordersRedis = getOrders(methodName);
        }
        ordersRedis.forEach(order -> {
            if (order.get(4).equals(address)) {
                myOrders.add(order);
            }
        });
        //价格翻转为chrToken计价
        priceTypeTransfer(ordersRedis);
        return new ResponseEntity(myOrders);
    }


    public static String getUserWalletPrivate(Map useWallet) {
        String encrypt_key = useWallet.get("encrypt_key").toString();
        String encrypted_private = useWallet.get("encrypted_private").toString();
        String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
        return walletPrivate;
    }

    public Map getUserWallet(Map user, String address) {
        Map walletMap = new HashMap<>();
        walletMap.put("tableName", UserController.USER_WALLET_TABLE);
        walletMap.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        baseModel.setPageNo(1);
        baseModel.setPageSize(10);
        List<Map> userWalletList = baseDao.selectBaseList(walletMap, baseModel);
        Map useWallet = null;
        for (var wallet : userWalletList) {
            if (wallet.get("address").equals(address)) {
                useWallet = wallet;
            }
        }
        return useWallet;
    }


    public static SingleKeyring getOperatorSingleKeyring() {
        SingleKeyring keyring = KeyringFactory.createFromPrivateKey(getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
        return keyring;
    }

    public static SingleKeyring getSingleKeyring(String privateKey) {
        SingleKeyring keyring = KeyringFactory.createFromPrivateKey(privateKey);
        return keyring;
    }

    public static SingleKeyring getSingleKeyring(Map userWallet) {
        SingleKeyring keyring = KeyringFactory.createFromPrivateKey(getUserWalletPrivate(userWallet));
        return keyring;
    }

    public static TransactionReceipt.TransactionReceiptData addSaleOrder(SingleKeyring keyring, BigInteger amount, BigInteger price) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(amount);
        params.add(price);
        return addOrder(keyring, params, new BigInteger("0"), "addSaleOrder");
    }

    public static TransactionReceipt.TransactionReceiptData addBuyOrder(SingleKeyring keyring, BigInteger amount, BigInteger price) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(price);
        return addOrder(keyring, params, amount, "addBuyOrder");
    }

    public static TransactionReceipt.TransactionReceiptData cancelSaleOrder(SingleKeyring keyring, BigInteger time) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(time);
        return addOrder(keyring, params, new BigInteger("0"), "cancelSaleOrder");
    }

    public static TransactionReceipt.TransactionReceiptData cancelBuyOrder(SingleKeyring keyring, BigInteger time) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(time);
        return addOrder(keyring, params, new BigInteger("0"), "cancelBuyOrder");
    }

    public static TransactionReceipt.TransactionReceiptData addOrder(SingleKeyring keyring, List<Object> params, BigInteger amount, String methodName) throws Exception {
        Caver caver = new Caver(MY_SCN_HOST);
        SingleKeyring systemKeyring = KeyringFactory.createFromPrivateKey(getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
        //设置操作人，gas费默认由操作人付款
        caver.wallet.add(keyring);
        SendOptions sendOptions = new SendOptions();
        sendOptions.setFrom(keyring.getAddress());
        sendOptions.setGas(gas);
        sendOptions.setValue(amount);

        if (!keyring.getAddress().equals(systemKeyring.getAddress())) {
            caver.wallet.add(systemKeyring);
            sendOptions.setFeeDelegation(true);
            sendOptions.setFeePayer(systemKeyring.getAddress());
        }
        ContractMethod method = caver.contract.create(SCNContractController.ABI, SCNContractController.GAME_COIN_CONTRACT_ADDRESS).getMethod(methodName);
        PollingTransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(caver, 5000, 10);
        var ret = method.send(params, sendOptions, processor);
        if (!methodName.startsWith("match")) {
            //暂不打印matchOrder相关
            logger.info(methodName + " :" + JSON.toJSONString(ret));
        }
        if (ret.getTxError() != null && ret.getTxError().length() != 0) {
            logger.error("addOrder method:" + methodName + ",ret:" + JSON.toJSONString(ret));
            throw new Exception("链上交易失败，错误码：" + ret.getTxError());
        }
        return ret;
    }

    public static List<Type> queryItem(SingleKeyring keyring, List<Object> params, String methodName) throws Exception {
        Caver caver = new Caver(MY_SCN_HOST);
        SingleKeyring systemKeyring = KeyringFactory.createFromPrivateKey(getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
        //设置操作人，gas费默认由操作人付款
        caver.wallet.add(keyring);

        if (!keyring.getAddress().equals(systemKeyring.getAddress())) {
            caver.wallet.add(systemKeyring);
        }
        ContractMethod method = caver.contract.create(SCNContractController.ABI, SCNContractController.GAME_COIN_CONTRACT_ADDRESS).getMethod(methodName);
        var ret = method.call(params);
        logger.info(methodName + " :" + JSON.toJSONString(ret));
        return ret;
    }

    public static void testTransfer(String address, String amount) throws Exception {
        SingleKeyring systemKeyring = KeyringFactory.createFromPrivateKey(getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
        List<Object> params = new ArrayList<>();
        params.add(address);
        params.add(amount);
        addOrder(systemKeyring, params, new BigInteger("0"), "transfer");
    }

    public static List<List> getOrders(String methodName) {
        Caver caver = new Caver(MY_SCN_HOST);
        List arr = null;
        try {
            Contract contract = caver.contract.create(SCNContractController.ABI, SCNContractController.GAME_COIN_CONTRACT_ADDRESS);
            SingleKeyring keyring = KeyringFactory.createFromPrivateKey(getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
            //设置操作人，gas费默认由操作人付款
            caver.wallet.add(keyring);
            ContractMethod method = contract.getMethod(methodName);
            List<Object> params = new ArrayList<>();
            var ret = method.call(params);
            if (ret.size() != 0) {
                arr = (List) ret.get(0).getValue();
                logger.info(methodName + " :" + JSON.toJSONString(arr));
            }
        } catch (Exception e) {
            logger.error(methodName + " error！", e);
            e.printStackTrace();
            return null;
        }
        List<List> ordersValues = new ArrayList();
        arr.forEach(order -> {
            StaticStruct orderValus = (StaticStruct) order;
            List valueList = new ArrayList();
            orderValus.getValue().forEach(v -> {
                valueList.add(v.getValue());

            });
            ordersValues.add(valueList);
        });
        return ordersValues;
    }

    public static String balanceOf(String address) {
        Caver caver = new Caver(MY_SCN_HOST);
        String result = "0";
        List<Type> ret = null;
        try {
            Contract contract = caver.contract.create(SCNContractController.ABI, SCNContractController.GAME_COIN_CONTRACT_ADDRESS);
            ContractMethod method = contract.getMethod("balanceOf");
            List<Object> params = new ArrayList<>();
            params.add(address);
            ret = method.call(params);
            result = ret.get(0).getValue().toString();
            logger.info("balanceOf :" + ret.get(0).getValue().toString());
        } catch (Exception e) {
            logger.error("balanceOf error！", e);
            e.printStackTrace();
            return null;
        }
        //保留4位小数转换
        return getPriceScaleDecimal(result);
    }

    public static TransactionReceipt.TransactionReceiptData matchSaleOrder() throws Exception {
        List<Object> params = new ArrayList<>();
        SingleKeyring systemKeyring = KeyringFactory.createFromPrivateKey(getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
        return addOrder(systemKeyring, params, new BigInteger("0"), "matchSaleOrder");
    }

    public static TransactionReceipt.TransactionReceiptData matchBuyOrder() throws Exception {
        List<Object> params = new ArrayList<>();
        SingleKeyring systemKeyring = KeyringFactory.createFromPrivateKey(getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
        return addOrder(systemKeyring, params, new BigInteger("0"), "matchBuyOrder");
    }

    public static void main(String[] args) {
        try {
//            balanceOf();
//            addSaleOrder(getOperatorSingleKeyring(), new BigInteger("1"), new BigInteger("9"));
//            addBuyOrder(getOperatorSingleKeyring(), new BigInteger("1"), new BigInteger("11"));
//            getOrders("getBuyOrders");
//            List orders = getOrders("getSaleOrders");
//            List myOrders = new ArrayList();
//            orders.forEach(order -> {
//                StaticStruct orderStr = (StaticStruct) order;
//                if (orderStr.getValue().get(3).getValue().equals("0x85c616c2d51b6c653e00325ae85660d5b0c50786")) {
//                    myOrders.add(order);
//                }
//            });
//            testTransfer("0x85c616c2d51b6c653e00325ae85660d5b0c50786", "10000000000000");
//            List addresses = new ArrayList();
//            addresses.add("0x83bc8d296e2a0d07425915d0e4b3f3c058db9415");
//            addresses.add("1");
//            var ret = queryItem(getOperatorSingleKeyring(), addresses, "userItemMap");
//            logger.info(JSON.toJSONString(ret));
//
//            BigDecimal priceInt = BigDecimal.valueOf(Long.parseLong("7"));
//            var transferPrice = new BigDecimal(1).setScale(5)
//                    .divide(priceInt, RoundingMode.HALF_DOWN);
//            transferPrice.setScale(5);
//            String transferPriceStr = transferPrice.toPlainString();
//            logger.info(transferPriceStr);

            BigInteger price = new BigDecimal("3.28134").setScale(4, RoundingMode.DOWN).multiply(new BigDecimal(10000)).toBigInteger();
            logger.info(price.toString());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}

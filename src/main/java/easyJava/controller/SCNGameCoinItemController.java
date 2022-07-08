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
import easyJava.utils.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;


/**
 * 服务链上，gamecoin和nft道具兑换相关
 */
@RestController
public class SCNGameCoinItemController {
    private static final Logger logger = LoggerFactory.getLogger(SCNGameCoinItemController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    UserController userController;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String MY_SCN_HOST = SCNGameCoinController.MY_SCN_HOST;
    public static final String SCN_CHILD_OPERATOR = SCNGameCoinController.SCN_CHILD_OPERATOR;
    public static final String SCN_CHILD_OPERATOR_PASSWORD = SCNGameCoinController.SCN_CHILD_OPERATOR_PASSWORD;
    public static final String ITEM_LOG_TABLE = "item_change_log";

    public static ObjectMapper mapper = new ObjectMapper();

    public static String getPrivateKeyFromJson(String keyStoreJSON, String pwd) {
        String fromPrivateKey = "";
        try {
            logger.info(keyStoreJSON);
            KeyStore keyStore = JSON.parseObject(keyStoreJSON, KeyStore.class);
            logger.info(keyStore.getKeyring().get(0).toString());
            List<KeyStore.Crypto> crypto = mapper.readValue(keyStore.getKeyring().get(0).toString(), new TypeReference<List<KeyStore.Crypto>>() {
            });
            fromPrivateKey = KeyStore.Crypto.decryptCrypto(crypto.get(0), pwd);
            logger.info(fromPrivateKey);
        } catch (Exception e) {
            logger.error("sendingSCN 失败 KeyStore.Crypto.decryptCrypto:" + e.getMessage() + ",p:" + fromPrivateKey);
            e.printStackTrace();
            return null;
        }
        return fromPrivateKey;
    }

    private static BigInteger getPriceScale(String priceStr) {
        BigInteger price = new BigDecimal(priceStr).setScale(4, RoundingMode.DOWN).multiply(new BigDecimal(10000)).toBigInteger();
        return price;
    }

    /**
     * 管理员增加游戏道具
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/gameCoin/addGameItem")
    public ResponseEntity<?> addGameItem(@RequestParam Map<String, Object> map, @RequestHeader("admin_token") String token) {
        if (map.get("id") == null || map.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "id不能为空！");
        }
        if (map.get("amount") == null || map.get("amount").toString().length() == 0) {
            return new ResponseEntity(400, "amount不能为空！");
        }
        if (map.get("price") == null || map.get("price").toString().length() == 0) {
            return new ResponseEntity(400, "price不能为空！");
        }
        try {
            new AddGameItemThread(map).start();
            return new ResponseEntity();
        } catch (Exception e) {
            logger.error("addGameItem error!", e);
            return new ResponseEntity(400, "addGameItem 失败:" + e.getMessage());
        }
    }

    class AddGameItemThread extends Thread {
        Map map;

        public AddGameItemThread(Map map) {
            this.map = map;
        }

        @Override
        public void run() {
            try {
                var ret = addGameItem(getOperatorSingleKeyring(), new BigInteger(map.get("id").toString()), new BigInteger(map.get("amount").toString()),
                        getPriceScale(map.get("price").toString()));
            } catch (Exception e) {
                logger.error("AddGameItemThread error!", e);
            }
        }
    }

    /**
     * 管理员，用户之间交易道具
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/gameCoin/transferItem")
    public ResponseEntity<?> transferItem(@RequestParam Map<String, Object> map
            , @RequestHeader("admin_token") String admin_token
            , @RequestHeader("token") String token) {
        if (admin_token == null || admin_token.length() == 0) {
            return new ResponseEntity(400, "admin_token不能为空！");
        }
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);
        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        if (map.get("id") == null || map.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "id不能为空！");
        }
        if (map.get("from") == null || map.get("from").toString().length() == 0) {
            return new ResponseEntity(400, "from不能为空！");
        }
        if (map.get("to") == null || map.get("to").toString().length() == 0) {
            return new ResponseEntity(400, "to不能为空！");
        }
        if (map.get("price") == null || map.get("price").toString().length() == 0) {
            return new ResponseEntity(400, "price不能为空！");
        }
        if (map.get("count") == null || map.get("count").toString().length() == 0) {
            return new ResponseEntity(400, "count不能为空！");
        }
        if (map.get("no") == null || map.get("no").toString().length() == 0) {
            return new ResponseEntity(400, "no不能为空！");
        }
        try {
            new TransferItemThread(user, map).start();
            return new ResponseEntity(user);
        } catch (Exception e) {
            logger.error("transferItem error!", e);
            return new ResponseEntity(400, "transferItem 失败:" + e.getMessage());
        }
    }

    class TransferItemThread extends Thread {
        Map user;
        Map map;

        public TransferItemThread(Map user, Map map) {
            this.map = map;
            this.user = user;
        }

        @Override
        public void run() {
            try {
                String no = map.get("no").toString();
                Map orderMap = new HashMap<>();
                //插入订单
                orderMap.put("tableName", ITEM_LOG_TABLE);
                orderMap.put("user_id", user.get("id"));
                orderMap.put("address", map.get("from").toString() + "," + map.get("to").toString());
                orderMap.put("item_ids", map.get("id").toString());
                orderMap.put("item_counts", map.get("count").toString());
                orderMap.put("no", no);
                orderMap.put("create_time", new Date());
                baseDao.insertBase(orderMap);
                var ret = transferItem(getOperatorSingleKeyring(),
                        new BigInteger(map.get("id").toString()),
                        map.get("from").toString(),
                        map.get("to").toString(),
                        getPriceScale(map.get("price").toString()),
                        Integer.parseInt(map.get("count").toString()));
                int code = 1;
                if (ret.getTxError() != null && ret.getTxError().length() != 0) {
                    code = 0;
                }
                String time = new Date().getTime() + "";
                String sign = DigestUtils.md5Hex(no + time);
                String url = "http://52.77.31.208/api/index/changeback?no=" + no
                        + "&code=" + code
                        + "&time=" + time
                        + "&info=" + ret.getStatus()
                        + "&sign=" + sign;
                String result = HttpUtil.httpGet(url);
                logger.info("BuyGameItemThread changeback result:" + result);
                orderMap.put("chain_result", JSON.toJSONString(ret));
                orderMap.put("web_payback_result", result);
                orderMap.put("update_time", new Date());
                baseDao.updateBaseByPrimaryKey(orderMap);
            } catch (Exception e) {
                logger.error("TransferItemThread error!", e);
            }
        }
    }

    /**
     * 管理员直接发nft
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/gameCoin/sendItem")
    public ResponseEntity<?> sendItem(@RequestParam Map<String, Object> map
            , @RequestHeader("admin_token") String admin_token
            , @RequestHeader("token") String token) {
        if (admin_token == null || admin_token.length() == 0) {
            return new ResponseEntity(400, "admin_token不能为空！");
        }
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);
        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        if (map.get("id") == null || map.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "id不能为空！");
        }
        if (map.get("to") == null || map.get("to").toString().length() == 0) {
            return new ResponseEntity(400, "to不能为空！");
        }
        if (map.get("count") == null || map.get("count").toString().length() == 0) {
            return new ResponseEntity(400, "count不能为空！");
        }
        if (map.get("no") == null || map.get("no").toString().length() == 0) {
            return new ResponseEntity(400, "no不能为空！");
        }
        try {
            new SendItemThread(user, map).start();
            return new ResponseEntity(user);
        } catch (Exception e) {
            logger.error("sendItem error!", e);
            return new ResponseEntity(400, "sendItem 失败:" + e.getMessage());
        }
    }

    class SendItemThread extends Thread {
        Map user;
        Map map;

        public SendItemThread(Map user, Map map) {
            this.map = map;
            this.user = user;
        }

        @Override
        public void run() {
            try {
                String no = map.get("no").toString();
                Map orderMap = new HashMap<>();
                //插入订单
                orderMap.put("tableName", ITEM_LOG_TABLE);
                orderMap.put("user_id", user.get("id"));
                orderMap.put("address", map.get("to").toString());
                orderMap.put("item_ids", map.get("id").toString());
                orderMap.put("item_counts", map.get("count").toString());
                orderMap.put("no", no);
                orderMap.put("create_time", new Date());
                baseDao.insertBase(orderMap);
                var ret = sendItem(getOperatorSingleKeyring(),
                        new BigInteger(map.get("id").toString()),
                        Integer.parseInt(map.get("count").toString()),
                        map.get("to").toString());
                int code = 1;
                if (ret.getTxError() != null && ret.getTxError().length() != 0) {
                    code = 0;
                }
                String time = new Date().getTime() + "";
                String sign = DigestUtils.md5Hex(no + time);
                String url = "http://52.77.31.208/api/index/nftback?no=" + no
                        + "&code=" + code
                        + "&time=" + time
                        + "&info=" + ret.getStatus()
                        + "&sign=" + sign;
                String result = HttpUtil.httpGet(url);
                logger.info("BuyGameItemThread nftback result:" + result);
                orderMap.put("chain_result", JSON.toJSONString(ret));
                orderMap.put("web_payback_result", result);
                orderMap.put("update_time", new Date());
                baseDao.updateBaseByPrimaryKey(orderMap);
            } catch (Exception e) {
               logger.error("SendItemThread",e);
            }
        }
    }

    @RequestMapping("/gameCoin/getItem")
    public ResponseEntity<?> getItem(@RequestParam Map<String, Object> map) {
        if (map.get("id") == null || map.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "id不能为空！");
        }
        try {
            var addr = new ArrayList<>();
            addr.add(map.get("id").toString());
            var result = queryItem(getOperatorSingleKeyring(), addr, "itemMap");
            return new ResponseEntity(result);
        } catch (Exception e) {
            logger.error("getItem error!", e);
            return new ResponseEntity(400, "getItem 失败:" + e.getMessage());
        }
    }

    @RequestMapping("/gameCoin/buyGameItemList")
    public ResponseEntity<?> buyGameItemList(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("ids") == null || map.get("ids").toString().length() == 0) {
            return new ResponseEntity(400, "ids不能为空！");
        }
        if (map.get("counts") == null || map.get("counts").toString().length() == 0) {
            return new ResponseEntity(400, "counts不能为空！");
        }
        if (map.get("no") == null || map.get("no").toString().length() == 0) {
            return new ResponseEntity(400, "no不能为空！");
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
            var ids = Arrays.stream(map.get("ids").toString().split(",")).mapToInt(Integer::parseInt).toArray();
            var counts = Arrays.stream(map.get("counts").toString().split(",")).mapToInt(Integer::parseInt).toArray();
            //异步购买
            new BuyGameItemThread(getSingleKeyring(useWallet), ids, counts, map.get("no").toString()
                    , user, map.get("address").toString()).start();
            return new ResponseEntity(user);
        } catch (Exception e) {
            logger.error("buyGameItemList error!", e);
            return new ResponseEntity(400, "buyGameItemList 失败:" + e.getMessage());
        }
    }

    class BuyGameItemThread extends Thread {
        SingleKeyring keyring;
        int[] ids;
        int[] counts;
        String no;
        Map user;
        String address;

        public BuyGameItemThread(SingleKeyring keyring, int[] ids, int[] counts, String no, Map user, String address) {
            this.keyring = keyring;
            this.ids = ids;
            this.counts = counts;
            this.no = no;
            this.user = user;
            this.address = address;
        }

        @Override
        public void run() {
            Map orderMap = new HashMap<>();
            //插入订单
            orderMap.put("tableName", ITEM_LOG_TABLE);
            orderMap.put("user_id", user.get("id"));
            orderMap.put("address", address);
            orderMap.put("item_ids", Arrays.toString(ids));
            orderMap.put("item_counts", Arrays.toString(counts));
            orderMap.put("no", no);
            orderMap.put("create_time", new Date());
            baseDao.insertBase(orderMap);
            try {
                var ret = buyGameItems(keyring, ids, counts);
                int code = 1;
                if (ret.getTxError() != null && ret.getTxError().length() != 0) {
                    code = 0;
                }
                String time = new Date().getTime() + "";
                String sign = DigestUtils.md5Hex(no + time);
                String url = "http://52.77.31.208/api/index/payback?no=" + no
                        + "&code=" + code
                        + "&time=" + time
                        + "&info=" + ret.getStatus()
                        + "&sign=" + sign;
                String result = HttpUtil.httpGet(url);
                logger.info("BuyGameItemThread payback result:" + result);
                orderMap.put("chain_result", JSON.toJSONString(ret));
                orderMap.put("web_payback_result", result);
                orderMap.put("update_time", new Date());
                baseDao.updateBaseByPrimaryKey(orderMap);
            } catch (Exception e) {
                logger.error("BuyGameItemThread error:", e);
            }
        }
    }

    @RequestMapping("/gameCoin/queryItem")
    public ResponseEntity<?> queryItem(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("id") == null || map.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "id不能为空！");
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
            var addr = new ArrayList<>();
            addr.add(map.get("address").toString());
            addr.add(map.get("id").toString());
            var result = queryItem(getSingleKeyring(useWallet), addr, "userItemMap");
            return new ResponseEntity(result);
        } catch (Exception e) {
            logger.error("queryItem error!", e);
            return new ResponseEntity(400, "queryItem 失败:" + e.getMessage());
        }
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

    public static SingleKeyring getSingleKeyring(Map userWallet) {
        SingleKeyring keyring = KeyringFactory.createFromPrivateKey(getUserWalletPrivate(userWallet));
        return keyring;
    }

    public static TransactionReceipt.TransactionReceiptData addGameItem(SingleKeyring keyring, BigInteger id, BigInteger amount, BigInteger price) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(price);
        params.add(amount);
        return SCNGameCoinController.addOrder(keyring, params, new BigInteger("0"), "addItem");
    }

    public static TransactionReceipt.TransactionReceiptData transferItem(SingleKeyring keyring, BigInteger id, String from, String to, BigInteger price, int count) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(from);
        params.add(to);
        params.add(price);
        params.add(count);
        return SCNGameCoinController.addOrder(keyring, params, new BigInteger("0"), "transferItem");
    }

    public static TransactionReceipt.TransactionReceiptData sendItem(SingleKeyring keyring, BigInteger id, int count, String to) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(count);
        params.add(to);
        return SCNGameCoinController.addOrder(keyring, params, new BigInteger("0"), "sendItem");
    }


    public static TransactionReceipt.TransactionReceiptData buyGameItems(SingleKeyring keyring, int[] ids, int[] counts) throws Exception {
        List<Object> params = new ArrayList<>();
        params.add(ids);
        params.add(counts);
        return SCNGameCoinController.addOrder(keyring, params, new BigInteger("0"), "buyItems");
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


    public static void main(String[] args) {
        try {

            BigInteger price = new BigDecimal("3.28134").setScale(4, RoundingMode.DOWN).multiply(new BigDecimal(10000)).toBigInteger();
            logger.info(price.toString());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}

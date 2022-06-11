package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import com.klaytn.caver.transaction.type.ValueTransfer;
import com.klaytn.caver.wallet.keyring.KeyStore;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.utils.DESUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@RestController
public class SCNController {
    private static final Logger logger = LoggerFactory.getLogger(SCNController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    UserController userController;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    public static final String CHR_TOKEN_ORDER_TABLE = "chr_token_order";

    public static final String MY_SCN_HOST = "http://13.213.135.231:7551";
    public static final String MY_SCN_WS_HOST = "ws://13.213.135.231:7552";

    public static final String KLAY_SCN_ADDRESS = "0xD3CFb75cE8Ed4Cbe10e7E343676a4788eC148d50";
    public static final int USDT_ERC20_PRICE = 10;
    public static Map<String, String> usdtERC20Address = new HashMap<>();
    public static volatile BigInteger gas = BigInteger.valueOf(8000000);
    public static final String SCN_CHILD_OPERATOR = "{\"address\":\"56c8cb5daf329fc8613112b51e359b2dbae4fd97\",\"keyring\":[[{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"1a6d4aac70114be5b4eb54bf8cc11c58f23c4e8e97b2235cf6a9d0bfcc478a55\",\"cipherparams\":{\"iv\":\"24a74d100afae38093f7a5267ee17626\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"17ff967beb4c3e98c4d63c3b78c9a721a2fc5906c5d3ab43f81ec0a305c7e4c6\"},\"mac\":\"000d9abe5cd71085e4789abd1a604d77cdc31aef05eae5b1bcfbed364a94fbfb\"}]],\"id\":\"7de1963a-e59d-496b-bf34-029d50b76ab3\",\"version\":4}";
    public static final String SCN_CHILD_OPERATOR_PASSWORD = "cbor{@b9b1__#+#}";

    public static ObjectMapper mapper = new ObjectMapper();

    public static BigInteger int18 = new BigInteger("1000000000000000000");


    /**
     * 发送scn
     *
     * @param keyStoreJSON
     * @param pwd
     * @param toAddress
     * @param value
     * @throws IOException
     * @throws CipherException
     * @throws TransactionException
     */
    public static TransactionReceipt.TransactionReceiptData sendingSCN(String keyStoreJSON, String pwd, String toAddress, BigInteger value) throws TransactionException, IOException, ExecutionException, InterruptedException {
        String fromPrivateKey = getPrivateKeyFromJson(keyStoreJSON, pwd);
        var transactionReceipt = sendingSCN(fromPrivateKey, toAddress, value);
        return transactionReceipt;
    }

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

    public static TransactionReceipt.TransactionReceiptData sendingSCN(String fromPrivateKey, String toAddress, BigInteger value) throws IOException, TransactionException, ExecutionException, InterruptedException {
        if (fromPrivateKey == null) {
            return null;
        }
        Caver caver = new Caver(MY_SCN_HOST);
        SingleKeyring keyring = KeyringFactory.createFromPrivateKey(fromPrivateKey);
        String fromAddress = keyring.toAccount().getAddress();
        //Add to caver wallet.
        caver.wallet.add(keyring);
        //Create a value transfer transaction
        ValueTransfer valueTransfer = caver.transaction.valueTransfer.create(
                TxPropertyBuilder.valueTransfer()
                        .setFrom(keyring.getAddress())
                        .setTo(toAddress).setValue(value)
                        .setGas(gas));
        //Sign to the transaction
        valueTransfer.sign(keyring);
        //Send a transaction to the klaytn blockchain platform (Klaytn)
        Bytes32 result = caver.rpc.klay.sendRawTransaction(valueTransfer.getRawTransaction()).sendAsync().get();
        if (result.hasError()) {
            logger.error("sendingSCN 失败:" + result.getError().getMessage() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
            throw new RuntimeException(result.getError().getMessage());
        }
        logger.info("sendingSCN :" + result.getResult() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
        //Check transaction receipt.
        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 2000, 30);
        TransactionReceipt.TransactionReceiptData transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(result.getResult());

        return transactionReceipt;
    }

    /**
     * 使用chr购买scn链上的chrToken
     *
     * @param map
     * @param token
     * @return
     */
//    @RequestMapping("/klaySCN/buychrToken")
    @RequestMapping("/klaySCN/buyChrToken")
    public ResponseEntity<?> buyScn(@RequestParam Map<String, Object> map,
                                    @RequestHeader("token") String token
    ) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        map.put("account", user.get("account"));

        Map walletMap = new HashMap<>();
        walletMap.put("tableName", UserController.USER_WALLET_TABLE);
        walletMap.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        baseModel.setPageNo(1);
        baseModel.setPageSize(10);
        List<Map> userWalletList = baseDao.selectBaseList(walletMap, baseModel);
        boolean myWallet = false;
        Map useWallet = null;
        for (var wallet : userWalletList) {
            if (wallet.get("address").equals(map.get("address").toString())) {
                myWallet = true;
                useWallet = wallet;
            }
        }
        if (!myWallet) {
            return new ResponseEntity(400, "address不属于自己！");
        }
        BigInteger v = BigInteger.ZERO;
        try {
            v = new BigInteger(map.get("value").toString());
        } catch (Exception e) {
            return new ResponseEntity(400, "value不合法，必须是整数");
        }
        if (v.compareTo(new BigInteger("1")) < 0) {
            return new ResponseEntity(400, "value必须大于等于1");
        }
        BigInteger chrValue = null;
        BigInteger chrTokenValue = null;
        try {
            chrValue = toDecimal18(v);
            chrTokenValue = v;//chrTochrTokenPrice(v);
            if (chrTokenValue.compareTo(new BigInteger("1")) < 0) {
                return new ResponseEntity(400, "能兑换到的chrToken小于1");
            }
        } catch (Exception e) {
            logger.error("value解析失败!", e);
            return new ResponseEntity(400, "value解析失败" + map.get("value"));
        }
        Map orderMap = new HashMap<>();
        try {
            String encrypt_key = useWallet.get("encrypt_key").toString();
            String encrypted_private = useWallet.get("encrypted_private").toString();
            String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
            var chrResult =
                    KlayController.sendingCHR(walletPrivate, KlayController.SWAP_ADDRESS, chrValue);
            orderMap.put("send_chr_json", JSON.toJSONString(chrResult));
        } catch (Exception e) {
            logger.error("burnCHR error!", e);
            return new ResponseEntity(400, "chr支付失败：" + e.getMessage());
        }
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            result = sendingSCN(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD
                    , map.get("address").toString(), chrTokenValue);
            orderMap.put("send_chr_token_json", JSON.toJSONString(result));
        } catch (Exception e) {
            logger.error("send scn error!", e);
            return new ResponseEntity(400, "chr支付后，发送chrToken失败：" + e.getMessage());
        }
        //插入订单
        orderMap.put("tableName", CHR_TOKEN_ORDER_TABLE);
        orderMap.put("user_id", user.get("id"));
        orderMap.put("address", map.get("address").toString());
        orderMap.put("chr_value", chrValue.toString());
        orderMap.put("chr_token_value", chrTokenValue.toString());
        orderMap.put("type", "buyChrToken");
        orderMap.put("time", new Date());
        baseDao.insertBase(orderMap);
        return new ResponseEntity(result);
    }

    /**
     * 使用chrToken换回chr
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/klaySCN/withDrawChrToken")
    public ResponseEntity<?> withDrawchrToken(@RequestParam Map<String, Object> map,
                                              @RequestHeader("token") String token
    ) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        map.put("account", user.get("account"));

        Map walletMap = new HashMap<>();
        walletMap.put("tableName", UserController.USER_WALLET_TABLE);
        walletMap.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        baseModel.setPageNo(1);
        baseModel.setPageSize(10);
        List<Map> userWalletList = baseDao.selectBaseList(walletMap, baseModel);
        boolean myWallet = false;
        Map useWallet = null;
        for (var wallet : userWalletList) {
            if (wallet.get("address").equals(map.get("address").toString())) {
                myWallet = true;
                useWallet = wallet;
            }
        }
        if (!myWallet) {
            return new ResponseEntity(400, "address不属于自己！");
        }
        BigInteger v = BigInteger.ZERO;
        try {
            v = new BigInteger(map.get("value").toString());
        } catch (Exception e) {
            return new ResponseEntity(400, "value不合法，必须是整数");
        }
        if (v.compareTo(new BigInteger("1")) < 0) {
            return new ResponseEntity(400, "value必须大于等于1");
        }
        BigInteger chrValue = null;
        BigInteger scnValue = null;
        try {
            chrValue = toDecimal18(chrTokenToChrPrice(v));
            if (chrValue.compareTo(int18) < 0) {
                return new ResponseEntity(400, "能兑换到的chr小于1");
            }
            scnValue = v;
        } catch (Exception e) {
            logger.error("value解析失败!", e);
            return new ResponseEntity(400, "value解析失败" + map.get("value"));
        }
        Map orderMap = new HashMap<>();
        //先扣除chrToken
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            String encrypt_key = useWallet.get("encrypt_key").toString();
            String encrypted_private = useWallet.get("encrypted_private").toString();
            String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
            result = sendingSCN(walletPrivate
                    , KlayController.SWAP_ADDRESS, scnValue);
            orderMap.put("send_chr_token_json", JSON.toJSONString(result));
        } catch (Exception e) {
            logger.error("send chrToken error!", e);
            return new ResponseEntity(400, "chrToken支付，提现失败:" + e.getMessage());
        }
        //再发放chr
        try {
            var chrResult = KlayController.sendingCHRFromChrToken(useWallet.get("address").toString(), chrValue);
            orderMap.put("send_chr_json", JSON.toJSONString(chrResult));
        } catch (Exception e) {
            logger.error("burnCHR error!", e);
            return new ResponseEntity(400, "chrToken支付后，发送chr失败:" + e.getMessage());
        }

        //插入订单
        orderMap.put("tableName", CHR_TOKEN_ORDER_TABLE);
        orderMap.put("user_id", user.get("id"));
        orderMap.put("address", map.get("address").toString());
        orderMap.put("chr_value", chrValue.toString());
        orderMap.put("chr_token_value", scnValue.toString());
        orderMap.put("type", "withDrawChrToken");
        orderMap.put("time", new Date());
        baseDao.insertBase(orderMap);
        return new ResponseEntity(result);
    }

    /**
     * 使用chrToken换回chr
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/klaySCN/getChrTokenOrders")
    public ResponseEntity<?> getChrTokenOrders(@RequestParam Map<String, Object> map,
                                               @RequestHeader("token") String token
    ) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo不能为空！");
        }
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize不能为空！");
        }
        int order = 1;
        if (map.get("order") != null && map.get("order").toString().length() != 0) {
            order = Integer.parseInt(map.get("order").toString());
            map.remove("order");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        map.put("account", user.get("account"));

        Map walletMap = new HashMap<>();
        walletMap.put("tableName", UserController.USER_WALLET_TABLE);
        walletMap.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        List<Map> userWalletList = baseDao.selectBaseList(walletMap, baseModel);
        boolean myWallet = false;
        for (var wallet : userWalletList) {
            if (wallet.get("address").equals(map.get("address").toString())) {
                myWallet = true;
            }
        }
        if (!myWallet) {
            return new ResponseEntity(400, "address不属于自己！");
        }
        Map orderMap = new HashMap<>();


        if (map.get("type") != null && map.get("type").toString().length() != 0) {
            orderMap.put("type", map.get("type").toString());
        }
        //订单
        orderMap.put("tableName", CHR_TOKEN_ORDER_TABLE);
        orderMap.put("user_id", user.get("id"));
        orderMap.put("address", map.get("address").toString());
        baseModel.setOrderColumn("id");
        baseModel.setOrderAsc("desc");
        baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
        baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
        if (order == 2) {
            baseModel.setOrderAsc("asc");
        }
        List<Map> list = baseDao.selectBaseListOrder(orderMap, baseModel);
        list.forEach(orderM -> {
            orderM.remove("send_chr_json");
            orderM.remove("send_chr_token_json");
        });
        return new ResponseEntity(list);
    }

    //给某个账户发送scn，测试使用
    @RequestMapping("/test/klaySCN/sendChrTokenTo")
    public ResponseEntity<?> sendKlayTo(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        BigInteger value = BigInteger.valueOf(Long.parseLong(map.get("value").toString()));
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            result = sendingSCN(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD
                    , map.get("address").toString(), value);
        } catch (Exception e) {
            logger.error("发送sending scn失败！", e);
        }
        return new ResponseEntity(result);
    }

    /**
     * 获取chrToken的余额
     *
     * @param map
     * @return
     */
    @RequestMapping("/klaySCN/getBalance")
    public ResponseEntity<?> getBalance(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        var result = getchrTokenBalance(map.get("address").toString());
        return new ResponseEntity(result);
    }

    public static BigInteger getchrTokenBalance(String address) {
        Caver caver = new Caver(MY_SCN_HOST);
        var request = caver.rpc.klay.getBalance(address, DefaultBlockParameter.valueOf("latest"));
        BigInteger val = new BigInteger("0");
        try {
            val = request.send().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return val;
    }

    public static BigInteger getChrBalance(String address) {
        return KlayController.balanceOfCHR(address);
    }

    public static BigInteger getChrTokenBalance() {
        return getDecimal6(getchrTokenBalance(KlayController.SWAP_ADDRESS));
    }

    public static BigInteger getChrBalance() {
        return getDecimal18(getChrBalance(KlayController.SWAP_ADDRESS));
    }

    //    @RequestMapping("/klaySCN/swap/chrTochrTokenPrice")
    public ResponseEntity<?> chrTochrTokenPrice(@RequestParam Map<String, Object> map) {
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        if (map.get("value").toString().contains(".")) {
            return new ResponseEntity(400, "value不能包含小数点");
        }
        BigInteger payChrValue = new BigInteger(map.get("value").toString());
        BigInteger chrTokenMinus = chrTochrTokenPrice(payChrValue);
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", getChrBalance());
        balanceMap.put("chrTokenBalance", getChrTokenBalance());
        balanceMap.put("chrAdd", map.get("value").toString());
        balanceMap.put("chrTokenMinus", chrTokenMinus);
        return new ResponseEntity(balanceMap);
    }

    //    @RequestMapping("/klaySCN/swap/chrTokenToChrPrice")
    public ResponseEntity<?> chrTokenToChrPrice(@RequestParam Map<String, Object> map) {
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        if (map.get("value").toString().contains(".")) {
            return new ResponseEntity(400, "value不能包含小数点");
        }
        BigInteger paychrTokenValue = new BigInteger(map.get("value").toString());

        BigInteger chrMinus = chrTokenToChrPrice(paychrTokenValue);
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", getChrBalance());
        balanceMap.put("chrTokenBalance", getChrTokenBalance());
        balanceMap.put("chrTokenAdd", map.get("value").toString());
        balanceMap.put("chrMinus", chrMinus);
        return new ResponseEntity(balanceMap);
    }

    public static BigInteger chrTochrTokenPrice(BigInteger value) {
        return value;
//        BigInteger chrBalance = getChrBalance();
//        BigInteger chrTokenBalance = getChrTokenBalance();
//        return abSwap(chrBalance, chrTokenBalance, value);
    }

    public static BigInteger chrTokenToChrPrice(BigInteger value) {
        return value;
//        BigInteger chrBalance = getChrBalance();
//        BigInteger chrTokenBalance = getChrTokenBalance();
//        return abSwap(chrTokenBalance, chrBalance, value);
    }

    public static BigInteger abSwap(BigInteger a, BigInteger b, BigInteger payAValue) {
        BigInteger k = a.multiply(b);
        BigInteger aAfterAdd = a.add(payAValue);
        BigInteger divided = k.divide(aAfterAdd).add(new BigInteger("1"));
        BigInteger bMinus = b.subtract(divided);
        logger.info("k:" + k + "," + "aAfterAdd:" + aAfterAdd + "," + "divided:" + divided + "," + "bMinus:" + bMinus + ",");
        return bMinus;
    }

    /**
     * 返回除以10的18次方后，带小数点的字符串
     *
     * @param amountStr
     * @return
     */
    public static String getDecimal18(String amountStr) {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr)).divide(BigDecimal.valueOf(Math.pow(10, 18)));
        String longStr = amount.toPlainString().replaceAll("(0)+$", "");
        return longStr;
    }

    public static BigInteger getDecimal18(BigInteger amountStr) {
        BigInteger amount = amountStr.divide(int18);
        return amount;
    }


    public static BigInteger toDecimal18(String amountStr) {
        BigInteger amount = new BigInteger(amountStr).multiply(int18);
        return amount;
    }

    public static BigInteger toDecimal18(BigInteger amountStr) {
        BigInteger amount = amountStr.multiply(int18);
        return amount;
    }

    public static BigInteger getDecimal6(BigInteger amountStr) {
        BigInteger amount = amountStr.divide(new BigInteger("1000000"));
        return amount;
    }


    public static BigInteger toDecimal6(String amountStr) {
        BigInteger amount = new BigInteger(amountStr).multiply(new BigInteger("1000000"));
        return amount;
    }


    public static void main(String[] args) {
        try {
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}

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
import java.math.RoundingMode;
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
     * ??????scn
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
            logger.error("sendingSCN ?????? KeyStore.Crypto.decryptCrypto:" + e.getMessage() + ",p:" + fromPrivateKey);
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
            logger.error("sendingSCN ??????:" + result.getError().getMessage() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
            throw new RuntimeException(result.getError().getMessage());
        }
        logger.info("sendingSCN :" + result.getResult() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
        //Check transaction receipt.
        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 2000, 30);
        TransactionReceipt.TransactionReceiptData transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(result.getResult());

        return transactionReceipt;
    }

    /**
     * ??????chr??????scn?????????chrToken
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
            return new ResponseEntity(400, "token ???????????????");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address???????????????");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value???????????????");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token ?????????????????????????????????");
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
            return new ResponseEntity(400, "address??????????????????");
        }
        BigInteger v = BigInteger.ZERO;
        try {
            v = new BigInteger(map.get("value").toString());
        } catch (Exception e) {
            return new ResponseEntity(400, "value???????????????????????????");
        }
        if (v.compareTo(new BigInteger("1")) < 0) {
            return new ResponseEntity(400, "value??????????????????1");
        }
        BigInteger chrValue = null;
        BigInteger chrTokenValue = null;
        try {
            chrValue = toDecimal18(v);
            chrTokenValue = v;//chrTochrTokenPrice(v);
            if (chrTokenValue.compareTo(new BigInteger("1")) < 0) {
                return new ResponseEntity(400, "???????????????chrToken??????1");
            }
        } catch (Exception e) {
            logger.error("value????????????!", e);
            return new ResponseEntity(400, "value????????????" + map.get("value"));
        }
        Map orderMap = new HashMap<>();
        //????????????
        orderMap.put("tableName", CHR_TOKEN_ORDER_TABLE);
        orderMap.put("user_id", user.get("id"));
        orderMap.put("address", map.get("address").toString());
        orderMap.put("chr_value", chrValue.toString());
        orderMap.put("chr_token_value", chrTokenValue.toString());
        orderMap.put("type", "buyChrToken");
        orderMap.put("time", new Date());
        orderMap.put("status", 1);
        baseDao.insertBase(orderMap);
        new BuyChrToken(orderMap, useWallet, chrValue, chrTokenValue, map.get("address").toString()).start();
        return new ResponseEntity(orderMap);
    }

    class BuyChrToken extends Thread {
        Map useWallet = null;
        Map orderMap = null;
        BigInteger chrValue = null;
        BigInteger chrTokenValue = null;
        String address = null;

        public BuyChrToken(Map orderMapIn, Map userWallet, BigInteger chrValueIn, BigInteger chrTokenValueIn,
                           String addressIn) {
            orderMap = orderMapIn;
            useWallet = userWallet;
            chrValue = chrValueIn;
            chrTokenValue = chrTokenValueIn;
            address = addressIn;
        }

        @Override
        public void run() {
            //????????????
            orderMap.put("tableName", CHR_TOKEN_ORDER_TABLE);
            try {
                String encrypt_key = useWallet.get("encrypt_key").toString();
                String encrypted_private = useWallet.get("encrypted_private").toString();
                String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
                var chrResult =
                        KlayController.sendingCHR(walletPrivate, KlayController.SWAP_ADDRESS, chrValue);
                orderMap.put("send_chr_json", JSON.toJSONString(chrResult));
                orderMap.put("status", 2);
            } catch (Exception e) {
                logger.error("burnCHR error!chr???????????????" + e.getMessage(), e);
                orderMap.put("send_chr_json", e.getMessage());
                orderMap.put("status", 4);
                baseDao.updateBaseByPrimaryKey(orderMap);
                return;
//                new ResponseEntity(400, "chr???????????????" + e.getMessage())
            }
            TransactionReceipt.TransactionReceiptData result = null;
            try {
                //??????chrToken????????????chrtoken???gamecoin?????????4????????????????????????????????????????????????
                result = sendingSCN(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD
                        , address, getScaleChrToken(chrTokenValue));
                orderMap.put("send_chr_token_json", JSON.toJSONString(result));
                orderMap.put("status", 3);
            } catch (Exception e) {
                logger.error("send scn error!chr??????????????????chrToken?????????" + e.getMessage(), e);
                orderMap.put("send_chr_token_json", e.getMessage());
                orderMap.put("status", 5);
//                new ResponseEntity(400, "chr??????????????????chrToken?????????" + e.getMessage())
            }
            baseDao.updateBaseByPrimaryKey(orderMap);
        }
    }

    /**
     * ??????chrToken??????chr
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
            return new ResponseEntity(400, "token ???????????????");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address???????????????");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value???????????????");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token ?????????????????????????????????");
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
            return new ResponseEntity(400, "address??????????????????");
        }
        BigInteger v = BigInteger.ZERO;
        try {
            v = new BigInteger(map.get("value").toString());
        } catch (Exception e) {
            return new ResponseEntity(400, "value???????????????????????????");
        }
        if (v.compareTo(new BigInteger("1")) < 0) {
            return new ResponseEntity(400, "value??????????????????1");
        }
        BigInteger chrValue = null;
        BigInteger scnValue = null;
        try {
            chrValue = toDecimal18(v);
            if (chrValue.compareTo(int18) < 0) {
                return new ResponseEntity(400, "???????????????chr??????1");
            }
            scnValue = v;
        } catch (Exception e) {
            logger.error("value????????????!", e);
            return new ResponseEntity(400, "value????????????" + map.get("value"));
        }
        Map orderMap = new HashMap<>();

        //????????????
        orderMap.put("tableName", CHR_TOKEN_ORDER_TABLE);
        orderMap.put("user_id", user.get("id"));
        orderMap.put("address", map.get("address").toString());
        orderMap.put("chr_value", chrValue.toString());
        orderMap.put("chr_token_value", scnValue.toString());
        orderMap.put("type", "withDrawChrToken");
        orderMap.put("time", new Date());
        orderMap.put("status", 1);
        baseDao.insertBase(orderMap);
        new WithDrawChrToken(orderMap, useWallet, chrValue, scnValue, map.get("address").toString()).start();
        return new ResponseEntity(orderMap);


    }

    class WithDrawChrToken extends Thread {
        Map useWallet = null;
        Map orderMap = null;
        BigInteger chrValue = null;
        BigInteger chrTokenValue = null;
        String address = null;

        public WithDrawChrToken(Map orderMapIn, Map userWallet, BigInteger chrValueIn, BigInteger chrTokenValueIn,
                                String addressIn) {
            orderMap = orderMapIn;
            useWallet = userWallet;
            chrValue = chrValueIn;
            chrTokenValue = chrTokenValueIn;
            address = addressIn;
        }

        @Override
        public void run() {
//?????????chrToken
            TransactionReceipt.TransactionReceiptData result = null;
            try {
                String encrypt_key = useWallet.get("encrypt_key").toString();
                String encrypted_private = useWallet.get("encrypted_private").toString();
                String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
                result = sendingSCN(walletPrivate
                        , KlayController.SWAP_ADDRESS, getScaleChrToken(chrTokenValue));
                orderMap.put("send_chr_token_json", JSON.toJSONString(result));
                orderMap.put("status", 2);
            } catch (Exception e) {
                logger.error("send chrToken error!", e);
                orderMap.put("status", 4);
                orderMap.put("send_chr_token_json", e.getMessage());
                baseDao.updateBaseByPrimaryKey(orderMap);
                return;
//                return new ResponseEntity(400, "chrToken?????????????????????:" + e.getMessage());
            }
            //?????????chr
            try {
                var chrResult = KlayController.sendingCHRFromChrToken(useWallet.get("address").toString(), chrValue);
                orderMap.put("send_chr_json", JSON.toJSONString(chrResult));
                orderMap.put("status", 3);
            } catch (Exception e) {
                logger.error("burnCHR error!", e);
                orderMap.put("status", 5);
                orderMap.put("send_chr_json", e.getMessage());
//                return new ResponseEntity(400, "chrToken??????????????????chr??????:" + e.getMessage());
            }
            baseDao.updateBaseByPrimaryKey(orderMap);
        }
    }

    private static BigInteger getScaleChrToken(BigInteger chrTokenAmount){
        return  chrTokenAmount.multiply(new BigInteger("10").pow(SCNGameCoinController.priceScale));
    }
    private static String getScaleChrTokenDecimal(BigInteger chrTokenAmount){
        if(chrTokenAmount.compareTo(new BigInteger("0"))==0){
            return "0";
        }
        BigDecimal amount = new BigDecimal(chrTokenAmount.toString()).setScale(4, RoundingMode.DOWN).divide(new BigDecimal(10000));
        return amount.toPlainString();
    }
    /**
     * ??????chrToken??????chr
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
            return new ResponseEntity(400, "token ???????????????");
        }
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address???????????????");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo???????????????");
        }
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize???????????????");
        }
        int order = 1;
        if (map.get("order") != null && map.get("order").toString().length() != 0) {
            order = Integer.parseInt(map.get("order").toString());
            map.remove("order");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token ?????????????????????????????????");
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
            return new ResponseEntity(400, "address??????????????????");
        }
        Map orderMap = new HashMap<>();


        if (map.get("type") != null && map.get("type").toString().length() != 0) {
            orderMap.put("type", map.get("type").toString());
        }
        //??????
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

    //?????????????????????scn???????????????
    @RequestMapping("/test/klaySCN/sendChrTokenTo")
    public ResponseEntity<?> sendKlayTo(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address???????????????");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value???????????????");
        }
        BigInteger value = BigInteger.valueOf(Long.parseLong(map.get("value").toString()));
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            result = sendingSCN(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD
                    , map.get("address").toString(), value);
        } catch (Exception e) {
            logger.error("??????sending scn?????????", e);
        }
        return new ResponseEntity(result);
    }

    /**
     * ??????chrToken?????????
     *
     * @param map
     * @return
     */
    @RequestMapping("/klaySCN/getBalance")
    public ResponseEntity<?> getBalance(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address???????????????");
        }
        var result = getchrTokenBalance(map.get("address").toString());
        return new ResponseEntity(result);
    }

    public static String getchrTokenBalance(String address) {
        Caver caver = new Caver(MY_SCN_HOST);
        var request = caver.rpc.klay.getBalance(address, DefaultBlockParameter.valueOf("latest"));
        BigInteger val = new BigInteger("0");
        try {
            val = request.send().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return getScaleChrTokenDecimal(val);
    }


    /**
     * ????????????10???18????????????????????????????????????
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

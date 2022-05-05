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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class KlaySCNController {
    private static final Logger logger = LoggerFactory.getLogger(KlaySCNController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    UserController userController;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String MY_SCN_HOST = "http://13.213.28.145:7551";//http://172.31.22.236:7551
    public static final String MY_KLAY_HOST = "http://13.213.28.145:8551";

    public static final String KLAY_SCN_ADDRESS = "0xD3CFb75cE8Ed4Cbe10e7E343676a4788eC148d50";
    public static final int USDT_ERC20_PRICE = 10;
    public static Map<String, String> usdtERC20Address = new HashMap<>();
    public static volatile BigInteger gas = BigInteger.valueOf(80000);
    public static final String SCN_CHILD_OPERATOR = "{\"address\":\"56c8cb5daf329fc8613112b51e359b2dbae4fd97\",\"keyring\":[[{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"1a6d4aac70114be5b4eb54bf8cc11c58f23c4e8e97b2235cf6a9d0bfcc478a55\",\"cipherparams\":{\"iv\":\"24a74d100afae38093f7a5267ee17626\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"17ff967beb4c3e98c4d63c3b78c9a721a2fc5906c5d3ab43f81ec0a305c7e4c6\"},\"mac\":\"000d9abe5cd71085e4789abd1a604d77cdc31aef05eae5b1bcfbed364a94fbfb\"}]],\"id\":\"7de1963a-e59d-496b-bf34-029d50b76ab3\",\"version\":4}";
    public static final String SCN_CHILD_OPERATOR_PASSWORD = "cbor{@b9b1__#+#}";
    public static ObjectMapper mapper = new ObjectMapper();


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
    public static TransactionReceipt.TransactionReceiptData sendingSCN(String keyStoreJSON, String pwd, String toAddress, BigInteger value) throws IOException, TransactionException {
        logger.info(keyStoreJSON);
        KeyStore keyStore = JSON.parseObject(keyStoreJSON, KeyStore.class);
        logger.info(keyStore.getKeyring().get(0).toString());
        List<KeyStore.Crypto> crypto = mapper.readValue(keyStore.getKeyring().get(0).toString(), new TypeReference<List<KeyStore.Crypto>>() {
        });
        String fromPrivateKey = "";
        try {
            fromPrivateKey = KeyStore.Crypto.decryptCrypto(crypto.get(0), pwd);
            logger.info(fromPrivateKey);
        } catch (CipherException e) {
            logger.error("sendingSCN 失败 KeyStore.Crypto.decryptCrypto:" + e.getMessage() + ",p:" + fromPrivateKey);
            e.printStackTrace();
            return null;
        }
        var transactionReceipt = sendingSCN(fromPrivateKey, toAddress, value);
        return transactionReceipt;
    }


    public static TransactionReceipt.TransactionReceiptData sendingSCN(String fromPrivateKey, String toAddress, BigInteger value) throws IOException, TransactionException {
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
        Bytes32 result = caver.rpc.klay.sendRawTransaction(valueTransfer.getRawTransaction()).send();
        if (result.hasError()) {
            logger.error("sendingSCN 失败:" + result.getError().getMessage() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
            throw new RuntimeException(result.getError().getMessage());
        }
        logger.info("sendingSCN :" + result.getResult() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
        //Check transaction receipt.
        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
        TransactionReceipt.TransactionReceiptData transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(result.getResult());

        return transactionReceipt;
    }

    /**
     * 使用chr购买scn链上的gamecoin
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/klaySCN/buyGameCoin")
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

        Double v = Double.parseDouble(map.get("value").toString());
        if (v.compareTo(Double.valueOf(0.001)) < 1) {
            return new ResponseEntity(400, "value必须大于0.001");
        }
        BigInteger chrValue = null;
        BigInteger scnValue = null;
        try {
            chrValue = toDecimal18(map.get("value").toString());
            scnValue = toGameCoin(map.get("value").toString());
        } catch (Exception e) {
            logger.error("value解析失败!", e);
            return new ResponseEntity(400, "value解析失败" + map.get("value"));
        }
        try {
            String encrypt_key = useWallet.get("encrypt_key").toString();
            String encrypted_private = useWallet.get("encrypted_private").toString();
            String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
            KlayController.sendingCHR(walletPrivate, KlayController.SWAP_ADDRESS, chrValue);
        } catch (Exception e) {
            logger.error("burnCHR error!", e);
            return new ResponseEntity();
        }
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            result = sendingSCN(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD
                    , map.get("address").toString(), scnValue);
        } catch (Exception e) {
            logger.error("send scn error!", e);
        }
        return new ResponseEntity(result);
    }

    /**
     * 使用gamecoin换回chr
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/klaySCN/withDrawGameCoin")
    public ResponseEntity<?> withDrawGameCoin(@RequestParam Map<String, Object> map,
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
        Double v = Double.parseDouble(map.get("value").toString());
        if (v.compareTo(Double.valueOf(100000.0)) < 1) {
            return new ResponseEntity(400, "value必须大于100000");
        }
        BigInteger chrValue = null;
        BigInteger scnValue = null;
        try {
            chrValue = toDecimal18(toChr(map.get("value").toString()));
            scnValue = BigInteger.valueOf(Long.parseLong(map.get("value").toString()));
        } catch (Exception e) {
            logger.error("value解析失败!", e);
            return new ResponseEntity(400, "value解析失败" + map.get("value"));
        }
        //先扣除scn
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            String encrypt_key = useWallet.get("encrypt_key").toString();
            String encrypted_private = useWallet.get("encrypted_private").toString();
            String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
            result = sendingSCN(walletPrivate
                    , KlayController.SWAP_ADDRESS, scnValue);
        } catch (Exception e) {
            logger.error("send scn error!", e);
        }
        //再发放chr
        try {
            KlayController.sendingCHR(useWallet.get("address").toString(), chrValue);
        } catch (Exception e) {
            logger.error("burnCHR error!", e);
            return new ResponseEntity();
        }
        return new ResponseEntity(result);
    }

    //给某个账户发送scn，测试使用
    @RequestMapping("/test/klaySCN/sendSCNTo")
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

    @RequestMapping("/klaySCN/getBalance")
    public ResponseEntity<?> getBalance(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        var result = getBalance(map.get("address").toString()).toString();
        return new ResponseEntity(result);
    }

    public static BigInteger getBalance(String address) {
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

    public static AtomicLong chrBalance = new AtomicLong(10000);
    public static AtomicLong gameCoinBalance = new AtomicLong(10000);
    public static final long chrGameCoinK;

    static {
        chrGameCoinK = chrBalance.get() * gameCoinBalance.get();
    }

    @RequestMapping("/klaySCN/swap/getBalance")
    public ResponseEntity<?> swapGetBalance(@RequestParam Map<String, Object> map) {
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", chrBalance.get());
        balanceMap.put("gameCoinBalance", gameCoinBalance.get());
        return new ResponseEntity(balanceMap);
    }

    /**
     * 付出一定的chr兑换gameCoin，随着gameCoin的余额减少，同样数量chr兑换的gamecoin会越来越少
     *
     * @param map
     * @return
     */
    @RequestMapping("/klaySCN/swap/chrToGameCoin")
    public ResponseEntity<?> chrToGameCoin(@RequestParam Map<String, Object> map) {
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        long chrBalanceInt = chrBalance.addAndGet(Long.parseLong(map.get("value").toString()));
        long gameCoinMinus = gameCoinBalance.get() - chrGameCoinK / chrBalanceInt;
        long gameCoinBalanceAfter = gameCoinBalance.addAndGet(gameCoinMinus * (-1));
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", chrBalance.get());
        balanceMap.put("gameCoinBalance", gameCoinBalance.get());
        balanceMap.put("chrAdd", Integer.parseInt(map.get("value").toString()));
        balanceMap.put("gameCoinMinus", gameCoinMinus);
        return new ResponseEntity(balanceMap);
    }

    /**
     * 付出一定的gameCoin兑换chr，随着chr的余额减少，同样数量gameCoin兑换的chr会越来越少
     *
     * @param map
     * @return
     */
    @RequestMapping("/klaySCN/swap/gameCoinToChr")
    public ResponseEntity<?> gameCoinToChr(@RequestParam Map<String, Object> map) {
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        long gameCoinInt = gameCoinBalance.addAndGet(Long.parseLong(map.get("value").toString()));
        long chrMinus = chrBalance.get() - chrGameCoinK / gameCoinInt;
        long gameCoinBalanceAfter = chrBalance.addAndGet(chrMinus * (-1));
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", chrBalance.get());
        balanceMap.put("gameCoinBalance", gameCoinBalance.get());
        balanceMap.put("gameCoinAdd", Integer.parseInt(map.get("value").toString()));
        balanceMap.put("chrMinus", chrMinus);
        return new ResponseEntity(balanceMap);
    }

    public static BigInteger getDecimal18(String amountStr) {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr)).divide(BigDecimal.valueOf(Math.pow(10, 18)));
        String longStr = amount.toPlainString().replaceAll("(0)+$", "");
        BigInteger ret = BigInteger.valueOf(Long.parseLong(longStr));
        return ret;
    }

    public static BigInteger toDecimal18(String amountStr) {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr)).multiply(BigDecimal.valueOf(Math.pow(10, 18)));
        String longStr = amount.toPlainString();
        BigInteger ret = BigInteger.valueOf(Long.parseLong(longStr));
        return ret;
    }

    /**
     * 比例1 chr兑换10000个gamecoin
     *
     * @param amountStr
     * @return
     */
    public static BigInteger toGameCoin(String amountStr) {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr)).multiply(BigDecimal.valueOf(Math.pow(10, 5)));
        String longStr = amount.toPlainString();
        if (longStr.contains(".")) {
            longStr = longStr.replaceAll("(0)+$", "");
        }
        if (longStr.endsWith(".")) {
            longStr = longStr.substring(0, longStr.length() - 1);
        }
        BigInteger ret = BigInteger.valueOf(Long.parseLong(longStr));
        return ret;
    }

    /**
     * 比例1 chr兑换10000个gamecoin
     *
     * @param amountStr
     * @return
     */
    public static String toChr(String amountStr) {
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr)).divide(BigDecimal.valueOf(Math.pow(10, 5)));
        String ret = amount.toPlainString();
        if (ret.contains(".")) {
            ret = ret.replaceAll("(0)+$", "");
        }
        if (ret.endsWith(".")) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    public static void main(String[] args) {
//        try {
//            //klay.getBalance("0x38bd8d9f0acda0ce533f44adcfd02b403f411de7")
//            sendingSCN(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD, "0x38bd8d9f0acda0ce533f44adcfd02b403f411de7", new BigInteger("2"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TransactionException e) {
//            e.printStackTrace();
//        }
//        logger.info(JSON.toJSONString(getBalance("0x38bd8d9f0acda0ce533f44adcfd02b403f411de7")));

//        logger.debug(getDecimal18("1003000000000000000"));
//        logger.debug(toDecimal18("1.00123"));
//        String ret = "10.";
//        logger.debug(ret);
//        if (ret.endsWith(".")) {
//            ret = ret.substring(0, ret.length() - 1);
//        }
//        String ret = toDecimal18(toChr("100000"));
        String ret = BigInteger.valueOf(10000l).toString();
        logger.debug(ret);
    }
}

package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.datatypes.Type;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.ContractMethod;
import com.klaytn.caver.contract.SendOptions;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class SCNController {
    private static final Logger logger = LoggerFactory.getLogger(SCNController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    UserController userController;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String MY_SCN_HOST = "http://13.213.135.231:7551";
    public static final String MY_SCN_WS_HOST = "ws://13.213.135.231:7552";

    public static final String KLAY_SCN_ADDRESS = "0xD3CFb75cE8Ed4Cbe10e7E343676a4788eC148d50";
    public static final int USDT_ERC20_PRICE = 10;
    public static Map<String, String> usdtERC20Address = new HashMap<>();
    public static volatile BigInteger gas = BigInteger.valueOf(80000);
    public static final String SCN_CHILD_OPERATOR = "{\"address\":\"56c8cb5daf329fc8613112b51e359b2dbae4fd97\",\"keyring\":[[{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"1a6d4aac70114be5b4eb54bf8cc11c58f23c4e8e97b2235cf6a9d0bfcc478a55\",\"cipherparams\":{\"iv\":\"24a74d100afae38093f7a5267ee17626\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"17ff967beb4c3e98c4d63c3b78c9a721a2fc5906c5d3ab43f81ec0a305c7e4c6\"},\"mac\":\"000d9abe5cd71085e4789abd1a604d77cdc31aef05eae5b1bcfbed364a94fbfb\"}]],\"id\":\"7de1963a-e59d-496b-bf34-029d50b76ab3\",\"version\":4}";
    public static final String SCN_CHILD_OPERATOR_PASSWORD = "cbor{@b9b1__#+#}";
    public static final String SCN_CHILD_OPERATOR_ADDRESS = "0x56c8cb5daf329fc8613112b51e359b2dbae4fd97";
    public static final String GAME_COIN_CONTRACT_ADDRESS = "0xf0385e4dd297d8c439e7c5f186ecd5fddb6318d7";


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
    public static TransactionReceipt.TransactionReceiptData sendingSCN(String keyStoreJSON, String pwd, String toAddress, BigInteger value) throws TransactionException, IOException {
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

    public static TransactionReceipt.TransactionReceiptData sendingSCN(String fromPrivateKey, String toAddress, BigInteger value) throws IOException, TransactionException {
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
        BigInteger gamecoinValue = null;
        try {
            chrValue = toDecimal18(v);
            gamecoinValue = chrToGameCoinPrice(v);
            if (gamecoinValue.compareTo(new BigInteger("1")) < 0) {
                return new ResponseEntity(400, "能兑换到的gamecoin小于1");
            }
        } catch (Exception e) {
            logger.error("value解析失败!", e);
            return new ResponseEntity(400, "value解析失败" + map.get("value"));
        }
        try {
            String encrypt_key = useWallet.get("encrypt_key").toString();
            String encrypted_private = useWallet.get("encrypted_private").toString();
            String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
            var chrResult =
                    KlayController.sendingCHR(walletPrivate, KlayController.SWAP_ADDRESS, chrValue);
        } catch (Exception e) {
            logger.error("burnCHR error!", e);
            return new ResponseEntity(400, "chr支付失败：" + e.getMessage());
        }
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            result = sendingSCN(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD
                    , map.get("address").toString(), gamecoinValue);
        } catch (Exception e) {
            logger.error("send scn error!", e);
            return new ResponseEntity(400, "chr支付后，发送gamecoin失败：" + e.getMessage());
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
            chrValue = toDecimal18(gameCoinToChrPrice(v));
            if (chrValue.compareTo(new BigInteger("1")) < 0) {
                return new ResponseEntity(400, "能兑换到的chr小于1");
            }
            scnValue = v;
        } catch (Exception e) {
            logger.error("value解析失败!", e);
            return new ResponseEntity(400, "value解析失败" + map.get("value"));
        }
        //先扣除gamecoin
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            String encrypt_key = useWallet.get("encrypt_key").toString();
            String encrypted_private = useWallet.get("encrypted_private").toString();
            String walletPrivate = DESUtils.encrypt(encrypted_private, Integer.parseInt(encrypt_key));
            result = sendingSCN(walletPrivate
                    , KlayController.SWAP_ADDRESS, scnValue);
        } catch (Exception e) {
            logger.error("send gamecoin error!", e);
            return new ResponseEntity(400, "gamecoin支付，提现失败:" + e.getMessage());
        }
        //再发放chr
        try {
            KlayController.sendingCHR(useWallet.get("address").toString(), chrValue);
        } catch (Exception e) {
            logger.error("burnCHR error!", e);
            return new ResponseEntity(400, "gamecoin支付后，发送chr失败:" + e.getMessage());
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
        var result = getGameCoinBalance(map.get("address").toString());
        return new ResponseEntity(result);
    }

    public static BigInteger getGameCoinBalance(String address) {
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

    public static BigInteger getGameCoinBalance() {
        return getDecimal6(getGameCoinBalance(KlayController.SWAP_ADDRESS));
    }

    public static BigInteger getChrBalance() {
        return getDecimal18(getChrBalance(KlayController.SWAP_ADDRESS));
    }

    @RequestMapping("/klaySCN/swap/chrToGameCoinPrice")
    public ResponseEntity<?> chrToGameCoinPrice(@RequestParam Map<String, Object> map) {
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        if (map.get("value").toString().contains(".")) {
            return new ResponseEntity(400, "value不能包含小数点");
        }
        BigInteger payChrValue = new BigInteger(map.get("value").toString());
        BigInteger gameCoinMinus = chrToGameCoinPrice(payChrValue);
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", getChrBalance());
        balanceMap.put("gameCoinBalance", getGameCoinBalance());
        balanceMap.put("chrAdd", map.get("value").toString());
        balanceMap.put("gameCoinMinus", gameCoinMinus);
        return new ResponseEntity(balanceMap);
    }

    @RequestMapping("/klaySCN/swap/gameCoinToChrPrice")
    public ResponseEntity<?> gameCoinToChrPrice(@RequestParam Map<String, Object> map) {
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        if (map.get("value").toString().contains(".")) {
            return new ResponseEntity(400, "value不能包含小数点");
        }
        BigInteger payGameCoinValue = new BigInteger(map.get("value").toString());

        BigInteger chrMinus = gameCoinToChrPrice(payGameCoinValue);
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", getChrBalance());
        balanceMap.put("gameCoinBalance", getGameCoinBalance());
        balanceMap.put("gameCoinAdd", map.get("value").toString());
        balanceMap.put("chrMinus", chrMinus);
        return new ResponseEntity(balanceMap);
    }

    public static BigInteger chrToGameCoinPrice(BigInteger value) {
        BigInteger chrBalance = getChrBalance();
        BigInteger gameCoinBalance = getGameCoinBalance();
        return abSwap(chrBalance, gameCoinBalance, value);
    }

    public static BigInteger gameCoinToChrPrice(BigInteger value) {
        BigInteger chrBalance = getChrBalance();
        BigInteger gameCoinBalance = getGameCoinBalance();
        return abSwap(gameCoinBalance, chrBalance, value);
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
        BigInteger amount = amountStr.divide(new BigInteger("1000000000000000000"));
        return amount;
    }


    public static BigInteger toDecimal18(String amountStr) {
        BigInteger amount = new BigInteger(amountStr).multiply(new BigInteger("1000000000000000000"));
        return amount;
    }

    public static BigInteger toDecimal18(BigInteger amountStr) {
        BigInteger amount = amountStr.multiply(new BigInteger("1000000000000000000"));
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
//        BigInteger ret = abSwap(new BigInteger("268407048"), new BigInteger("72339069039"), new BigInteger("1"));
//        logger.debug(ret.toString());
//        ret = abSwap(new BigInteger("72339069039"), new BigInteger("268407048"), new BigInteger("269"));
//        logger.debug(ret.toString());
        try {
            balanceOf();
            addSaleOrder();
            getSaleOrders();
//            gameCoinContractDeploy();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static String gameCoinContractDeploy() {
        Caver caver = new Caver(MY_SCN_HOST);
        Contract contract = null;
        try {
            contract = caver.contract.create(SCNContractController.ABI);
            SingleKeyring keyring = KeyringFactory.createFromPrivateKey(
                    getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
            //设置操作人，gas费默认由操作人付款
            caver.wallet.add(keyring);
            SendOptions sendOptions = new SendOptions();
            sendOptions.setFrom(SCN_CHILD_OPERATOR_ADDRESS);
            sendOptions.setGas(new BigInteger("3000000"));
            String initialSupply = "10000000";
            contract.deploy(sendOptions, SCNContractController.contractBinaryData.toString(), initialSupply);
        } catch (Exception e) {
            logger.error("gameCoinContractDeploy error！", e);
            e.printStackTrace();
            return null;
        }
        return contract.getContractAddress();
    }

    public static TransactionReceipt.TransactionReceiptData addSaleOrder() {
        Caver caver = new Caver(MY_SCN_HOST);
        Contract contract = null;
        TransactionReceipt.TransactionReceiptData ret = null;
        try {
            contract = caver.contract.create(SCNContractController.ABI, GAME_COIN_CONTRACT_ADDRESS);
            SingleKeyring keyring = KeyringFactory.createFromPrivateKey(
                    getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
            //设置操作人，gas费默认由操作人付款
            caver.wallet.add(keyring);
            List<Object> params = new ArrayList<>();
            params.add(1);
            params.add(2);
            SendOptions sendOptions = new SendOptions();
            sendOptions.setFrom(keyring.getAddress());
            sendOptions.setGas(gas);
            ContractMethod method = contract.getMethod("addSaleOrder");
            ret = method.send(params, sendOptions);
            logger.info("addSaleOrder :", JSON.toJSONString(ret));
        } catch (Exception e) {
            logger.error("addSaleOrder error！", e);
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    public static List<Type> getSaleOrders() {
        Caver caver = new Caver(MY_SCN_HOST);
        Contract contract = null;
        List<Type> ret = null;
        try {
            contract = caver.contract.create(SCNContractController.ABI, GAME_COIN_CONTRACT_ADDRESS);
            SingleKeyring keyring = KeyringFactory.createFromPrivateKey(
                    getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
            //设置操作人，gas费默认由操作人付款
            caver.wallet.add(keyring);
            ContractMethod method = contract.getMethod("getSaleOrders");
            List<Object> params = new ArrayList<>();
            ret = method.call(params);
            logger.info("getSaleOrders :", JSON.toJSONString(ret));
        } catch (Exception e) {
            logger.error("getSaleOrders error！", e);
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    public static List<Type> balanceOf() {
        Caver caver = new Caver(MY_SCN_HOST);
        Contract contract = null;
        List<Type> ret = null;
        try {
            contract = caver.contract.create(SCNContractController.ABI, GAME_COIN_CONTRACT_ADDRESS);
            ContractMethod method = contract.getMethod("balanceOf");
            List<Object> params = new ArrayList<>();
            params.add(SCN_CHILD_OPERATOR_ADDRESS);
            ret = method.call(params);
            logger.info("balanceOf :", JSON.toJSONString(ret));
        } catch (Exception e) {
            logger.error("balanceOf error！", e);
            e.printStackTrace();
            return null;
        }
        return ret;
    }
}

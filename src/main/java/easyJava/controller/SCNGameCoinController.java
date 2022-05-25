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
import easyJava.entity.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.exceptions.TransactionException;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public static final String GAME_COIN_CONTRACT_ADDRESS = "0xb2434172cdb18c35473e1931da69549a7bdc304d";

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
     * 查询余额
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/gameCoin/balanceOf")
    public ResponseEntity<?> balanceOf(@RequestParam Map<String, Object> map,
                                       @RequestHeader("token") String token
    ) {
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
    public ResponseEntity<?> addSaleOrder(@RequestParam Map<String, Object> map,
                                          @RequestHeader("token") String token
    ) {
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
        return new ResponseEntity(addSaleOrder(new BigInteger(map.get("amount").toString()),
                new BigInteger(map.get("price").toString())));
    }

    /**
     * 出售gameCoin
     *
     * @param map
     * @return
     */
    @RequestMapping("/gameCoin/getOrders/{methodName}")
    public ResponseEntity<?> getOrders(@RequestParam Map<String, Object> map,
                                       @PathParam(value = "methodName") String methodName
    ) {
        if (map.get("methodName") == null || map.get("methodName").toString().length() == 0) {
            return new ResponseEntity(400, "methodName不能为空,可选：getBuyOrders,getSaleOrders！");
        }
        return new ResponseEntity(getOrders(map.get("methodName").toString()));
    }

    /**
     * 购买gameCoin挂单
     *
     * @param map
     * @param token
     * @return
     */
    @RequestMapping("/gameCoin/addBuyOrder")
    public ResponseEntity<?> addBuyOrder(@RequestParam Map<String, Object> map,
                                         @RequestHeader("token") String token
    ) {
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
        return new ResponseEntity(addBuyOrder(new BigInteger(map.get("amount").toString()),
                new BigInteger(map.get("price").toString())));
    }

    public static void main(String[] args) {
        try {
//            balanceOf();
            addSaleOrder(new BigInteger("1"), new BigInteger("9"));
            addBuyOrder(new BigInteger("1"), new BigInteger("11"));
//            getOrders("getBuyOrders");
//            getOrders("getSaleOrders");
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
            sendOptions.setGas(new BigInteger("300000000"));
            BigInteger initialSupply = new BigInteger("10000000");
            PollingTransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(caver, 5000, 10);
            contract.deploy(sendOptions, processor, SCNContractController.contractBinaryData.toString(), initialSupply);
            logger.info("gameCoinContractDeploy address:" + contract.getContractAddress());
        } catch (Exception e) {
            logger.error("gameCoinContractDeploy error！", e);
            e.printStackTrace();
            return null;
        }
        return contract.getContractAddress();
    }


    public static TransactionReceipt.TransactionReceiptData addSaleOrder(BigInteger amount, BigInteger price) {
        Caver caver = new Caver(MY_SCN_HOST);
        TransactionReceipt.TransactionReceiptData ret = null;
        try {
            Contract contract = caver.contract.create(SCNContractController.ABI, GAME_COIN_CONTRACT_ADDRESS);
            SingleKeyring keyring = KeyringFactory.createFromPrivateKey(
                    getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
            //设置操作人，gas费默认由操作人付款
            caver.wallet.add(keyring);
            List<Object> params = new ArrayList<>();
            params.add(amount);
            params.add(price);
            SendOptions sendOptions = new SendOptions();
            sendOptions.setFrom(keyring.getAddress());
            sendOptions.setGas(gas);
            ContractMethod method = contract.getMethod("addSaleOrder");
            PollingTransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(caver, 5000, 10);
            ret = method.send(params, sendOptions, processor);
            logger.info("addSaleOrder :" + JSON.toJSONString(ret));
        } catch (Exception e) {
            logger.error("addSaleOrder error！", e);
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    public static TransactionReceipt.TransactionReceiptData addBuyOrder(BigInteger amount, BigInteger price) {
        Caver caver = new Caver(MY_SCN_HOST);
        TransactionReceipt.TransactionReceiptData ret = null;
        try {
            SingleKeyring keyring = KeyringFactory.createFromPrivateKey(
                    getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
            //设置操作人，gas费默认由操作人付款
            caver.wallet.add(keyring);
            List<Object> params = new ArrayList<>();
            params.add(price);
            SendOptions sendOptions = new SendOptions();
            sendOptions.setFrom(keyring.getAddress());
            sendOptions.setGas(gas);
            sendOptions.setValue(amount);
            ContractMethod method = caver.contract.create(SCNContractController.ABI, GAME_COIN_CONTRACT_ADDRESS)
                    .getMethod("addBuyOrder");
            PollingTransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(caver, 5000, 10);
            ret = method.send(params, sendOptions, processor);
            logger.info("addBuyOrder :" + JSON.toJSONString(ret));
        } catch (Exception e) {
            logger.error("addBuyOrder error！", e);
            e.printStackTrace();
            return null;
        }
        return ret;
    }


    public static ArrayList getOrders(String methodName) {
        Caver caver = new Caver(MY_SCN_HOST);
        ArrayList arr = null;
        try {
            Contract contract = caver.contract.create(SCNContractController.ABI, GAME_COIN_CONTRACT_ADDRESS);
            SingleKeyring keyring = KeyringFactory.createFromPrivateKey(
                    getPrivateKeyFromJson(SCN_CHILD_OPERATOR, SCN_CHILD_OPERATOR_PASSWORD));
            //设置操作人，gas费默认由操作人付款
            caver.wallet.add(keyring);
            ContractMethod method = contract.getMethod(methodName);
            List<Object> params = new ArrayList<>();
            var ret = method.call(params);
            if (ret.size() != 0) {
                arr = (ArrayList) ret.get(0).getValue();
                logger.info(methodName + " :" + JSON.toJSONString(arr));
            }
        } catch (Exception e) {
            logger.error(methodName + " error！", e);
            e.printStackTrace();
            return null;
        }
        return arr;
    }

    public static String balanceOf(String address) {
        Caver caver = new Caver(MY_SCN_HOST);
        String result = "0";
        List<Type> ret = null;
        try {
            Contract contract = caver.contract.create(SCNContractController.ABI, GAME_COIN_CONTRACT_ADDRESS);
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
        return result;
    }
}

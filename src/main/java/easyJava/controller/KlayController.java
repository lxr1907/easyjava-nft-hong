package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.klaytn.caver.Caver;
import com.klaytn.caver.account.Account;
import com.klaytn.caver.contract.Contract;
import com.klaytn.caver.contract.SendOptions;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import com.klaytn.caver.transaction.type.ValueTransfer;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.utils.DateUtils;
import easyJava.utils.GenerateUtils;
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

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;


@RestController
public class KlayController {
    private static final Logger logger = LoggerFactory.getLogger(KlayController.class);
    @Autowired
    BaseDao baseDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String ORDER_TABLE = "order_usdt";
    public static final String SYSTEM_PRIVATE = "6b9332b28c2a689f464994cb7be26485aba9a3471077cf8607eefe8f849c10f8";
    public static final String SYSTEM_ADDRESS = "0xe61c910ac9A6629E88675Ba34E36620cFA966824";
    public static final String Klay_HOST = "https://api.baobab.klaytn.net:8651/";
    public static final String MY_KLAY_HOST = "http://43.132.248.207:8551";
    public static final String KLAY_CHR_ADDRESS = "0xc3aCA86099BCcf65EdC65E83fC7CCFae6bbe87dC";
    //usdt-erc20充值地址,ropsten测试链
    public static final String USDT_ADDRESS_ERC20_ROPSTEN = "0xC4f459a93169bbF3CF9Dc3c50D34502473703FB0";
    //usdt-erc20充值地址,rinkeby测试链
    public static final String USDT_ADDRESS_ERC20_RINKEBY = "0xC4f459a93169bbF3CF9Dc3c50D34502473703FB0";
    //一个usdt可以购买的合约币数量
    public static final int USDT_ERC20_PRICE = 10;
    public static Map<String, String> usdtERC20Address = new HashMap<>();
    public static volatile BigInteger gas = BigInteger.valueOf(80000);

    static {
        usdtERC20Address.put("ropsten", USDT_ADDRESS_ERC20_ROPSTEN);
        usdtERC20Address.put("rinkeby", USDT_ADDRESS_ERC20_RINKEBY);
        usdtERC20Address.put("main", USDT_ADDRESS_ERC20_RINKEBY);
    }

    /**
     * 生成privateKey
     *
     * @return
     */
    public static String generatePrivate() {
        String newPrivateKey = KeyringFactory.generateSingleKey();
        return newPrivateKey;
    }

    /**
     * 获取privateKey对应的address
     *
     * @return
     */
    public static String getWalletAddress(String newPrivateKey) {
        SingleKeyring newKeyring = KeyringFactory.createFromPrivateKey(newPrivateKey);
        Account account = newKeyring.toAccount();
        return account.getAddress();
    }

    //  BigInteger value = new BigInteger(Utils.convertToPeb(BigDecimal.ONE, "KLAY"));
    public static void main(String[] args) {
        try {
            sendingCHR(SYSTEM_PRIVATE, "0x38bd8d9f0acda0ce533f44adcfd02b403f411de7", BigInteger.valueOf(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(0 + Float.parseFloat(GenerateUtils.getRandomOneToMax(1000) + "") / 10000);
    }

    /**
     * 发送klay
     *
     * @param fromPrivateKey
     * @param toAddress
     * @param value
     * @throws IOException
     * @throws CipherException
     * @throws TransactionException
     */
    public static TransactionReceipt.TransactionReceiptData sendingKLAY(String fromPrivateKey, String toAddress, BigInteger value) throws IOException, TransactionException {
        Caver caver = new Caver(Klay_HOST);
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
            logger.error("sendingKLAY 失败:" + result.getError().getMessage() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
            throw new RuntimeException(result.getError().getMessage());
        }
        logger.info("sendingKLAY :" + result.getResult() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
        //Check transaction receipt.
        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
        TransactionReceipt.TransactionReceiptData transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(result.getResult());

        return transactionReceipt;
    }

    /**
     * 发送klay链上合约chr币
     *
     * @param fromPrivateKey
     * @param toAddress
     * @param value
     * @throws IOException
     * @throws CipherException
     * @throws TransactionException
     */
    public static void sendingCHR(String fromPrivateKey, String toAddress, BigInteger value) {
        logger.info("---------start sendingCHR,to:" + toAddress + ",amount:" + value + "-----");
        Caver caver = new Caver(Klay_HOST);
        SingleKeyring executor = KeyringFactory.createFromPrivateKey(fromPrivateKey);
        String fromAddress = executor.toAccount().getAddress();
        caver.wallet.add(executor);
        try {
            Contract contract = new Contract(caver, KlayContractController.ABI, KLAY_CHR_ADDRESS);

            SendOptions sendOptions = new SendOptions();
            sendOptions.setFrom(executor.getAddress());
            sendOptions.setGas(gas);
            TransactionReceipt.TransactionReceiptData receipt = contract.getMethod("transfer")
                    .send(Arrays.asList(toAddress, value), sendOptions);
            logger.info("------sendingCHR ret:" + JSON.toJSONString(receipt) + "--to:" + toAddress + ",amount:" + value + "------");
        } catch (Exception e) {
            logger.error("sendingCHR 失败:" + e.getMessage() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value, e);
            throw new RuntimeException(e.getMessage());
        }
        logger.info("---------end sendingCHR,to:" + toAddress + ",amount:" + value + "-----");
    }

    @RequestMapping("/klay/sendKlayTo")
    public ResponseEntity<?> sendKlayTo(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            result = sendingKLAY(SYSTEM_PRIVATE, map.get("address").toString(), BigInteger.valueOf(Long.parseLong(map.get("value").toString())));
        } catch (Exception e) {
            logger.error("发送sendingKLAY失败！", e);
        }
        return new ResponseEntity(result);
    }

    @RequestMapping("/klay/sendingCHR")
    public ResponseEntity<?> sendingCHR(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            sendingCHR(SYSTEM_PRIVATE, map.get("address").toString(), BigInteger.valueOf(Long.parseLong(map.get("value").toString())));
        } catch (Exception e) {
            logger.error("sendingCHR error！", e);
        }
        return new ResponseEntity(result);
    }

    public static String getUsdtToAddress(String chain) {
        return usdtERC20Address.get(chain);
    }

    /**
     * 生成用usdt-erc20购买合约币的订单
     *
     * @param map
     * @return
     */
    @RequestMapping("/klay/createOrder")
    public ResponseEntity createOrder(@RequestParam Map<String, Object> map, @RequestHeader("token") String
            token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        if (map.get("buy_amount") == null || map.get("buy_amount").toString().length() == 0) {
            return new ResponseEntity(400, "buy_amount 不能为空！");
        }
        if (map.get("chain") == null || map.get("chain").toString().length() == 0) {
            return new ResponseEntity(400, "chain 不能为空！");
        }
        String to = getUsdtToAddress(map.get("chain").toString());
        if (to == null) {
            return new ResponseEntity(400, "chain 未找到对应的地址！");
        }
        map.put("to_address", to);
        map.remove("chain");
        int buy_amount_int = 0;
        try {
            buy_amount_int = Integer.parseInt(map.get("buy_amount").toString());
        } catch (Exception e) {
            return new ResponseEntity(400, "buy_amount 必须是整数！");
        }
        map.put("tableName", ORDER_TABLE);
        map.put("status", 1);
        map.put("user_id", user.get("id"));
        map.put("date", DateUtils.getDateTimeString(new Date()));
        map.put("price", USDT_ERC20_PRICE);
        //生成0.0开头加3位随机数，0.0123
        Float tail = Float.parseFloat(GenerateUtils.getRandomOneToMax(1000) + "") / 10000;
        map.put("send_value", buy_amount_int + tail);

        int count = baseDao.insertBase(map);
        return new ResponseEntity(map);
    }


    /**
     * 获取充值erc20-usdt的充值地址
     *
     * @return
     */
    @RequestMapping("/klay/getUSDTAddress")
    public ResponseEntity getUSDTAddress() {
        return new ResponseEntity(usdtERC20Address);
    }

    /**
     * 获取单个erc20-usdt可兑换合约币数量
     *
     * @return
     */
    @RequestMapping("/klay/getPrice")
    public ResponseEntity getPrice() {
        return new ResponseEntity(USDT_ERC20_PRICE);
    }

    public ResponseEntity updateOrder(@RequestParam Map<String, Object> map) {
        if (map.get("id") == null || map.get("id").toString().trim().length() == 0) {
            return new ResponseEntity(400, "id不能为空！");
        }
        map.put("tableName", ORDER_TABLE);
        int count = baseDao.updateBaseByPrimaryKey(map);
        return new ResponseEntity(count);
    }

    @RequestMapping("/klay/getOrder")
    public ResponseEntity getOrder(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token 不能为空！");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token 已经失效，请重新登录！");
        }
        map.put("tableName", ORDER_TABLE);
        map.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(1);
        baseModel.setPageNo(1);
        HashMap retmap = new HashMap();
        List list = baseDao.selectBaseList(map, baseModel);
        retmap.put("list", list);
        return new ResponseEntity(retmap, 1, baseModel);
    }

}

package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.klaytn.caver.Caver;
import com.klaytn.caver.abi.datatypes.Type;
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
    UserController userController;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String ORDER_TABLE = "order_usdt";
    public static final String CHR_PRICE_TABLE = "chr_price";
    //0x0CcEB89A051711b0af1Afd52855fA76Da8aea265
//    public static final String SYSTEM_PRIVATE = "6b9332b28c2a689f464994cb7be26485aba9a3471077cf8607eefe8f849c10f8";
//    public static final String SYSTEM_ADDRESS = "0xe61c910ac9A6629E88675Ba34E36620cFA966824";
    public static final String SYSTEM_PRIVATE = "55490a18860328954ba2d80eaed84dbe1b7a2c42073b6cec3704cf591990e71f";
    public static final String SYSTEM_ADDRESS = "0x0CcEB89A051711b0af1Afd52855fA76Da8aea265";

    //??????usdt??????chr????????????????????????
    public static final String SYSTEM_USDT_PRIVATE = "8a06a7a22851b7e3a97cbb53641ec2a4b3f287d2844b91ba60823643171c4bdf";
    public static final String SYSTEM_USDT_ADDRESS = "0xC4f459a93169bbF3CF9Dc3c50D34502473703FB0";

    //??????chrToken??????chr????????????????????????
    public static final String SYSTEM_CHR_TOKEN_PRIVATE = "6b9332b28c2a689f464994cb7be26485aba9a3471077cf8607eefe8f849c10f8";
    public static final String SYSTEM_CHR_TOKEN_ADDRESS = "0xe61c910ac9A6629E88675Ba34E36620cFA966824";


    public static final String SWAP_ADDRESS = "0x0CcEB89A051711b0af1Afd52855fA76Da8aea265";
    public static final String Klay_HOST = "https://api.baobab.klaytn.net:8651/";
    public static final String MY_KLAY_HOST = "http://43.132.248.207:8551";

    //?????????chr???klay????????????
    public static final String KLAY_CHR_ADDRESS = "0xf8ed8301b4ae2bff0ccee31e06b5f49a2c015fec";//"0xD3CFb75cE8Ed4Cbe10e7E343676a4788eC148d50";
    //usdt-erc20????????????,ropsten?????????
    public static final String USDT_ADDRESS_ERC20_ROPSTEN = "0xC4f459a93169bbF3CF9Dc3c50D34502473703FB0";
    //usdt-erc20????????????,rinkeby?????????
    public static final String USDT_ADDRESS_ERC20_RINKEBY = "0xC4f459a93169bbF3CF9Dc3c50D34502473703FB0";
    //??????usdt??????????????????????????????
    public static final int USDT_ERC20_PRICE = 10;
    public static Map<String, String> usdtERC20Address = new HashMap<>();
    public static volatile BigInteger gas = BigInteger.valueOf(80000);

    static {
        usdtERC20Address.put("ropsten", USDT_ADDRESS_ERC20_ROPSTEN);
        usdtERC20Address.put("rinkeby", USDT_ADDRESS_ERC20_RINKEBY);
        usdtERC20Address.put("main", USDT_ADDRESS_ERC20_RINKEBY);
    }

    /**
     * ??????privateKey
     *
     * @return
     */
    public static String generatePrivate() {
        String newPrivateKey = KeyringFactory.generateSingleKey();
        return newPrivateKey;
    }

    /**
     * ??????privateKey?????????address
     *
     * @return
     */
    public static String getWalletAddress(String newPrivateKey) {
        SingleKeyring newKeyring = KeyringFactory.createFromPrivateKey(newPrivateKey);
        Account account = newKeyring.toAccount();
        return account.getAddress();
    }


    /**
     * ??????klay
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
            logger.error("sendingKLAY ??????:" + result.getError().getMessage() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
            throw new RuntimeException(result.getError().getMessage());
        }
        logger.info("sendingKLAY :" + result.getResult() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
        //Check transaction receipt.
        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
        TransactionReceipt.TransactionReceiptData transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(result.getResult());

        return transactionReceipt;
    }

    /**
     * ??????klay????????????chr???
     *
     * @param toAddress
     * @param value
     */

    public static TransactionReceipt.TransactionReceiptData sendingCHR(String toAddress, BigInteger value) {
        return sendingCHR(SYSTEM_PRIVATE, toAddress, value);
    }

    /**
     * ???usdt??????chr????????????chr
     *
     * @param toAddress
     * @param value
     * @return
     */
    public static TransactionReceipt.TransactionReceiptData sendingCHRFromUSDTBuy(String toAddress, BigInteger value) {
        return sendingCHR(SYSTEM_USDT_PRIVATE, toAddress, value);
    }

    /**
     * ???chrToken??????????????????chr
     *
     * @param toAddress
     * @param value
     * @return
     */
    public static TransactionReceipt.TransactionReceiptData sendingCHRFromChrToken(String toAddress, BigInteger value) {
        return sendingCHR(SYSTEM_CHR_TOKEN_PRIVATE, toAddress, value);
    }

    /**
     * ??????klay????????????chr???
     *
     * @param toAddress
     * @param value
     */
    public static TransactionReceipt.TransactionReceiptData sendingCHR(String privateKey, String toAddress, BigInteger value) {
        logger.info("---------start sendingCHR,to:" + toAddress + ",amount:" + value + "-----");
        Caver caver = new Caver(Klay_HOST);
        SingleKeyring executor = KeyringFactory.createFromPrivateKey(privateKey);
        String fromAddress = executor.toAccount().getAddress();
        SingleKeyring feePayer = KeyringFactory.createFromPrivateKey(SYSTEM_PRIVATE);
        String feePayerAddress = executor.toAccount().getAddress();
        caver.wallet.add(executor);
        SendOptions sendOptions = new SendOptions();
        if (!privateKey.equals(SYSTEM_PRIVATE)) {
            caver.wallet.add(feePayer);
            sendOptions.setFeeDelegation(true);
            sendOptions.setFeePayer(SYSTEM_ADDRESS);
        }
        TransactionReceipt.TransactionReceiptData receipt = null;
        try {
            Contract contract = new Contract(caver, KlayContractController.ABI, KLAY_CHR_ADDRESS);

            sendOptions.setFrom(executor.getAddress());
            sendOptions.setGas(gas);
            receipt = contract.getMethod("transfer")
                    .send(Arrays.asList(toAddress, value), sendOptions);
            String error = receipt.getTxError();
            if (error == null) {
                logger.info("------sendingCHR ret:" + JSON.toJSONString(receipt) + "--to:" + toAddress + ",amount:" + value + "------");
            } else {
                String message=JSON.toJSONString(receipt);
                logger.error("------sendingCHR ret error:" + message + "--to:" + toAddress + ",amount:" + value + "------");
                throw new RuntimeException(message);
            }
        } catch (Exception e) {
            logger.error("sendingCHR ret error:" + e.getMessage() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value, e);
            throw new RuntimeException(e.getMessage());
        }
        logger.info("---------end sendingCHR,to:" + toAddress + ",amount:" + value + "-----");
        return receipt;
    }

    public static BigInteger balanceOfCHR(String toAddress) {
        logger.info("---------  start balanceOfCHR,to:" + toAddress + "  -----");
        Caver caver = new Caver(Klay_HOST);
        SingleKeyring feePayer = KeyringFactory.createFromPrivateKey(SYSTEM_PRIVATE);
        caver.wallet.add(feePayer);
        List<Type> receipt = null;
        try {
            Contract contract = new Contract(caver, KlayContractController.ABI, KLAY_CHR_ADDRESS);
            receipt = contract.getMethod("balanceOf")
                    .call(Arrays.asList(toAddress));
            logger.info("------balanceOfCHR ret:" + JSON.toJSONString(receipt) + "--to:" + toAddress + "------");
        } catch (Exception e) {
            logger.error("balanceOfCHR ??????:" + e.getMessage() + ",to:" + toAddress, e);
            throw new RuntimeException(e.getMessage());
        }
        return (BigInteger) receipt.get(0).getValue();
    }

    public static void withDrawCHR(String fromPrivateKey, String toAddress, BigInteger value) {
        logger.info("---------start withDrawCHR,to:" + toAddress + ",amount:" + value + "-----");
        Caver caver = new Caver(Klay_HOST);
        SingleKeyring executor = KeyringFactory.createFromPrivateKey(fromPrivateKey);
        String fromAddress = executor.toAccount().getAddress();
        caver.wallet.add(executor);
        try {
            Contract contract = new Contract(caver, KlayContractController.ABI, KLAY_CHR_ADDRESS);
            SendOptions sendOptions = new SendOptions();
            sendOptions.setFrom(executor.getAddress());
            sendOptions.setGas(gas.multiply(BigInteger.valueOf(2)));
            TransactionReceipt.TransactionReceiptData receipt = contract.getMethod("withDraw")
                    .send(Arrays.asList(value, toAddress), sendOptions);
            logger.info("------withDrawCHR ret:" + JSON.toJSONString(receipt) + "--to:" + toAddress + ",amount:" + value + "------");
            if (receipt.getTxError() != null && receipt.getTxError().length() != 0) {
                throw new Exception("?????????????????????????????????" + receipt.getTxError());
            }
        } catch (Exception e) {
            logger.error("withDrawCHR ??????:" + e.getMessage() + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value, e);
            throw new RuntimeException(e.getMessage());
        }
        logger.info("---------end withDrawCHR,to:" + toAddress + ",amount:" + value + "-----");
    }

    /**
     * ?????????????????????????????????burnAddress?????????chr
     *
     * @param burnAddress
     * @param value
     */
    public static void burnCHR(String burnAddress, BigInteger value) {
        logger.info("---------start burnCHR,amount:" + value + "-----");
        Caver caver = new Caver(Klay_HOST);
        SingleKeyring executor = KeyringFactory.createFromPrivateKey(SYSTEM_PRIVATE);
        logger.info("--------- burnCHR,from:" + burnAddress + "amount:" + value + "-----");
        //??????????????????gas???????????????????????????
        caver.wallet.add(executor);
        try {
            Contract contract = new Contract(caver, KlayContractController.ABI, KLAY_CHR_ADDRESS);
            SendOptions sendOptions = new SendOptions();
            sendOptions.setFrom(executor.getAddress());
            sendOptions.setGas(gas.multiply(BigInteger.valueOf(2)));
            TransactionReceipt.TransactionReceiptData receipt = contract.getMethod("burn")
                    .send(Arrays.asList(value, burnAddress), sendOptions);
            logger.info("---------end burnCHR,from:" + burnAddress + ",amount:" + value + "-----ret:" + JSON.toJSONString(receipt));
        } catch (Exception e) {
            logger.error("burnCHR ??????:" + e.getMessage() + ",from:" + burnAddress + ",val:" + value, e);
            throw new RuntimeException(e.getMessage());
        }
        logger.info("---------end burnCHR,from:" + burnAddress + ",amount:" + value + "-----");
    }

    //?????????????????????klay???????????????
    //    @RequestMapping("/klay/sendKlayTo")
    public ResponseEntity<?> sendKlayTo(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address???????????????");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value???????????????");
        }
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            result = sendingKLAY(SYSTEM_PRIVATE, map.get("address").toString(), BigInteger.valueOf(Long.parseLong(map.get("value").toString())));
        } catch (Exception e) {
            logger.error("??????sendingKLAY?????????", e);
        }
        return new ResponseEntity(result);
    }

    @RequestMapping("/klay/withDrawCHR")
    public ResponseEntity<?> withDrawCHR(@RequestParam Map<String, Object> map,
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
        if (map.get("code") == null || map.get("code").toString().length() == 0) {
            return new ResponseEntity(400, "????????????????????????");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token ?????????????????????????????????");
        }
        map.put("account", user.get("account"));
        if (userController.checkEmailCode(map) == 0) {
            return new ResponseEntity(400, "??????????????????");
        }
        Map walletMap = new HashMap<>();
        walletMap.put("tableName", UserController.USER_WALLET_TABLE);
        walletMap.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        baseModel.setPageNo(1);
        baseModel.setPageSize(10);
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
        try {
            withDrawCHR(SYSTEM_PRIVATE, map.get("address").toString(), BigInteger.valueOf(Long.parseLong(map.get("value").toString())));
        } catch (Exception e) {
            logger.error("withDrawCHR?????????", e);
            return new ResponseEntity("withDrawCHR?????????");
        }
        return new ResponseEntity();
    }

    //?????????????????????chr???????????????
    @RequestMapping("/test/klay/sendingCHR")
    public ResponseEntity<?> sendingCHRApi(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address???????????????");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value???????????????");
        }
        BigInteger value = new BigInteger(map.get("value").toString());
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            sendingCHR(map.get("address").toString(), value);
        } catch (Exception e) {
            logger.error("sendingCHR error???", e);
        }
        return new ResponseEntity(result);
    }

    public static String getUsdtToAddress(String chain) {
        return usdtERC20Address.get(chain);
    }

    /**
     * ?????????usdt-erc20????????????????????????
     *
     * @param map
     * @return
     */
    @RequestMapping("/klay/createOrder")
    public ResponseEntity createOrder(@RequestParam Map<String, Object> map,
                                      @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token ???????????????");
        }
        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token ?????????????????????????????????");
        }
        if (map.get("buy_amount") == null || map.get("buy_amount").toString().length() == 0) {
            return new ResponseEntity(400, "buy_amount ???????????????");
        }
        if (map.get("chain") == null || map.get("chain").toString().length() == 0) {
            return new ResponseEntity(400, "chain ???????????????");
        }
        String to = getUsdtToAddress(map.get("chain").toString());
        if (to == null) {
            return new ResponseEntity(400, "chain ???????????????????????????");
        }
        map.put("to_address", to);
        map.remove("chain");
        int buy_amount_int = 0;
        try {
            buy_amount_int = Integer.parseInt(map.get("buy_amount").toString());
        } catch (Exception e) {
            return new ResponseEntity(400, "buy_amount ??????????????????");
        }
        map.put("tableName", ORDER_TABLE);
        map.put("status", 1);
        map.put("user_id", user.get("id"));
        map.put("date", DateUtils.getDateTimeString(new Date()));
        map.put("price", USDT_ERC20_PRICE);
        //??????0.0?????????3???????????????0.0123
        Float tail = Float.parseFloat(GenerateUtils.getRandomOneToMax(1000) + "") / 10000;
        map.put("send_value", buy_amount_int + tail);

        int count = baseDao.insertBase(map);
        return new ResponseEntity(map);
    }


    /**
     * ????????????erc20-usdt???????????????
     *
     * @return
     */
    @RequestMapping("/klay/getUSDTAddress")
    public ResponseEntity getUSDTAddress() {
        return new ResponseEntity(usdtERC20Address);
    }

    /**
     * ????????????erc20-usdt????????????????????????
     *
     * @return
     */
    @RequestMapping("/klay/getPrice")
    public ResponseEntity getPrice(@RequestParam Map<String, Object> map) {
        map.put("tableName", CHR_PRICE_TABLE);
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(10);
        baseModel.setPageNo(1);
        HashMap retmap = new HashMap();
        List list = baseDao.selectBaseList(map, baseModel);
        retmap.put("list", list);
        return new ResponseEntity(retmap, 1, baseModel);
    }

    public ResponseEntity updateOrder(@RequestParam Map<String, Object> map) {
        if (map.get("id") == null || map.get("id").toString().trim().length() == 0) {
            return new ResponseEntity(400, "id???????????????");
        }
        map.put("tableName", ORDER_TABLE);
        int count = baseDao.updateBaseByPrimaryKey(map);
        return new ResponseEntity(count);
    }

    @RequestMapping("/klay/getOrder")
    public ResponseEntity getOrder(@RequestParam Map<String, Object> map, @RequestHeader("token") String token) {
        if (token == null || token.length() == 0) {
            return new ResponseEntity(400, "token ???????????????");
        }
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize???????????????");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo???????????????");
        }

        Map user = (Map) redisTemplate.opsForValue().get(token);

        if (user == null || user.get("id").toString().length() == 0) {
            return new ResponseEntity(400, "token ?????????????????????????????????");
        }
        map.put("tableName", ORDER_TABLE);
        map.put("user_id", user.get("id"));
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
        baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
        baseModel.setOrderColumn("id");
        baseModel.setOrderAsc("desc");
        HashMap retmap = new HashMap();
        List list = baseDao.selectBaseListOrder(map, baseModel);
        retmap.put("list", list);
        return new ResponseEntity(retmap, 1, baseModel);
    }

    @RequestMapping("/klaySCN/contractDeploy")
    public ResponseEntity<?> contractDeploy(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address???????????????");
        }

        return new ResponseEntity(contractDeploy());
    }

    /**
     * ????????????
     *
     * @return
     */
    public static String contractDeploy() {
        Caver caver = new Caver(Klay_HOST);
        Contract contract = null;
        try {
            contract = caver.contract.create(KlayContractController.ABI);
            SingleKeyring keyring = KeyringFactory.createFromPrivateKey(SYSTEM_PRIVATE);
            //??????????????????gas???????????????????????????
            caver.wallet.add(keyring);
            SendOptions sendOptions = new SendOptions();
            sendOptions.setFrom(SYSTEM_ADDRESS);
            sendOptions.setGas(new BigInteger("3000000"));
            String initialSupply = "10000000";
            contract.deploy(sendOptions, KlayContractController.contractBinaryData.toString(), initialSupply);
        } catch (Exception e) {
            logger.error("contractDeploy error???", e);
            e.printStackTrace();
            return null;
        }
        return contract.getContractAddress();
    }

    //  BigInteger value = new BigInteger(Utils.convertToPeb(BigDecimal.ONE, "KLAY"));
    public static void main(String[] args) {
        try {
            //????????????
//            String address = contractDeploy();
//            logger.info(address);
//            burnCHR("0x83bc8d296e2a0d07425915d0e4b3f3c058db9415",new BigInteger("3"));
//            sendingCHR(SYSTEM_USDT_ADDRESS, new BigInteger("66116788386621325939833066"));
//            sendingCHR(SYSTEM_CHR_TOKEN_ADDRESS, new BigInteger("66116788386621325939833066"));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(0 + Float.parseFloat(GenerateUtils.getRandomOneToMax(1000) + "") / 10000);
        BigInteger d = balanceOfCHR(SYSTEM_ADDRESS);
        logger.info(d.toString());
        d = balanceOfCHR(SYSTEM_USDT_ADDRESS);
        logger.info(d.toString());
        d = balanceOfCHR(SYSTEM_CHR_TOKEN_ADDRESS);
        logger.info(d.toString());
    }
}

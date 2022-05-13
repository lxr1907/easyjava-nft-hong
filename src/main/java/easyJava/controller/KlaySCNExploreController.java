package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.request.KlayLogFilter;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.KlayLogs;
import com.klaytn.caver.methods.response.Transaction;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class KlaySCNExploreController {
    private static final Logger logger = LoggerFactory.getLogger(KlaySCNExploreController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    UserController userController;
    public static Caver caver = new Caver(KlaySCNController.MY_SCN_HOST);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static ObjectMapper mapper = new ObjectMapper();

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
        Caver caver = new Caver(KlaySCNController.MY_SCN_HOST);
        SingleKeyring keyring = KeyringFactory.createFromPrivateKey(fromPrivateKey);
        String fromAddress = keyring.toAccount().getAddress();
        //Add to caver wallet.
        caver.wallet.add(keyring);
        //Create a value transfer transaction
        ValueTransfer valueTransfer = caver.transaction.valueTransfer.create(TxPropertyBuilder.valueTransfer().setFrom(keyring.getAddress()).setTo(toAddress).setValue(value).setGas(KlaySCNController.gas));
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


    @RequestMapping("/scnExplore/getBalance")
    public ResponseEntity<?> getBalance(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        var result = getGameCoinBalance(map.get("address").toString());
        return new ResponseEntity(result);
    }

    @RequestMapping("/scnExplore/info")
    public ResponseEntity<?> info(@RequestParam Map<String, Object> map) {
        var result = getScnInfo();
        return new ResponseEntity(result);
    }

    @RequestMapping("/scnExplore/getTransaction")
    public ResponseEntity<?> getTransactionByBlockNumberAndIndex(@RequestParam Map<String, Object> map) {
        if (map.get("blockNumber") == null || map.get("blockNumber").toString().length() == 0) {
            return new ResponseEntity(400, "blockNumber不能为空！");
        }
        if (map.get("index") == null || map.get("index").toString().length() == 0) {
            return new ResponseEntity(400, "index不能为空！");
        }
        var result = getTransactionByBlockNumberAndIndex(Long.parseLong(map.get("blockNumber").toString()), Long.parseLong(map.get("index").toString()));
        return new ResponseEntity(result);
    }

    @RequestMapping("/scnExplore/getTransactionLatestExist")
    public ResponseEntity<?> getTransactionLatestExist(@RequestParam Map<String, Object> map) {
        var result = getTransactionLatestExist();
        return new ResponseEntity(result);
    }

    public static Map getTransactionLatestExist() {
        Map retMap = new HashMap();
        var blockNumberReq = caver.rpc.klay.getBlockNumber();
        BigInteger blockNumberNow = new BigInteger("0");
        try {
            blockNumberNow = blockNumberReq.send().getValue();
            retMap.put("blockNumberNow", blockNumberNow);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (var i = blockNumberNow.longValue(); i > 0; i--) {
            var getTransactionCountByNumber = caver.rpc.klay.getTransactionCountByNumber(i);
            try {
                var count = getTransactionCountByNumber.send().getValue();
                if (count.intValue() > 0) {
                    var getTransactionByBlockNumberAndIndex = caver.rpc.klay.getTransactionByBlockNumberAndIndex(blockNumberNow.longValue(), 0);
                    try {
                        var transaction = getTransactionByBlockNumberAndIndex.send();
                        retMap.put("transaction", JSON.toJSONString(transaction));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retMap;
    }

    public static Transaction getTransactionByBlockNumberAndIndex(long blockNumber, long index) {
        Transaction ret = new Transaction();
        var getTransactionByBlockNumberAndIndex = caver.rpc.klay.getTransactionByBlockNumberAndIndex(blockNumber, index);
        try {
            ret = getTransactionByBlockNumberAndIndex.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static int getTransactionCountByNumber(long blockNumber) {
        BigInteger ret = new BigInteger("0");
        var getTransactionCountByNumber = caver.rpc.klay.getTransactionCountByNumber(blockNumber);
        try {
            ret = getTransactionCountByNumber.send().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret.intValue();
    }

    public static long getBlockNumberNow() {
        var blockNumberReq = caver.rpc.klay.getBlockNumber();
        BigInteger blockNumberNow = new BigInteger("0");
        try {
            blockNumberNow = blockNumberReq.send().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockNumberNow.longValue();
    }

    public static Map getScnInfo() {
        Map retMap = new HashMap();
        Caver caver = new Caver(KlaySCNController.MY_SCN_HOST);
        var request = caver.rpc.klay.getChainID();
        try {
            var val = request.send().getValue();
            retMap.put("chainId", val);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var blockNumberNow = getBlockNumberNow();
        retMap.put("blockNumber", blockNumberNow);

        var getTransactionCountByNumber = getTransactionCountByNumber(blockNumberNow);
        retMap.put("getTransactionCountByNumber", getTransactionCountByNumber);

        var getBlockByNumber = caver.rpc.klay.getBlockByNumber(blockNumberNow);
        try {
            var val = getBlockByNumber.send();
            retMap.put("getBlockByNumber", JSON.toJSONString(val));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retMap;
    }

    public static BigInteger getGameCoinBalance(String address) {
        Caver caver = new Caver(KlaySCNController.MY_SCN_HOST);
        var request = caver.rpc.klay.getBalance(address, DefaultBlockParameter.valueOf("latest"));
        BigInteger val = new BigInteger("0");
        try {
            val = request.send().getValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return val;
    }

    public static KlayLogs getGameCoinTxLogs(String address, BigInteger toBlock) {
        Caver caver = new Caver(KlaySCNController.MY_SCN_HOST);
        KlayLogFilter filter = new KlayLogFilter();
        filter.setAddress(address);
        if (toBlock == null) {
            try {
                toBlock = caver.rpc.klay.getBlockNumber().send().getValue();
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        filter.setToBlock(toBlock);
        filter.setFromBlock(BigInteger.ZERO);
        var request = caver.rpc.klay.getLogs(filter);
        KlayLogs val = null;
        try {
            val = request.send();
        } catch (IOException e) {
            logger.error("", e);
        }
        return val;
    }

    @RequestMapping("/scnExplore/scanManual")
    public ResponseEntity<?> scanManual(@RequestParam Map<String, Object> map) {
        if (map.get("blockNum") == null || map.get("blockNum").toString().length() == 0) {
            return new ResponseEntity(400, "blockNum不能为空！");
        }
        if (map.get("endBlock") == null || map.get("endBlock").toString().length() == 0) {
            return new ResponseEntity(400, "endBlock不能为空！");
        }
        var retList = doScanSCN(Long.parseLong(map.get("blockNum").toString()),
                Long.parseLong(map.get("endBlock").toString()));
        logger.info("retList size: " + retList.size());
        retList.forEach(entity -> {
            entity.put("tableName", SNC_TX_TABLE);
            baseDao.insertIgnoreBase(entity);
        });
        return new ResponseEntity(retList);
    }

    @RequestMapping("/scnExplore/transactions")
    public ResponseEntity<?> transactions(@RequestParam Map<String, Object> map) {
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize不能为空！");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo不能为空！");
        }
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
        baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
        Map param = new HashMap<>();
        param.put("tableName", SNC_TX_TABLE);
        var retList = baseDao.selectBaseList(param, baseModel);
        return new ResponseEntity(retList);
    }

    public static final String SNC_TX_TABLE = "scn_scan_tx";

    @Scheduled(cron = "*/30 * * * * ?")
    public ResponseEntity<?> scanSCN() {
        //这个方法要在代码里写个定时器， 每隔 5或10秒 扫一次

        logger.info("scanSCN begin ");

        Long blockNum = (Long) redisTemplate.opsForValue().get(SNC_TX_TABLE + ":block");
        if (blockNum == null) {
            blockNum = 0L;
        } else {
            logger.info("doScanSCN blockNum in redis :" + blockNum);
        }
        long blockNumNow = getBlockNumberNow();
        long endBlock = blockNum + 100;
        if (blockNumNow < endBlock) {
            endBlock = blockNumNow;
        }
        List<Map> retList = doScanSCN(blockNum, endBlock);
        logger.info("retList size: " + retList.size());
        retList.forEach(map -> {
            map.put("tableName", SNC_TX_TABLE);
            baseDao.insertIgnoreBase(map);
        });
        logger.info("doScanSCN blockNum save to redis :" + endBlock);
        redisTemplate.opsForValue().set(SNC_TX_TABLE + ":block", endBlock);
        return new ResponseEntity();
    }

    public static synchronized List<Map> doScanSCN(long blockNum, long endBlock) {
        List<Map> list = new ArrayList<>();
        for (long block = blockNum; block < endBlock; block++) {
            int count = getTransactionCountByNumber(block);
            if (count != 0) {
                for (int i = 0; i < count; i++) {
                    var rest = getTransactionByBlockNumberAndIndex(blockNum, i);
                    Transaction.TransactionData data = rest.getResult();
                    String json = JSON.toJSONString(data);
                    Map row = JSON.parseObject(json);
                    if (row.containsKey("feePayerSignatures"))
                        row.remove("feePayerSignatures");
                    if (row.containsKey("maxPriorityFeePerGas"))
                        row.remove("maxPriorityFeePerGas");
                    if (row.containsKey("maxFeePerGas"))
                        row.remove("maxFeePerGas");
                    if (row.containsKey("humanReadable"))
                        row.remove("humanReadable");
                    if (row.containsKey("signatures"))
                        row.remove("signatures");
                    if (row.containsKey("accessList"))
                        row.remove("accessList");
                    logger.info("doScanSCN add row :" + json);
                    list.add(row);
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
//
//        try {
//            var logs = getScnInfo();
//            logger.info(JSON.toJSONString(logs));
//        } catch (Exception e) {
//            logger.error("", e);
//        }

        try {
//            Caver caver = new Caver(KlaySCNController.MY_SCN_HOST);
//            var ws = new WebSocketService(KlaySCNController.MY_SCN_WS_HOST, false);
//            ws.connect();
//            Caver caver = new Caver(ws);
//            KlayFilter options = new KlayFilter();
////            options.setAddress("0xD3CFb75cE8Ed4Cbe10e7E343676a4788eC148d50");
//            options.setFromBlock(new BigInteger("0"));
//            options.setToBlock(new BigInteger("1"));
//            options.addSingleTopic("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef");
//            options.addSingleTopic("0x0000000000000000000000004B2a72a43fd6E07FF3679523023898d91356eeaB");
//            Disposable disposable = caver.rpc.klay.subscribe("newHeads", (blockData) -> {
//                var trans = blockData.getParams().getResult().getTransactions();
//                if (trans != null)
//                    logger.info(JSON.toJSONString(trans));
//            });
//            Disposable disposable = caver.rpc.klay.subscribe("logs",options, (blockData) -> {
//                var trans = blockData.getParams().getResult();
//                if (trans != null)
//                    logger.info(JSON.toJSONString(trans));
//            });
            var rest = doScanSCN(1361141, 1361142);
//            var rest = getTransactionByBlockNumberAndIndex(1297882, 0);
            logger.info(JSON.toJSONString(rest));
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}

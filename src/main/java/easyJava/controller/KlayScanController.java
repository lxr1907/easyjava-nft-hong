package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import com.klaytn.caver.transaction.type.ValueTransfer;
import com.klaytn.caver.wallet.keyring.KeyringFactory;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import easyJava.dao.master.BaseDao;
import easyJava.dao.master.KlayScanDao;
import easyJava.entity.BaseModel;
import easyJava.entity.KlayTxsResult;
import easyJava.entity.ResponseEntity;
import easyJava.utils.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class KlayScanController {
    private static final Logger logger = LogManager.getLogger(KlayScanController.class);
    @Autowired
    BaseDao baseDao;
    @Autowired
    KlayScanDao klayScanDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String KLAY_TXS_TABLE = "klay_txs";
    public static final String KLAY_API_PRE = "https://api-baobab-v2.scope.klaytn.com/v2/accounts/";
    public static final String KLAY_CHR_API_TAIL = "/ftBalances";
    public static final String KLAY_CHR_TRANSFER_API_TAIL = "/ftTransfers";
    public static final String TXS_API = "/txs";
    //提现合约给用户转klay的记录，内部转账
    public static final String TXS_INTERNAL_API = "/itxs";

    @Scheduled(cron = "*/50 * * * * ?")
    @RequestMapping("/scanKlayTxs")
    public ResponseEntity<?> scanKlayTxs() {
        //查询直接给chr合约转入klay的链上交易
        KlayTxsResult result = getAddressTxs(KlayController.KLAY_CHR_ADDRESS);

        List<Map<String, Object>> retList = result.getResult();
        retList.forEach(map -> {
            map.put("tableName", KLAY_TXS_TABLE);
            baseDao.insertIgnoreBase(map);
        });
        //提现合约给用户转klay的记录，内部转账
        result = getAddressInternalTxs(KlayController.KLAY_CHR_ADDRESS);
        retList = result.getResult();
        retList.forEach(map -> {
            map.put("tableName", KLAY_TXS_TABLE);
            map.put("txHash", map.get("parentHash"));
            logger.info(JSON.toJSONString(map));
            baseDao.insertIgnoreBase(map);
        });
        return new ResponseEntity();
    }

    @RequestMapping("/klayScan/getAddressTokenTxs")
    public ResponseEntity<?> getAddressTokenTxs(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize不能为空！");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo不能为空！");
        }
        return new ResponseEntity(getAddressTokenTxs(map.get("address").toString(),
                Integer.parseInt(map.get("pageNo").toString()),
                Integer.parseInt(map.get("pageSize").toString())));
    }

    @RequestMapping("/klayScan/getAddressTokens")
    public ResponseEntity<?> getAddressTokens(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        String address = map.get("address").toString();
        Map ret = new HashMap();
        ret.put("tokens", getAddressTokens(address));
        ret.put("klay", getAddressAccounts(address));
        return new ResponseEntity(ret);
    }

    @RequestMapping("/klayScan/getAddressTx")
    public ResponseEntity<?> getAddressTx(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("pageSize") == null || map.get("pageSize").toString().length() == 0) {
            return new ResponseEntity(400, "pageSize不能为空！");
        }
        if (map.get("pageNo") == null || map.get("pageNo").toString().length() == 0) {
            return new ResponseEntity(400, "pageNo不能为空！");
        }
        map.put("tableName", KLAY_TXS_TABLE);
        String address = map.get("address").toString();
        map.put("fromAddress", address);
        map.put("toAddress", address);
        map.remove("address");
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(Integer.parseInt(map.get("pageSize").toString()));
        baseModel.setPageNo(Integer.parseInt(map.get("pageNo").toString()));
        baseModel.setOrderAsc("desc");
        baseModel.setOrderColumn("createdAt");
        var list = klayScanDao.selectBaseList(map, baseModel);
        return new ResponseEntity(list);
    }

    public static Object getAddressTokens(String address) {
        String result = HttpUtil.get(KLAY_API_PRE + address + KLAY_CHR_API_TAIL);
        return JSON.parse(result);
    }

    public static Object getAddressAccounts(String address) {
        String result = HttpUtil.get(KLAY_API_PRE + address);
        return JSON.parse(result);
    }

    /**
     * 获取address的klay转账记录
     *
     * @return
     */
    public static KlayTxsResult getAddressTxs(String address) {
        String result = HttpUtil.get(KLAY_API_PRE + address + TXS_API);
        KlayTxsResult response = JSON.parseObject(result, KlayTxsResult.class);
        return response;
    }

    /**
     * 获取address的提现klay转账记录
     *
     * @return
     */
    public static KlayTxsResult getAddressInternalTxs(String address) {
        String result = HttpUtil.get(KLAY_API_PRE + address + TXS_INTERNAL_API);
        KlayTxsResult response = JSON.parseObject(result, KlayTxsResult.class);
        return response;
    }

    /**
     * 获取address的klay链上的token转账记录
     *
     * @return
     */
    public static KlayTxsResult getAddressTokenTxs(String address, int page, int limit) {
        String result = HttpUtil.get(KLAY_API_PRE + address + KLAY_CHR_TRANSFER_API_TAIL +
                "?page=" + page + "&limit=" + limit);
        KlayTxsResult response = JSON.parseObject(result, KlayTxsResult.class);
        response.getResult().forEach(row -> {
            if (row.containsKey("amount")) {
                String amountStr = row.get("amount").toString();
                if (amountStr.startsWith("0x")) {
                    row.put("amountNum", jin_zhi(amountStr));
                }

            }
        });
        return response;
    }

    public static void main(String[] args) {
        String amountStr = "0x0000000000000000000000000000000000000001";
        System.out.println(jin_zhi(amountStr));
//        try {
//            KlayTxsResult result = getAddressTxs(KlayController.SYSTEM_ADDRESS);
//            System.out.println(JSON.toJSONString(result));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static String jin_zhi(String amountStr) {
        String result = "";
        amountStr = amountStr.replace("0x", "");
        amountStr = amountStr.replaceAll("^(0+)", "");
        if (amountStr.length() > 10) {
            String amountStr1 = amountStr.substring(amountStr.length() - 10);
            String amountStr2 = amountStr.substring(0, amountStr.length() - 10);
            Long amount1 = Long.parseLong(amountStr1, 16);
            Long amount2 = Long.parseLong(amountStr2, 16);
            result = BigInteger.valueOf(amount1).add(BigInteger.valueOf(amount2).multiply(BigInteger.valueOf(((Double) Math.pow(16d, 10d)).longValue()))).toString();
        } else {
            Long amount1 = Long.parseLong(amountStr, 16);
            result = BigInteger.valueOf(amount1).toString();
        }
        return result;
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
    public static TransactionReceipt.TransactionReceiptData sendingKLAY(String fromPrivateKey, String
            toAddress, BigInteger value) throws IOException, TransactionException {
        Caver caver = new Caver("");
        SingleKeyring keyring = KeyringFactory.createFromPrivateKey(fromPrivateKey);
        String fromAddress = keyring.toAccount().getAddress();
        //Add to caver wallet.
        caver.wallet.add(keyring);
        //Create a value transfer transaction
        ValueTransfer valueTransfer = caver.transaction.valueTransfer.create(
                TxPropertyBuilder.valueTransfer()
                        .setFrom(keyring.getAddress())
                        .setTo(toAddress)
                        .setValue(value)
                        .setGas(BigInteger.valueOf(50000))
        );
        //Sign to the transaction
        valueTransfer.sign(keyring);
        //Send a transaction to the klaytn blockchain platform (Klaytn)
        Bytes32 result = caver.rpc.klay.sendRawTransaction(valueTransfer.getRawTransaction()).send();
        if (result.hasError()) {
            logger.error("sendingKLAY 失败:" + result.getError().getMessage()
                    + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
            throw new RuntimeException(result.getError().getMessage());
        }
        logger.info("sendingKLAY :" + result.getResult()
                + ",from:" + fromAddress + ",to:" + toAddress + ",val:" + value);
        //Check transaction receipt.
        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
        TransactionReceipt.TransactionReceiptData transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(result.getResult());

        return transactionReceipt;
    }

    @RequestMapping("/klayScan/getAddressTxs")
    public ResponseEntity<?> getAddressTxs(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        return new ResponseEntity(getAddressTxs(map.get("address").toString()));
    }

}

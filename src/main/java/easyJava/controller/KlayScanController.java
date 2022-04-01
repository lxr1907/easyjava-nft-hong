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
import java.util.List;
import java.util.Map;

@RestController
public class KlayScanController {
    private static final Logger logger = LogManager.getLogger(NFTScanController.class);
    @Autowired
    BaseDao baseDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String KLAY_TXS_TABLE = "klay_txs";
    public static final String KLAY_API_PRE = "https://api-baobab-v2.scope.klaytn.com/v2/accounts/";
    public static final String KLAY_CHR_API_TAIL = "/ftBalances";
    public static final String TXS_API = "/txs";
    @Scheduled(cron = "*/50 * * * * ?")
    @RequestMapping("/scanKlayTxs")
    public ResponseEntity<?> scanKlayTxs() {
        //这个方法要在代码里写个定时器， 每隔 5或10秒 扫一次
        KlayTxsResult result = getAddressTxs(KlayController.SYSTEM_ADDRESS);

        List<Map<String, Object>> retList = result.getResult();
        retList.forEach(map -> {
            map.put("tableName", KLAY_TXS_TABLE);

            baseDao.insertIgnoreBase(map);
        });
        return new ResponseEntity();
    }
    @RequestMapping("/klayScan/getAddressTokens")
    public ResponseEntity<?> getAddressTokens(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        return new ResponseEntity(getAddressTokens(map.get("address").toString()));
    }

    public static Object getAddressTokens(String address) {
        String result = HttpUtil.get(KLAY_API_PRE + address + KLAY_CHR_API_TAIL);
        return JSON.parse(result);
    }
    /**
     * 获取privateKey对应的address
     *
     * @return
     */
    public static KlayTxsResult getAddressTxs(String address) {
        String result = HttpUtil.get(KLAY_API_PRE + address + TXS_API);
        KlayTxsResult response = JSON.parseObject(result, KlayTxsResult.class);
        return response;
    }

    public static void main(String[] args) {
//        try {
//            KlayTxsResult result = getAddressTxs(KlayController.SYSTEM_ADDRESS);
//            System.out.println(JSON.toJSONString(result));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

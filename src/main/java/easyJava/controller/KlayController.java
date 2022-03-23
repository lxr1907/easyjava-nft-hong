package easyJava.controller;

import com.klaytn.caver.Caver;
import com.klaytn.caver.account.Account;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
public class KlayController {
    private static final Logger logger = LoggerFactory.getLogger(KlayController.class);
    @Autowired
    BaseDao baseDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String Klay_MANAGE = "address_invite";
    public static final String SYSTEM_PRIVATE = "6b9332b28c2a689f464994cb7be26485aba9a3471077cf8607eefe8f849c10f8";

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
    public TransactionReceipt.TransactionReceiptData sendingKLAY(String fromPrivateKey, String toAddress, BigInteger value) throws IOException, TransactionException {
        Caver caver = new Caver(Caver.DEFAULT_URL);
        SingleKeyring keyring = KeyringFactory.createFromPrivateKey(fromPrivateKey);
        //Add to caver wallet.
        caver.wallet.add(keyring);
        //Create a value transfer transaction
        ValueTransfer valueTransfer = caver.transaction.valueTransfer.create(
                TxPropertyBuilder.valueTransfer()
                        .setFrom(keyring.getAddress())
                        .setTo(toAddress)
                        .setValue(value)
                        .setGas(BigInteger.valueOf(25000))
        );
        //Sign to the transaction
        valueTransfer.sign(keyring);
        //Send a transaction to the klaytn blockchain platform (Klaytn)
        Bytes32 result = caver.rpc.klay.sendRawTransaction(valueTransfer.getRawTransaction()).send();
        if (result.hasError()) {
            throw new RuntimeException(result.getError().getMessage());
        }
        //Check transaction receipt.
        TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);
        TransactionReceipt.TransactionReceiptData transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(result.getResult());

        return transactionReceipt;
    }

    @RequestMapping("/klay/sendKlayTo")
    public ResponseEntity<?> login(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        TransactionReceipt.TransactionReceiptData result = null;
        try {
            result = sendingKLAY(SYSTEM_PRIVATE, map.get("address").toString()
                    , BigInteger.valueOf(Long.parseLong(map.get("value").toString())));
        } catch (Exception e) {
            logger.error("发送sendingKLAY失败！", e);
        }
        return new ResponseEntity(result);
    }

    @RequestMapping("/klay/insertKlay")
    public ResponseEntity insertKlay(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        map.put("tableName", Klay_MANAGE);
        int count = baseDao.insertIgnoreBase(map);
        return new ResponseEntity(count);
    }

    @RequestMapping("/klay/updateKlay")
    public ResponseEntity updateBaseByPrimaryKey(@RequestParam Map<String, Object> map) {
        if (map.get("id") == null || map.get("id").toString().trim().length() == 0) {
            return new ResponseEntity(400, "id不能为空！");
        }
        map.put("tableName", Klay_MANAGE);
        int count = baseDao.updateBaseByPrimaryKey(map);
        return new ResponseEntity(count);
    }

    @RequestMapping("/klay/getKlay")
    public ResponseEntity getKlay(@RequestParam Map<String, Object> map) {
        if (map.get("address") == null || map.get("address").toString().length() == 0) {
            return new ResponseEntity(400, "address不能为空！");
        }
        map.put("tableName", Klay_MANAGE);
        BaseModel baseModel = new BaseModel();
        baseModel.setPageSize(1);
        baseModel.setPageNo(1);
        HashMap retmap = new HashMap();
        List list = baseDao.selectBaseList(map, baseModel);
        retmap.put("list", list);
        return new ResponseEntity(retmap, 1, baseModel);
    }
}

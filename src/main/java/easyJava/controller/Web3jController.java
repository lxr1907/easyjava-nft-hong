package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import easyJava.dao.master.BaseDao;
import easyJava.entity.BaseModel;
import easyJava.entity.ResponseEntity;
import easyJava.entity.TransactionMy;
import easyJava.utils.DateUtils;
import easyJava.utils.HttpsUtils;
import easyJava.utils.MapBeanUtil;
import io.reactivex.functions.Consumer;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ConnectException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Web3jController {
    @Autowired
    BaseDao baseDao;
    public static final String ETH_NODE_URL = "ws://btcpay.lxrtalk.com:8546";
    public static final String TRANSACTION_RECEIPT_TABLE_NAME = "accounts_coin_transaction_receipts";
    public static final String TRANSACTION_TABLE_NAME = "accounts_coin_transactions";
    public static final String ACCOUNT_TABLE_NAME = "accounts_coin_balance";
    public static final String pwd = "123456";
    static WebSocketService ws = new WebSocketService(ETH_NODE_URL, false);

    private void initWsToEthNode() {
        try {
            ws.close();
        } catch (Exception e) {
        }
        try {
            ws = new WebSocketService(ETH_NODE_URL, false);
            subscribeTransactions(ws);
        } catch (Exception e) {
        }
    }

    public Web3jController() {
        try {
            ws.connect();
            subscribeTransactions(ws);
        } catch (ConnectException e) {
            e.printStackTrace();
        }
    }

    private void subscribeTransactions(WebSocketService ws) {
        Web3j web3 = Web3j.build(ws);
        web3.transactionFlowable().subscribe(new TransactionConsumer("finish") {
        });
        web3.pendingTransactionFlowable().subscribe(new TransactionConsumer("pending") {
        });
    }

    public class TransactionConsumer implements Consumer<Transaction> {
        private String pending = "";

        public TransactionConsumer(String pending) {
            this.pending = pending;
        }

        @Override
        public void accept(Transaction transaction) throws Exception {
            try {
                String tStr = JSON.toJSONString(transaction);
                TransactionMy t = JSON.parseObject(tStr, TransactionMy.class);
                t.setTime(DateUtils.getDatePastStr(new Date(), 0));
                t.setPending(pending);
                Map<String, Object> queryMap = new HashMap<>();
                queryMap.put("coin_name", "ETH");
                queryMap.put("recharge_address", transaction.getFrom());
                queryMap.put("tableName", ACCOUNT_TABLE_NAME);
                int outCount = baseDao.selectBaseCount(queryMap);
                if (outCount != 0) {
                    t.setTransferType("2");
                    insertTransaction(t, TRANSACTION_TABLE_NAME);
                    return;
                }
                queryMap.put("recharge_address", transaction.getTo());
                int inCount = baseDao.selectBaseCount(queryMap);
                if (inCount != 0) {
                    t.setTransferType("1");
                    insertTransaction(t, TRANSACTION_TABLE_NAME);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void insertTransaction(TransactionMy transaction, String tableName) {
        Map map = JSON.parseObject(JSON.toJSON(transaction).toString(), Map.class);
        map.put("tableName", tableName);
        System.out.println(JSON.toJSON(map));
        baseDao.insertBase(map);
    }

    @PostMapping("/v1/web3j/createWallet")
    public ResponseEntity createWallet(@RequestParam("uuid") String uuid) throws CipherException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Map map = createAccount(uuid);
        return new ResponseEntity(map);
    }

    /*************创建一个钱包文件**************/
    private static Map createAccount(String uuid) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        Map map = new HashMap();
        String walletFileName = "";//文件名
        String walletFilePath = getWalletFilePath(uuid);
        File dir = new File(walletFilePath);
        dir.setWritable(true, false);
        dir.mkdirs();
        //钱包文件保持路径，请替换位自己的某文件夹路径
        walletFileName = WalletUtils.generateNewWalletFile(pwd, dir, false);
        Credentials credentials = WalletUtils.loadCredentials(pwd, walletFilePath + walletFileName);
        //钱包地址 ：
        String address = credentials.getAddress();
        // 公钥16进制字符串表示：
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        //私钥16进制字符串表示：
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        map.put("uuid", uuid);
        map.put("address", address);
        map.put("publicKey", publicKey);
        map.put("privateKey", privateKey);
        map.put("walletFileName", walletFileName);
        return map;
    }

    private static String getWalletFilePath(String uuid) {
        String walletFilePath = "./eth_wallets/uid_" + uuid + "/";
        return walletFilePath;
    }

    private static String getWalletFilePathName(String uuid) {
        String walletFilePath = "./eth_wallets/uid_" + uuid + "/";
        File dir = new File(walletFilePath);
        dir.setWritable(true, false);
        String[] files = dir.list();
        if (files != null && files.length != 0) {
            return walletFilePath + files[0];
        }
        return null;
    }

    /*************创建一个钱包文件**************/
    private static Map createAccountRemote(String uuid) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String urlString = "https://signal.lxrtalk.com/easyJava/v1/web3j/createWallet?uuid=" + uuid;
        String response = HttpsUtils.Post(urlString, "text/json", null);
        ResponseEntity responseEntity = objectMapper.readValue(response, ResponseEntity.class);
        Map<String, Object> dataMap = responseEntity.getData();
        Map<String, Object> walletMap = (Map<String, Object>) dataMap.get("data");
        return walletMap;
    }

    @PostMapping("/v1/web3j/transfer")
    public ResponseEntity transfer(@RequestParam("uuid") String uuid, @RequestParam("toAddress") String toAddress
            , @RequestParam("balance") double balance) throws Exception {
        Admin web3 = Admin.build(ws);  // defaults to http://localhost:8545/
        Credentials credentials = WalletUtils.loadCredentials(pwd, getWalletFilePathName(uuid));
        TransactionReceipt transactionReceipt = Transfer.sendFunds(
                web3, credentials, toAddress,
                BigDecimal.valueOf(balance), Convert.Unit.ETHER)
                .send();
        transactionReceipt.setLogsBloom("");
        Map map = MapBeanUtil.object2Map(transactionReceipt);
        map.remove("logs");
        map.remove("logsBloom");
        map.put("tableName", TRANSACTION_RECEIPT_TABLE_NAME);
        baseDao.insertBase(map);
        return new ResponseEntity(transactionReceipt);
    }

    @GetMapping("/v1/web3j/transfer/history")
    public ResponseEntity history(@RequestParam("uuid") String uuid, @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize) throws IOException, CipherException {
        BaseModel baseModel = new BaseModel();
        baseModel.setPageNo(pageNo);
        baseModel.setPageSize(pageSize);
        Credentials credentials = WalletUtils.loadCredentials(pwd, getWalletFilePathName(uuid));
        Map map = new HashMap();
        map.put("to", credentials.getAddress());
        map.put("from", credentials.getAddress());
        map.put("tableName", TRANSACTION_TABLE_NAME);
        int count = baseDao.selectBaseCountOr(map);
        List<Map> list = baseDao.selectBaseListOr(map, baseModel);
        return new ResponseEntity(list, count, baseModel.getPageNo(), baseModel.getPageSize());
    }

    @GetMapping("/v1/web3j/balance")
    public ResponseEntity balance(@RequestParam("address") String address) throws Exception {
        Web3j web3 = Web3j.build(ws);  // defaults to http://localhost:8545/
        try {
            EthGetBalance ret = web3.ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();
            return new ResponseEntity(ret);
        } catch (WebsocketNotConnectedException e) {
            initWsToEthNode();
        }
        return new ResponseEntity(400, "查询失败。");
    }


    @GetMapping("/v1/web3j/feeRate")
    public ResponseEntity feeRate() throws Exception {
        Web3j web3 = Web3j.build(ws);
        EthGasPrice ret = web3.ethGasPrice().send();
        return new ResponseEntity(ret);
    }


    @GetMapping("/v1/web3j/getTX")
    public ResponseEntity getTX(@RequestParam("transactionHash") String transactionHash) throws Exception {
        Web3j web3 = Web3j.build(ws);
        EthTransaction ret = web3.ethGetTransactionByHash(transactionHash).send();
        return new ResponseEntity(ret);
    }


    public static void main(String[] args) throws Exception {
//        Map<String, Object> map = createAccountRemote("2");
//        for (Map.Entry<String, Object> e : map.entrySet()) {
//            System.out.println(e.getKey() + ":" + e.getValue());
//        }
        //加载钱包
        Credentials credentials = WalletUtils.loadCredentials(pwd, "D://eth//1.json");
        String form = credentials.getAddress();
        System.out.println("from:" + form);
        String to = "0x4eece1847ad0bd4ad47456c6d8f5952f3affd2e9";
        //初始web3j
        BigInteger gasLimit = Transfer.GAS_LIMIT;
        BigInteger gasPremium = new BigInteger("42000");
        BigInteger feeCap = new BigInteger("64000");
        Admin web3j = Admin.build(ws);  // defaults to http://localhost:8545/
        web3j.ethChainId().setId(4);
        TransactionReceipt transactionReceipt = Transfer.sendFunds(
                web3j, credentials, to,
                BigDecimal.valueOf(0.1), Convert.Unit.ETHER).send();
//        TransactionReceipt transactionReceipt = Transfer.sendFundsEIP1559(
//                web3j, credentials, to,
//                BigDecimal.valueOf(0.1), Convert.Unit.ETHER, gasLimit, gasPremium, feeCap).send();
        transactionReceipt.setLogsBloom("");
        System.out.println(JSON.toJSON(transactionReceipt));
    }

}

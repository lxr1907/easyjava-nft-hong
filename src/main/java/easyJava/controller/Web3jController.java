//package easyJava.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import easyJava.Configs;
//import easyJava.dao.master.BaseDao;
//import easyJava.entity.BaseModel;
//import easyJava.entity.ResponseEntity;
//import easyJava.entity.TransactionMy;
//import easyJava.utils.HttpsUtils;
//import easyJava.utils.MapBeanUtil;
//import io.reactivex.functions.Consumer;
//import org.java_websocket.exceptions.WebsocketNotConnectedException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.web3j.crypto.CipherException;
//import org.web3j.crypto.Credentials;
//import org.web3j.crypto.WalletUtils;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.admin.Admin;
//import org.web3j.protocol.core.DefaultBlockParameter;
//import org.web3j.protocol.core.methods.response.*;
//import org.web3j.protocol.websocket.WebSocketService;
//import org.web3j.tx.Transfer;
//import org.web3j.utils.Convert;
//
//import java.io.File;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.math.RoundingMode;
//import java.net.ConnectException;
//import java.security.InvalidAlgorithmParameterException;
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.util.*;
//
//@RestController
//public class Web3jController {
//    @Autowired
//    BaseDao baseDao;
//    public String ETH_NODE_URL = "ws://" + Configs.getBtcpayHost() + ":8552";
//    public static final String TRANSACTION_RECEIPT_TABLE_NAME = "accounts_coin_transaction_receipts";
//    public static final String TRANSACTION_TABLE_NAME = "accounts_coin_transactions";
//    public static final String ACCOUNT_TABLE_NAME = "accounts_coin_balance";
//    public static final String pwd = "123456";
//    WebSocketService ws = new WebSocketService(ETH_NODE_URL, false);
//    public static final BigInteger GAS_LIMIT = BigInteger.valueOf(21000);
//    public static final BigInteger ETH_WEI = BigInteger.valueOf(1000000000000000000L);
//
//    private void initWsToEthNode() {
//        try {
//            ws.close();
//        } catch (Exception e) {
//        }
//        try {
//            ws = new WebSocketService(ETH_NODE_URL, false);
//            subscribeTransactions(ws);
//        } catch (Exception e) {
//        }
//    }
//
//    public Web3jController() {
//        try {
//            ws.connect();
//            subscribeTransactions(ws);
//        } catch (ConnectException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void subscribeTransactions(WebSocketService ws) {
//        Web3j web3 = Web3j.build(ws);
//        //????????????
////        web3.transactionFlowable().subscribe(new TransactionConsumer("finish") {
////        });
////        web3.pendingTransactionFlowable().subscribe(new TransactionConsumer("pending") {
////        });
//    }
//
//    public class TransactionConsumer implements Consumer<Transaction> {
//        private String pending = "";
//
//        public TransactionConsumer(String pending) {
//            this.pending = pending;
//        }
//
//        private Set<String> addressSet = new HashSet<>();
//
//        private long lastUpdate = new Date().getTime();
//
//        @Override
//        public void accept(Transaction transaction) {
//            try {
//                dealWithPendingTransaction(transaction);
//                String tStr = JSON.toJSONString(transaction);
//                TransactionMy t = JSON.parseObject(tStr, TransactionMy.class);
//                t.setTime(new Date().getTime() + "");
//                t.setPending(pending);
//                if (hasEthAccountInOurDB(transaction.getFrom())) {
//                    insertTransaction(t, TRANSACTION_TABLE_NAME);
//                    return;
//                }
//                if (hasEthAccountInOurDB(transaction.getTo())) {
//                    insertTransaction(t, TRANSACTION_TABLE_NAME);
//                    return;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        private boolean hasEthAccountInOurDB(String address) {
//            if (addressSet.contains(address)) {
//                return true;
//            } else {
//                if (new Date().getTime() - lastUpdate > 10 * 1000) {
//                    updateAddressSet();
//                    return hasEthAccountInOurDB(address);
//                }
//                return false;
//            }
//        }
//
//        private synchronized void updateAddressSet() {
//            if (new Date().getTime() - lastUpdate < 10 * 1000) {
//                return;
//            }
//            Map<String, Object> queryMap = new HashMap<>();
//            queryMap.put("coin_name", "ETH");
//            //queryMap.put("recharge_address", address);
//            queryMap.put("tableName", ACCOUNT_TABLE_NAME);
//            var result = baseDao.selectBaseList(queryMap, new BaseModel().setPageNo(1).setPageSize(100000).setOrderBy("time desc"));
//            result.forEach(map -> {
//                if (map.get("recharge_address") != null) {
//                    String address = map.get("recharge_address").toString();
//                    addressSet.add(address);
//                }
//            });
//            lastUpdate = new Date().getTime();
//        }
//
//    }
//
//
//    private static boolean isValidHexQuantity(String value) {
//        if (value == null) {
//            return false;
//        } else if (value.length() < 3) {
//            return false;
//        } else {
//            return value.startsWith("0x");
//        }
//    }
//
//    private static final String DEFAULT_V = "0x111";
//
//    private void dealWithPendingTransaction(Transaction t) {
//        if (t.getNonceRaw() == null || t.getNonceRaw().length() == 0) {
//            t.setNonce(DEFAULT_V);
//        }
//        if (t.getBlockNumberRaw() == null || t.getBlockNumberRaw().length() == 0) {
//            t.setBlockNumber(DEFAULT_V);
//        }
//        if (t.getTransactionIndexRaw() == null || t.getTransactionIndexRaw().length() == 0) {
//            t.setTransactionIndex(DEFAULT_V);
//        }
//        if (t.getGasPriceRaw() == null || t.getGasPriceRaw().length() == 0) {
//            t.setGasPrice(DEFAULT_V);
//        }
//        if (t.getGasRaw() == null || t.getGasRaw().length() == 0) {
//            t.setGas(DEFAULT_V);
//        }
//        if (t.getValueRaw() == null || t.getValueRaw().length() == 0) {
//            t.setValue(DEFAULT_V);
//        }
//    }
//
//    private void insertTransaction(TransactionMy transaction, String tableName) {
//        Map map = JSON.parseObject(JSON.toJSON(transaction).toString(), Map.class);
//        map.put("tableName", tableName);
//        System.out.println(JSON.toJSON(map));
//        baseDao.insertUpdateBase(map);
//    }
//
//    @GetMapping("/v1/web3j/ethHost")
//    public ResponseEntity ethHost() {
//        return new ResponseEntity(Configs.getBtcpayHost());
//    }
//
//    @PostMapping("/v1/web3j/createWallet")
//    public ResponseEntity createWallet(@RequestParam("uuid") String uuid) throws CipherException, InvalidAlgorithmParameterException,
//            NoSuchAlgorithmException, NoSuchProviderException, IOException {
//        Map map = createAccount(uuid);
//        return new ResponseEntity(map);
//    }
//
//    /*************????????????????????????**************/
//    private static Map createAccount(String uuid) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
//        Map map = new HashMap();
//        String walletFileName = "";//?????????
//        String walletFilePath = getWalletFilePath(uuid);
//        File dir = new File(walletFilePath);
//        dir.setWritable(true, false);
//        dir.mkdirs();
//        //??????????????????????????????????????????????????????????????????
//        if (dir.list().length == 0) {
//            walletFileName = WalletUtils.generateNewWalletFile(pwd, dir, false);
//        }
//        Credentials credentials = WalletUtils.loadCredentials(pwd, getWalletFilePathName(uuid));
//        //???????????? ???
//        String address = credentials.getAddress();
//        // ??????16????????????????????????
//        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
//        //??????16????????????????????????
//        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
//        map.put("uuid", uuid);
//        map.put("address", address);
//        map.put("publickey", publicKey);
//        map.put("privatekey", privateKey);
//        map.put("walletfilename", walletFileName);
//        return map;
//    }
//
//    private static String getWalletFilePath(String uuid) {
//        String walletFilePath = "./eth_wallets/uid_" + uuid + "/";
//        return walletFilePath;
//    }
//
//    private static String getWalletFilePathName(String uuid) {
//        String walletFilePath = "./eth_wallets/uid_" + uuid + "/";
//        File dir = new File(walletFilePath);
//        dir.setWritable(true, false);
//        String[] files = dir.list();
//        if (files != null && files.length != 0) {
//            return walletFilePath + files[0];
//        }
//        return null;
//    }
//
//    /*************????????????????????????**************/
//    private static Map createAccountRemote(String uuid) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String urlString = "https://signal.lxrtalk.com/easyJava/v1/web3j/createWallet?uuid=" + uuid;
//        String response = HttpsUtils.Post(urlString, "text/json", null);
//        ResponseEntity responseEntity = objectMapper.readValue(response, ResponseEntity.class);
//        Map<String, Object> dataMap = responseEntity.getData();
//        Map<String, Object> walletMap = (Map<String, Object>) dataMap.get("data");
//        return walletMap;
//    }
//
//    /**
//     * ???????????????????????????,????????????????????????????????????
//     *
//     * @param uuid
//     * @param toAddress
//     * @param balance
//     * @return
//     * @throws Exception
//     */
//    @PostMapping("/v1/web3j/transferWithoutFee")
//    public ResponseEntity transferWithoutFee(@RequestParam("uuid") String uuid, @RequestParam("toAddress") String toAddress
//            , @RequestParam("balance") double balance) throws Exception {
//        Admin web3 = Admin.build(ws);  // defaults to http://localhost:8545/
//        Credentials credentials = WalletUtils.loadCredentials(pwd, getWalletFilePathName(uuid));
//        TransactionReceipt transactionReceipt = Transfer.sendFunds(
//                web3, credentials, toAddress,
//                BigDecimal.valueOf(balance), Convert.Unit.ETHER)
//                .send();
//        transactionReceipt.setLogsBloom("");
//        Map map = MapBeanUtil.object2Map(transactionReceipt);
//        map.remove("logs");
//        map.remove("logsBloom");
//        map.put("tableName", TRANSACTION_RECEIPT_TABLE_NAME);
//        baseDao.insertBase(map);
//        return new ResponseEntity(transactionReceipt);
//    }
//
//    /**
//     * ?????????????????????????????????????????????????????????
//     *
//     * @param uuid
//     * @param toAddress
//     * @param balance
//     * @return
//     * @throws Exception
//     */
//    @PostMapping("/v1/web3j/transfer")
//    public ResponseEntity transfer(@RequestParam("uuid") String uuid, @RequestParam("toAddress") String toAddress
//            , @RequestParam("balance") double balance) throws Exception {
//        Admin web3 = Admin.build(ws);  // defaults to http://localhost:8545/
//        EthGasPrice ethGasPrice = web3.ethGasPrice().send();
//        BigDecimal b = new BigDecimal(balance).multiply(new BigDecimal(ETH_WEI));
//        BigDecimal balanceWithoutFee = b.subtract(new BigDecimal(ethGasPrice.getGasPrice().multiply(GAS_LIMIT)))
//                .divide(new BigDecimal(ETH_WEI))
//                .setScale(15, RoundingMode.DOWN);
//        Credentials credentials = WalletUtils.loadCredentials(pwd, getWalletFilePathName(uuid));
//        TransactionReceipt transactionReceipt = Transfer.sendFunds(
//                web3, credentials, toAddress,
//                balanceWithoutFee, Convert.Unit.ETHER)
//                .send();
//        transactionReceipt.setLogsBloom("");
//        Map map = MapBeanUtil.object2Map(transactionReceipt);
//        map.remove("logs");
//        map.remove("logsBloom");
//        map.put("tableName", TRANSACTION_RECEIPT_TABLE_NAME);
//        baseDao.insertBase(map);
//        return new ResponseEntity(transactionReceipt);
//    }
//
//    @GetMapping("/v1/web3j/transfer/history")
//    public ResponseEntity history(@RequestParam("uuid") String uuid, @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize) throws IOException, CipherException {
//        BaseModel baseModel = new BaseModel();
//        baseModel.setPageNo(pageNo);
//        baseModel.setPageSize(pageSize);
//        Credentials credentials = WalletUtils.loadCredentials(pwd, getWalletFilePathName(uuid));
//        Map map = new HashMap();
//        map.put("to", credentials.getAddress());
//        map.put("from", credentials.getAddress());
//        map.put("tableName", TRANSACTION_TABLE_NAME);
//        baseModel.setOrderBy("time desc");
//        int count = baseDao.selectBaseCountOr(map);
//        List<Map> list = baseDao.selectBaseListOr(map, baseModel);
//        return new ResponseEntity(list, count, baseModel.getPageNo(), baseModel.getPageSize());
//    }
//
//    @GetMapping("/v1/web3j/balance")
//    public ResponseEntity balance(@RequestParam("address") String address) throws Exception {
//        Web3j web3 = Web3j.build(ws);  // defaults to http://localhost:8545/
//        try {
//            EthGetBalance ret = web3.ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();
//            return new ResponseEntity(ret);
//        } catch (WebsocketNotConnectedException e) {
//            initWsToEthNode();
//        }
//        return new ResponseEntity(400, "???????????????");
//    }
//
//
//    @GetMapping("/v1/web3j/gasPrice")
//    public ResponseEntity gasPrice() throws Exception {
//        Web3j web3 = Web3j.build(ws);
//        EthGasPrice ret = web3.ethGasPrice().send();
//        return new ResponseEntity(ret);
//    }
//
//
//    @GetMapping("/v1/web3j/getTX")
//    public ResponseEntity getTX(@RequestParam("transactionHash") String transactionHash) throws Exception {
//        Web3j web3 = Web3j.build(ws);
//        EthTransaction ret = web3.ethGetTransactionByHash(transactionHash).send();
//        return new ResponseEntity(ret);
//    }
//
//    @GetMapping("/v1/web3j/estimate")
//    public ResponseEntity estimate(@RequestParam("uuid") String uuid, @RequestParam("toAddress") String toAddress
//            , @RequestParam("balance") double balance) {
//        Web3j web3 = Web3j.build(ws);
//        try {
//            BigDecimal v = BigDecimal.valueOf(balance).multiply(new BigDecimal("1000000000000000000"));
//
//            EthGasPrice ethGasPrice = web3.ethGasPrice().send();
//            Credentials credentials = WalletUtils.loadCredentials(pwd, getWalletFilePathName(uuid));
//            EthEstimateGas gas = web3.ethEstimateGas(new org.web3j.protocol.core.methods.request.Transaction(credentials.getAddress(), BigInteger.valueOf(1),
//                    ethGasPrice.getGasPrice(), GAS_LIMIT, toAddress, v.toBigInteger(), "")).send();
//            return new ResponseEntity(gas);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (CipherException e) {
//            e.printStackTrace();
//        }
//        return new ResponseEntity();
//    }
//
//    public static void main(String[] args) throws Exception {
//    }
//
//}

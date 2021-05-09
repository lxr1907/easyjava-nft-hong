package easyJava.controller;

import easyJava.dao.master.BaseDao;
import easyJava.entity.ResponseEntity;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class BitcoinJController {
    @Autowired
    BaseDao baseDao;

    public static void main(String[] args) {
        try {
            String base58Address = "";
            NetworkParameters params;
            String filePrefix;
            params = MainNetParams.get();
            filePrefix = "forwarding-service";
            //LegacyAddress SegwitAddress
            var forwardingAddress = LegacyAddress.fromBase58(params, base58Address);
            Wallet wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);
            MemoryBlockStore blockStore = new MemoryBlockStore(params);
            BlockChain chain = new BlockChain(params, wallet, blockStore);
            PeerGroup peerGroup = new PeerGroup(params, chain);
            peerGroup.addWallet(wallet);
            peerGroup.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/v1/web3j/createWallet")
    public ResponseEntity createWallet(@RequestParam("uuid") String uuid) {
//        Map map = createAccount(uuid);
        return new ResponseEntity();
    }

    @PostMapping("/v1/web3j/transferWithoutFee")
    public ResponseEntity transferWithoutFee(@RequestParam("uuid") String uuid, @RequestParam("toAddress") String toAddress
            , @RequestParam("balance") double balance) throws Exception {
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
        return new ResponseEntity();
    }

    /**
     * //     * 转账金额包含手续费，因此到账会少一部分
     * //     *
     * //     * @param uuid
     * //     * @param toAddress
     * //     * @param balance
     * //     * @return
     * //     * @throws Exception
     * //
     */
    @PostMapping("/v1/web3j/transfer")
    public ResponseEntity transfer(@RequestParam("uuid") String uuid, @RequestParam("toAddress") String toAddress
            , @RequestParam("balance") double balance) throws Exception {
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
        return new ResponseEntity();
    }

    @GetMapping("/v1/web3j/transfer/history")
    public ResponseEntity history(@RequestParam("uuid") String uuid, @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize) {
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
        //return new ResponseEntity(list, count, baseModel.getPageNo(), baseModel.getPageSize());
        return new ResponseEntity();
    }

    @GetMapping("/v1/web3j/balance")
    public ResponseEntity balance(@RequestParam("address") String address) throws Exception {
//        Web3j web3 = Web3j.build(ws);  // defaults to http://localhost:8545/
//        try {
//            EthGetBalance ret = web3.ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();
//            return new ResponseEntity(ret);
//        } catch (WebsocketNotConnectedException e) {
//            initWsToEthNode();
//        }
//        return new ResponseEntity(400, "查询失败。");
        return new ResponseEntity();
    }


    @GetMapping("/v1/web3j/gasPrice")
    public ResponseEntity gasPrice() throws Exception {
//        Web3j web3 = Web3j.build(ws);
//        EthGasPrice ret = web3.ethGasPrice().send();
//        return new ResponseEntity(ret);
        return new ResponseEntity();
    }


    @GetMapping("/v1/web3j/getTX")
    public ResponseEntity getTX(@RequestParam("transactionHash") String transactionHash) {
//        Web3j web3 = Web3j.build(ws);
//        EthTransaction ret = web3.ethGetTransactionByHash(transactionHash).send();
//        return new ResponseEntity(ret);
        return new ResponseEntity();
    }

    @GetMapping("/v1/web3j/estimate")
    public ResponseEntity estimate(@RequestParam("uuid") String uuid, @RequestParam("toAddress") String toAddress
            , @RequestParam("balance") double balance) {
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
        return new ResponseEntity();
    }

}

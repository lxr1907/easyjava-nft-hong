package easyJava.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import easyJava.entity.ResponseEntity;
import easyJava.utils.HttpsUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Web3jController {
    public static final String ETH_NODE_URL = "ws://btcpay.lxrtalk.com:8546";
    public static final String pwd = "123456";
    static WebSocketService ws = new WebSocketService(ETH_NODE_URL, false);

    static {
        try {
            ws.connect();
        } catch (ConnectException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
//        Map<String, Object> map = createAccountRemote("2");
//        for (Map.Entry<String, Object> e : map.entrySet()) {
//            System.out.println(e.getKey() + ":" + e.getValue());
//        }

        Web3j web3 = Web3j.build(ws);  // defaults to http://localhost:8545/
        EthGetBalance ret = web3.ethGetBalance("0x9e772e6c0918ac3995863027c0c7c880ec8abc95", DefaultBlockParameter.valueOf("latest")).send();
        System.out.println(JSON.toJSON(ret));
    }


    @PostMapping("/v1/web3j/transfer")
    public ResponseEntity transfer(@RequestParam("uuid") String uuid, @RequestParam("toAddress") String toAddress
            , @RequestParam("balance") double balance) throws Exception {
        Web3j web3 = Web3j.build(ws);  // defaults to http://localhost:8545/
        Credentials credentials = WalletUtils.loadCredentials(pwd, getWalletFilePathName(uuid));
        TransactionReceipt transactionReceipt = Transfer.sendFunds(
                web3, credentials, toAddress,
                BigDecimal.valueOf(balance), Convert.Unit.ETHER)
                .send();
        return new ResponseEntity(transactionReceipt);
    }

    @GetMapping("/v1/web3j/balance")
    public ResponseEntity balance(@RequestParam("address") String address) throws Exception {
        Web3j web3 = Web3j.build(ws);  // defaults to http://localhost:8545/
        EthGetBalance ret = web3.ethGetBalance(address, DefaultBlockParameter.valueOf("latest")).send();
        return new ResponseEntity(ret);
    }


    @GetMapping("/v1/web3j/feeRate")
    public ResponseEntity feeRate() throws Exception {
        Web3j web3 = Web3j.build(ws);
        EthGasPrice ret = web3.ethGasPrice().send();
        return new ResponseEntity(ret);
    }

}

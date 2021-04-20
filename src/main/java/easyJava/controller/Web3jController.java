package easyJava.controller;

import easyJava.entity.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Web3jController {
    @PostMapping("/v1/web3j/createWallet")
    public ResponseEntity createWallet(@RequestParam("uuid") String uuid) throws CipherException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Integer.parseInt(uuid);
        Map map = createAccount(uuid);
        return new ResponseEntity(map);
    }

    /*************创建一个钱包文件**************/
    private static Map createAccount(String uuid) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
        Map map = new HashMap();
        String walletFileName = "";//文件名
        String walletFilePath = "./eth_wallets/uid_" + uuid + "/";
        //String walletFilePath = "D:/eth/uid_" + uuid + "/";
        File dir = new File(walletFilePath);
        dir.setWritable(true, false);
        dir.mkdirs();
        String pwd = "123456";
        //钱包文件保持路径，请替换位自己的某文件夹路径
        walletFileName = WalletUtils.generateNewWalletFile(pwd, new File(walletFilePath), false);
        Credentials credentials = WalletUtils.loadCredentials(pwd, walletFilePath + walletFileName);
        //钱包地址 ：
        String address = credentials.getAddress();
        // 公钥16进制字符串表示：
        String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
        //私钥16进制字符串表示：
        String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        System.out.println("createAccount uuid:" + uuid);
        System.out.println("createAccount address:" + address);
        System.out.println("createAccount publicKey:" + publicKey);
        System.out.println("createAccount privateKey:" + privateKey);
        System.out.println("createAccount walletFileName:" + walletFileName);
        map.put("uuid", uuid);
        map.put("address", address);
        map.put("publicKey", publicKey);
        map.put("privateKey", privateKey);
        map.put("walletFileName", walletFileName);
        return map;
    }

    public static void main(String[] args) throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        createAccount("1");
    }
}

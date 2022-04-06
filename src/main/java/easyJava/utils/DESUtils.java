package easyJava.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * DES加密介绍
 * DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
 * 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
 * 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现 。
 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
 */
public class DESUtils {
    public static String pwd =
            "9588028820109132570743325311898426347857298773549" +
                    "4687588750185795377577721630844788736994473060344662006164" +
                    "11960574122434059469100235892702736860872901247123456";

    //测试
//    public static void main(String args[]) {
//        //待加密内容
//        String str = "测试内容";
//        //密码，长度要是8的倍数
//        String password = "9588028820109132570743325311898426347857298773549468758875018579537757772163084478873699447306034466200616411960574122434059469100235892702736860872901247123456";
//        byte[] result = DESUtils.encrypt(str.getBytes());
//        System.out.println("加密后：" + new String(result));
//        //直接将如上内容解密
//        try {
//            byte[] decryResult = DESUtils.decrypt(result);
//            System.out.println("解密后：" + new String(decryResult));
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//    }

    /**
     * 加密     * @param datasource byte[]     * @param password String
     * * @return byte[]
     */
    public static byte[] encrypt(byte[] datasource) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(pwd.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            // 正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密     * @param src byte[]
     * * @param password String     * @return byte[]
     * * @throws Exception
     */
    public static byte[] decrypt(byte[] src) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(pwd.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }

    /**
     * 异或加密
     * @param str
     * @param numSalt
     * @return
     */
    public static String encrypt(String str, Integer numSalt) {

        //讲获取的字符串转成字符数组
        char[] c = str.toCharArray();
        //使用for循环给字符数组加密
        for (int i = 0; i < c.length; i++) {
            c[i] = (char) (c[i] ^ numSalt);
        }
        return new String(c);
    }

    /**
     * js版本
     * @param args
     */
//    var a="丐乘乆乄丕丕乂专乃丒丕丐久乄乂且乄久乄专丙久久丑丒丐久丙丐乁丗丑乃乂乄丑丕久乁丗丘乆久丘丐丒丘丙丙丕丒乆且乂丒乆乁丑世丕丙丒乃丒乄久"
//    var key=20000
//    var str="";
//for(var i=0;i<a.length;i++){
//        str=str+String.fromCharCode(a.charCodeAt(i)^key)
//    }
//console.log(str);

    public static void main(String[] args) {
        String c = encrypt("丐乘乆乄丕丕乂专乃丒丕丐久乄乂且乄久乄专丙久久丑丒丐久丙丐乁丗丑乃乂乄丑丕久乁丗丘乆久丘丐丒丘丙丙丕丒乆且乂丒乆乁丑世丕丙丒乃丒乄久", 20000);
        System.out.println(c);
    }

}
package easyJava.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;

public class TokenProccessor {
	private TokenProccessor() {
	};

	private static final TokenProccessor instance = new TokenProccessor();

	public static TokenProccessor getInstance() {
		return instance;
	}

	/**
	 * 生成Token
	 * 
	 * @return
	 */
	public static String makeToken() {
		String token = (System.currentTimeMillis() + new Random().nextInt(999999999)) + "";
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			byte md5[] = md.digest(token.getBytes());
			Encoder encoder = Base64.getEncoder();
			return encoder.encodeToString(md5);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

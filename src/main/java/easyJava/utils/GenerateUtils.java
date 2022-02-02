package easyJava.utils;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

@Component
public class GenerateUtils {
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	public String getUniqueId(String tableName) {
		String id = "";
		RedisAtomicLong entityIdCounter = new RedisAtomicLong(tableName, redisTemplate.getConnectionFactory());
		long increment = entityIdCounter.getAndIncrement();
		id = increment + "" + new Date().getTime();
		return id;
	}

	/**
	 * java生成随机数字10位数
	 *
	 * @param
	 * @return
	 */
	public static String getRandomNickname(int length) {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			val += String.valueOf(random.nextInt(10));
		}
		return val;
	}

	public static void main(String[] args) {
		System.out.println("java生成随机数字10位数：" + getRandomNickname(10));
	}
}

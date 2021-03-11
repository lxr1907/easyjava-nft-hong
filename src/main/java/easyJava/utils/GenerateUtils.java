package easyJava.utils;

import java.util.Date;

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
}

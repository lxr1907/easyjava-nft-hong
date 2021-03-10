package easyJava.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import easyJava.dao.master.HelloDao;
import easyJava.entity.ResponseEntity;

@RestController
public class HelloController {
	@Autowired
	private HelloDao helloDao;
	@Autowired
	private easyJava.dao.second.Hello2Dao helloDao2;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@RequestMapping({"/hello","/hell"})
	@Cacheable(value = "hello", key = "1")
	public ResponseEntity<?> hello() {
		var ret = helloDao.getHello();
		return new ResponseEntity(ret);
	}

	@RequestMapping("/hello2")
	@Cacheable(value = "hello2", key = "2")
	public ResponseEntity<?> hello2() {
		var ret2 = helloDao2.getHello().get(0).getHello();
		return new ResponseEntity(ret2);
	}
	@RequestMapping("/helloRedis")
	public ResponseEntity<?> helloRedis() {
		var key = "helloRedis";
		ResponseEntity<?> ret = (ResponseEntity<?>) redisTemplate.opsForValue().get(key);
		if (ret == null) {
			var str = helloDao.getHello().get(0).getHello();
			ret = new ResponseEntity(str);
			redisTemplate.opsForValue().set(key, ret, 10, TimeUnit.DAYS);
		}
		return ret;
	}
}

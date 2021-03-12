package easyJava.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import easyJava.entity.ResponseEntity;
import easyJava.job.HuobiDataSync;
import easyJava.job.HuobiDataSync.HuobiKlineEntity;

@RestController
public class KlineController {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@RequestMapping("/getKline")
	public ResponseEntity login(@RequestParam Map<String, Object> map) {
		if (map.get("symbol") == null || map.get("symbol").toString().length() == 0) {
			return new ResponseEntity(400, "symbol不能为空！");
		}

		HuobiKlineEntity entity = (HuobiKlineEntity) redisTemplate.opsForHash().get(HuobiDataSync.MARKET_KLINE,
				map.get("symbol"));
		return new ResponseEntity(entity);
	}

	public Map getUserByToken(String token) {
		return (Map) redisTemplate.opsForValue().get(token);
	}

	public boolean checkToken(String token) {
		return redisTemplate.hasKey(token);
	}
}

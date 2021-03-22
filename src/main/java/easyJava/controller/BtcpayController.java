package easyJava.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import easyJava.entity.ResponseEntity;
import easyJava.utils.HttpsUtils;

@RestController
public class BtcpayController {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public static final String BTCPAY_URL="https://btcpay.lxrtalk.com";
	public static final String GET_STORE="/api/v1/stores";
	public static final String TOKEN="Authorization";
	public static final String TOKEN_VALUE="Basic UUVJTEd5VWRsN3ZvaVpWTUJvbkUwMXZSR2l5V2w3TFFVMFdRN1o3UGRaRQ==";

	@RequestMapping("/btcpay/api/v1/stores")
	public ResponseEntity<?> stores() {
		Map<String, String> headers=new HashMap<String, String>();
		headers.put(TOKEN, TOKEN_VALUE);
		var ret = HttpsUtils.Get(BTCPAY_URL+GET_STORE, headers);
		return new ResponseEntity(ret);
	}

}
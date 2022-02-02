//package easyJava.job;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import com.alibaba.fastjson.JSON;
//
//import easyJava.entity.HuobiKlineEntity;
//import easyJava.entity.ResponseEntity;
//import easyJava.utils.HttpsUtils;
//
///**
// * https://huobiapi.github.io/docs/spot/v1/cn/#c1ae0a8486
// *
// * @author lxr
// *
// */
//@Component
//public class HuobiDataSync {
//
//	ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
//
//	@Autowired
//	private RedisTemplate<String, Object> redisTemplate;
//
//	public static final String HUOBI_API_URL_PRE = "https://api-aws.huobi.pro";
//	public static final String HUOBI_API_URL_PRE_BACKUP = "https://api.huobi.pro";
//	public static final String MARKET_KLINE = "/market/history/kline";
//
//	@PostConstruct
//	public void init() {
//
//		Set<String> symbolSet = new HashSet<String>();
//		symbolSet.add("btcusdt");
//		symbolSet.add("ethusdt");
//		symbolSet.add("dashusdt");
//		symbolSet.add("ltcusdt");
////1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1mon, 1week, 1year
//		Set<String> periodSet = new HashSet<String>();
//		periodSet.add("1min");
//		periodSet.add("5min");
//		periodSet.add("15min");
//		periodSet.add("30min");
//		periodSet.add("60min");
//		periodSet.add("4hour");
//		periodSet.add("1day");
//		periodSet.add("1mon");
//		periodSet.add("1week");
//		periodSet.add("1year");
//
//		singleThreadPool.submit(new Runnable() {
//
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						for (String symbol : symbolSet) {
//							for (String period : periodSet) {
//								Map<String, String> headers = new HashMap<String, String>();
//								Map<String, String> params = new HashMap<String, String>();
//								params.put("symbol", symbol);
//								params.put("period", period);
//								params.put("size", "150");
//								String result = HttpsUtils.Get(HUOBI_API_URL_PRE + MARKET_KLINE, headers, params);
//								if (result != null) {
//									HuobiKlineEntity entity = JSON.parseObject(result, HuobiKlineEntity.class);
//									redisTemplate.opsForHash().put(MARKET_KLINE, symbol+"_"+period, new ResponseEntity(entity));
//								}
//								try {
//									Thread.sleep(2 * 1000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							}
//						}
//					} catch (Exception e) {
//						System.out.println(e.getMessage());
//					}
//
//				}
//			}
//		});
//	}
//
//	public static void main(String[] args) {
//		/// https://api-aws.huobi.pro/market/history/kline?symbol=btcusdt&period=1day&size=1
//		Map<String, String> headers = new HashMap<String, String>();
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("symbol", "btcusdt");
//		params.put("period", "1day");
//		params.put("size", "1");
//		String result = HttpsUtils.Get(HUOBI_API_URL_PRE + MARKET_KLINE, headers, params);
//		System.out.println(result);
//	}
//
//}

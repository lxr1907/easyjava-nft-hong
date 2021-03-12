package easyJava.job;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import easyJava.utils.HttpUtil;
import easyJava.utils.HttpsUtils;

@Component
public class HuobiDataSync {

	ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public static final String HUOBI_API_URL_PRE = "https://api-aws.huobi.pro";
	public static final String HUOBI_API_URL_PRE_BACKUP = "https://api.huobi.pro";
	public static final String MARKET_KLINE = "/market/history/kline";

	@PostConstruct
	public void init() {

		Set<String> symbolSet = new HashSet<String>();
		symbolSet.add("btcusdt");
		symbolSet.add("ethusdt");

		singleThreadPool.submit(new Runnable() {

			@Override
			public void run() {
				while (true) {
					for (String symbol : symbolSet) {
						Map<String, String> headers = new HashMap<String, String>();
						Map<String, String> params = new HashMap<String, String>();
						params.put("symbol", symbol);
						params.put("period", "1day");
						params.put("size", "1");
						String result = HttpsUtils.Get(HUOBI_API_URL_PRE + MARKET_KLINE, headers, params);
						if (result != null) {
							HuobiKlineEntity entity = JSON.parseObject(result, HuobiKlineEntity.class);
							redisTemplate.opsForHash().put(MARKET_KLINE, symbol, entity);
						}
					}
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void main(String[] args) {
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("symbol", "btcusdt");
		params.put("period", "1day");
		params.put("size", "1");
		String result = HttpsUtils.Get(HUOBI_API_URL_PRE + MARKET_KLINE, headers, params);
		System.out.println(result);
	}

	public HuobiDataSync() {
	}

	public class HuobiKlineEntity {
		private String ch;
		private String status;
		private String ts;
		private List<OneKlineEntity> data;

		public String getCh() {
			return ch;
		}

		public void setCh(String ch) {
			this.ch = ch;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getTs() {
			return ts;
		}

		public void setTs(String ts) {
			this.ts = ts;
		}

		public List<OneKlineEntity> getData() {
			return data;
		}

		public void setData(List<OneKlineEntity> data) {
			this.data = data;
		}
	}

	public class OneKlineEntity {
		private String id;
		private String open;
		private String close;
		private String low;
		private String high;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOpen() {
			return open;
		}

		public void setOpen(String open) {
			this.open = open;
		}

		public String getClose() {
			return close;
		}

		public void setClose(String close) {
			this.close = close;
		}

		public String getLow() {
			return low;
		}

		public void setLow(String low) {
			this.low = low;
		}

		public String getHigh() {
			return high;
		}

		public void setHigh(String high) {
			this.high = high;
		}
	}
}

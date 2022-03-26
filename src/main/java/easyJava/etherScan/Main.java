package easyJava.etherScan;

import org.apache.http.HttpHost;

public class Main {

	public static void main(String[] args) throws Exception {
//
//		//如果部署在国外不需要用代理
		HttpHost proxy = new HttpHost("localhost", 7890);
		HttpClientUtil.init(10, 2, proxy);
		
		
		ScanServiceImpl scanServiceImpl = new ScanServiceImpl();

		String contractAddress = "0x04150f928c036810be42ee2e42c5a3b85561c3f7";
		String apiKey = "NZMHHDYZ8N2I46ZA85DMIQGWBST7227KJA";
		String url = "https://api.etherscan.io/api";
		
		
		//这个方法要在代码里写个定时器， 每隔 5或10秒 扫一次
		scanServiceImpl.doScan();
		
		
	}

}

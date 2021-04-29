package easyJava;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configs {
    private static String btcpayHost = "192.168.134.45";

    public static String getBtcpayHost() {
        return btcpayHost;
    }

    @Value("${btcpay.host}")
    public void setBtcpayHost(String btcpayHost) {
        this.btcpayHost = btcpayHost;
    }

}
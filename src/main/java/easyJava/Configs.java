package easyJava;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configs {
    private static String btcpayHost = "";

    public String getBtcpayHost() {
        return btcpayHost;
    }

    @Value("${btcpay.host}")
    public void setBtcpayHost(String btcpayHost) {
        this.btcpayHost = btcpayHost;
    }

}
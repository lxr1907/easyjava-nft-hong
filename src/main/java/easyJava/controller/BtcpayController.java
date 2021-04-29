package easyJava.controller;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.server.PathParam;

import easyJava.Configs;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import easyJava.entity.ResponseEntity;
import easyJava.utils.HttpsUtils;

@RestController
public class BtcpayController {

    public String BTCPAY_URL = "https://" + Configs.getBtcpayHost();
    public static final String GET_STORE = "/api/v1/stores";
    public static final String TOKEN = "Authorization";
    public static final String TOKEN_VALUE = "token a56fb37c559c6c0d45b31e79ebf11100792f56ea";

    @RequestMapping("/btcpay/api/v1/stores")
    public ResponseEntity<?> stores() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(TOKEN, TOKEN_VALUE);
        var ret = HttpsUtils.Get(BTCPAY_URL + GET_STORE, headers);
        return new ResponseEntity(ret);
    }

    @RequestMapping("/btcpay/api/v1/stores/{storeId}/invoices")
    public ResponseEntity<?> createInvoice(@PathParam(value = "storeId") String storeId) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(TOKEN, TOKEN_VALUE);
        var ret = HttpsUtils.Get(BTCPAY_URL + "/api/v1/stores/" + storeId + "/invoices", headers);
        return new ResponseEntity(ret);
    }

}
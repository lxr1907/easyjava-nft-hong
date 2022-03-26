package easyJava.etherScan;

import java.math.BigInteger;
import java.util.*;

import easyJava.controller.KlayController;
import easyJava.utils.DateUtils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class ScanServiceImpl implements ScanService {
    private static final Logger logger = LoggerFactory.getLogger(ScanServiceImpl.class);

    private static String contractAddress;
    private static String myAddress;
    private static String apiKey;
    private static String url;
    private static volatile Long currentBlock = 0L;
    public static String url_main = "https://api.etherscan.io/api";
    public static String url_ropsten = "https://api-ropsten.etherscan.io/api";
    public static String url_rinkeby = "https://api-rinkeby.etherscan.io/api";

    static {

        myAddress = KlayController.USDT_ADDRESS_ERC20_ROPSTEN;
        apiKey = "8UBE25IH7EXCS97K2ZSZJGQK2MR19PN8S3";
        //测试链
        url = url_ropsten;
        contractAddress = myAddress;
    }


    private EventList scanEventOnePage(Long startBlock, Long endBlock) {

        Map<String, String> map = new HashMap<String, String>();

        map.put("module", "logs");
        map.put("action", "getLogs");
        map.put("address", contractAddress);
        map.put("fromBlock", String.valueOf(startBlock));
        map.put("toBlock", String.valueOf(endBlock));
        map.put("apikey", apiKey);
        map.put("topic0", "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef");

        String responseStr = HttpClientUtil.httpGet(url, map, null);
        EventList response = JSON.parseObject(responseStr, EventList.class);

        return response;
    }

    private EventList scanAddressTransactions() {

        Map<String, String> map = new HashMap<String, String>();

        map.put("module", "account");
        map.put("action", "txlist");
        map.put("address", myAddress);
        map.put("fromBlock", "0");
        map.put("page", "1");
        map.put("sort", "desc");
        map.put("toBlock", "latest");
        map.put("apikey", apiKey);

        String responseStr = HttpClientUtil.httpGet(url, map, null);
        EventList response = JSON.parseObject(responseStr, EventList.class);

        return response;
    }


    private Map updateInfo(Map<String, Object> item) {

//        List<String> topics = (List<String>) item.get("topics");
//        Long tokenId = EthUtil.hexToBigInt(topics.get(3)).longValue();

        Long blockNum = EthUtil.hexToBigInt(item.get("blockNumber").toString()).longValue();
        long time = EthUtil.hexToBigInt(item.get("timeStamp").toString()).longValue() * 1000;

        Map map = new HashMap();
        map.put("block_num", blockNum);
        map.put("timestamp", time);
        map.put("value", item.get("value"));
        map.put("from", item.get("from"));
        map.put("to", item.get("to"));
        if (item.get("input") != null) {
            String input = item.get("input").toString();
            if (input.length() > 100) {
                input = input.substring(0, 100);
            }
            map.put("input", input);
        }
        map.put("hash", item.get("hash"));
        return map;
    }

    public List<Map> doScan() {
        List<Map> ret = new ArrayList<>();
        EventList eventList = null;

        System.out.println(String.format("doScan startBlock:%d", currentBlock));

        eventList = scanEventOnePage(currentBlock, 99999999L);

        if (eventList != null && eventList.getResult() != null && eventList.getResult().size() > 0) {

            for (Map<String, Object> item : eventList.getResult()) {

                logger.info(JSON.toJSONString(item));
                ret.add(updateInfo(item));

            }

            Map<String, Object> map = eventList.getResult().get(eventList.getResult().size() - 1);

            BigInteger blockNum = new BigInteger(map.get("blockNumber").toString().substring(2), 16);

            if (blockNum.longValue() > currentBlock) {
                currentBlock = blockNum.longValue();
                //保存 当前区块 到 数据库 或 redis  或文件
            }

        }
        return ret;
    }

    public List<Map> doScanAddress() {
        List<Map> ret = new ArrayList<>();
        EventList eventList = null;

        eventList = scanAddressTransactions();

        if (eventList != null && eventList.getResult() != null && eventList.getResult().size() > 0) {

            for (Map<String, Object> item : eventList.getResult()) {

                logger.info(JSON.toJSONString(item));
                ret.add(updateInfo(item));

            }

            Map<String, Object> map = eventList.getResult().get(eventList.getResult().size() - 1);

            BigInteger blockNum = new BigInteger(map.get("blockNumber").toString().substring(2), 16);

            if (blockNum.longValue() > currentBlock) {
                currentBlock = blockNum.longValue();
                //保存 当前区块 到 数据库 或 redis  或文件
            }

        }
        return ret;
    }
}

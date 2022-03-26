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
    private static String apiKey;
    private static String url;
    private static volatile Long currentBlock = 0L;
    public static String url_main = "https://api.etherscan.io/api";
    public static String url_ropsten = "https://api-ropsten.etherscan.io/api";
    public static String url_rinkeby = "https://api-rinkeby.etherscan.io/api";

    static {

        String contractAddress = KlayController.USDT_ADDRESS_ERC20_ROPSTEN;
        String apiKey = "8UBE25IH7EXCS97K2ZSZJGQK2MR19PN8S3";
        String url = "https://api.etherscan.io/api";

        init(contractAddress, apiKey, url);
    }

    public static void init(String a, String b, String c) {
        contractAddress = a;
        apiKey = b;
        url = url_ropsten;
        //实际上 当前区块 从数据库 或 redis  或文件 读取
        currentBlock = 0L;

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


    private Map updateInfo(Map<String, Object> item) {

        List<String> topics = (List<String>) item.get("topics");
        String fromAddress = topics.get(1).replace("0x000000000000000000000000", "0x");
        String toAddress = topics.get(2).replace("0x000000000000000000000000", "0x");
        Long tokenId = EthUtil.hexToBigInt(topics.get(3)).longValue();

        Long blockNum = EthUtil.hexToBigInt(item.get("blockNumber").toString()).longValue();
        String transactionHash = item.get("transactionHash").toString().toLowerCase();
        long time = EthUtil.hexToBigInt(item.get("timeStamp").toString()).longValue() * 1000;
        Date date = new Date(time);

        Map map = new HashMap();
        map.put("block_num", blockNum);
        map.put("to_address", toAddress);
        map.put("token_id", tokenId);
        map.put("timestamp", time);
//        System.out.println(String.format("%d %s %s %s %d %s", blockNum, transactionHash, fromAddress, toAddress, tokenId, DateUtils.getDateTimeString(date)));

        //这里的逻辑是 根据tokenId查询数据库， 如果记录不存在 插入，如果存在 判断  toAddress是否是当前token 持有人，如果不是， 更新
        return map;
    }

    public List<Map> doScan() {
        List<Map> ret = new ArrayList<>();
        EventList eventList = null;

        System.out.println(String.format("startBlock:%d", currentBlock));

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

}

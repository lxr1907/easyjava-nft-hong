package easyJava.controller;

import easyJava.entity.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class SwapTestController {
    private static final Logger logger = LoggerFactory.getLogger(SwapTestController.class);
    public static AtomicLong chrBalance = new AtomicLong(10000);
    public static AtomicLong gameCoinBalance = new AtomicLong(10000);
    public static final long chrGameCoinK;

    static {
        chrGameCoinK = chrBalance.get() * gameCoinBalance.get();
    }

    @RequestMapping("/test/klaySCN/swap/getBalance")
    public ResponseEntity<?> swapGetBalance(@RequestParam Map<String, Object> map) {
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", chrBalance.get());
        balanceMap.put("gameCoinBalance", gameCoinBalance.get());
        return new ResponseEntity(balanceMap);
    }

    /**
     * 付出一定的chr兑换gameCoin，随着gameCoin的余额减少，同样数量chr兑换的gamecoin会越来越少
     *
     * @param map
     * @return
     */
    @RequestMapping("/test/klaySCN/swap/chrToGameCoin")
    public ResponseEntity<?> chrToGameCoin(@RequestParam Map<String, Object> map) {
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        long chrBalanceInt = chrBalance.addAndGet(Long.parseLong(map.get("value").toString()));
        long gameCoinMinus = gameCoinBalance.get() - chrGameCoinK / chrBalanceInt;
        long gameCoinBalanceAfter = gameCoinBalance.addAndGet(gameCoinMinus * (-1));
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", chrBalance.get());
        balanceMap.put("gameCoinBalance", gameCoinBalance.get());
        balanceMap.put("chrAdd", Integer.parseInt(map.get("value").toString()));
        balanceMap.put("gameCoinMinus", gameCoinMinus);
        return new ResponseEntity(balanceMap);
    }

    /**
     * 付出一定的gameCoin兑换chr，随着chr的余额减少，同样数量gameCoin兑换的chr会越来越少
     *
     * @param map
     * @return
     */
    @RequestMapping("/test/klaySCN/swap/gameCoinToChr")
    public ResponseEntity<?> gameCoinToChr(@RequestParam Map<String, Object> map) {
        if (map.get("value") == null || map.get("value").toString().length() == 0) {
            return new ResponseEntity(400, "value不能为空！");
        }
        long gameCoinInt = gameCoinBalance.addAndGet(Long.parseLong(map.get("value").toString()));
        long chrMinus = chrBalance.get() - chrGameCoinK / gameCoinInt;
        long gameCoinBalanceAfter = chrBalance.addAndGet(chrMinus * (-1));
        Map balanceMap = new HashMap();
        balanceMap.put("chrBalance", chrBalance.get());
        balanceMap.put("gameCoinBalance", gameCoinBalance.get());
        balanceMap.put("gameCoinAdd", Integer.parseInt(map.get("value").toString()));
        balanceMap.put("chrMinus", chrMinus);
        return new ResponseEntity(balanceMap);
    }

}

package easyJava.klay;

import com.alibaba.fastjson.JSON;
import easyJava.controller.SCNGameCoinController;
import easyJava.controller.websocket.TexasWS;
import easyJava.entity.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

public class ClearOrdersRedisThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClearOrdersRedisThread.class);
    int type;
    RedisTemplate<String, Object> redisTemplate;
    SCNGameCoinController sCNGameCoinController;

    public ClearOrdersRedisThread(int type, SCNGameCoinController sCNGameCoinController) {
        this.type = type;
        this.sCNGameCoinController = sCNGameCoinController;
        this.redisTemplate = sCNGameCoinController.redisTemplate;
    }

    /**
     * 类型0全部，1sale，2buy，3history
     *
     * @param type
     */
    public void clearOrdersRedis(int type) {
        String key = "getOrders:getSaleOrders";
        if (type == 0 || type == 1) {
            redisTemplate.opsForValue().set(key, new ArrayList<>());
        }
        if (type == 0 || type == 2) {
            key = "getOrders:getBuyOrders";
            redisTemplate.opsForValue().set(key, new ArrayList<>());
        }
        if (type == 0 || type == 3) {
            key = "getOrders:getHistoryOrders";
            redisTemplate.opsForValue().set(key, new ArrayList<>());
            key = "getOrders:getKline";
            redisTemplate.opsForValue().set(key, new ArrayList<>());
        }
    }

    public void sendNotification(String methodName, String secondIntervalStr, int pageSize, int order) {
        var ordersRedis = sCNGameCoinController.getOrdersList(methodName, null, secondIntervalStr, pageSize, order);
        BaseEntity entity = new BaseEntity();
        entity.setType(methodName);
        entity.setList(ordersRedis);
        TexasWS.sendToAllText(JSON.toJSONString(entity));
    }

    @Override
    public void run() {
        clearOrdersRedis(type);
        if (type == 0) {
            sendNotification("getHistoryOrders", "1", 15, 2);
            sendNotification("getKline", "21600", 100, 1);
        }
        if (type == 0 || type == 2) {
            sendNotification("getBuyOrders", "1", 5, 1);
        }
        if (type == 0 || type == 1) {
            sendNotification("getSaleOrders", "1", 5, 1);
        }
    }
}

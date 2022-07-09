package easyJava.klay;

import com.alibaba.fastjson.JSON;
import easyJava.controller.SCNGameCoinController;
import easyJava.controller.websocket.TexasWS;
import easyJava.entity.BaseEntity;
import easyJava.utils.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClearOrdersRedisThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClearOrdersRedisThread.class);
    int type;

    public ClearOrdersRedisThread(int type) {
        this.type = type;
    }


    public void sendNotification(String methodName, String secondIntervalStr, int pageSize, int order) {
        var ordersRedis = SpringContextUtil.getBean(SCNGameCoinController.class)
                .getOrdersList(methodName, null, secondIntervalStr, pageSize, order, false);
        BaseEntity entity = new BaseEntity();
        entity.setType(methodName);
        entity.setList(ordersRedis);
        TexasWS.sendToAllText(JSON.toJSONString(entity));
    }

    @Override
    public void run() {
        //类型0全部，1sale，2buy，3history
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

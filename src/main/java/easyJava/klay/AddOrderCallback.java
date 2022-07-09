package easyJava.klay;

import com.alibaba.fastjson.JSON;
import com.klaytn.caver.methods.response.Callback;
import easyJava.controller.ScheduledController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddOrderCallback implements Callback {
    private static final Logger logger = LoggerFactory.getLogger(AddOrderCallback.class);
    String method;
    ClearOrdersRedisThread cache;
    @Autowired
    ScheduledController scheduledController;

    public AddOrderCallback(String method, ClearOrdersRedisThread cache) {
        this.method = method;
        this.cache = cache;
    }

    @Override
    public void accept(Object result) {
        logger.info("AddOrderCallback::" + JSON.toJSONString(result));
        cache.start();
        if (method.equals("addBuyOrder") || method.equals("addSaleOrder")) {
            scheduledController.matchOrders();
        }
    }

    @Override
    public void exception(Exception exception) {

    }
}
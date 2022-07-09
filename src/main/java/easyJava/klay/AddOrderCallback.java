package easyJava.klay;

import com.alibaba.fastjson.JSON;
import com.klaytn.caver.methods.response.Callback;
import easyJava.controller.SCNGameCoinController;
import easyJava.controller.ScheduledController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddOrderCallback implements Callback {
    private static final Logger logger = LoggerFactory.getLogger(AddOrderCallback.class);
    String method;
    ClearOrdersRedisThread cache;
    SCNGameCoinController sCNGameCoinController;

    public AddOrderCallback(String method, SCNGameCoinController sCNGameCoinController) {
        cache = new ClearOrdersRedisThread(0, sCNGameCoinController);
        this.method = method;
        this.sCNGameCoinController = sCNGameCoinController;
    }

    @Override
    public void accept(Object result) {
        logger.info("AddOrderCallback::" + JSON.toJSONString(result));
        cache.start();
        if (method.equals("addBuyOrder") || method.equals("addSaleOrder")) {
            new ScheduledController(sCNGameCoinController).matchOrders();
        }
    }

    @Override
    public void exception(Exception exception) {

    }
}
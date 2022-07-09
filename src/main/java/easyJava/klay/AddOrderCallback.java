package easyJava.klay;

import com.alibaba.fastjson.JSON;
import com.klaytn.caver.methods.response.Callback;
import easyJava.controller.ScheduledController;
import easyJava.utils.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddOrderCallback implements Callback {
    private static final Logger logger = LoggerFactory.getLogger(AddOrderCallback.class);
    String method;
    ClearOrdersRedisThread cache;

    public AddOrderCallback(String method) {
        cache = new ClearOrdersRedisThread(0);
        this.method = method;
    }

    @Override
    public void accept(Object result) {
        logger.info("AddOrderCallback:method:" + method + ",ret:" + JSON.toJSONString(result));
        cache.start();
        if (method.equals("addBuyOrder") || method.equals("addSaleOrder")) {
            SpringContextUtil.getBean(ScheduledController.class).matchOrders();
        }
    }

    @Override
    public void exception(Exception e) {
        logger.error("AddOrderCallback:method:" + method, e);
    }
}
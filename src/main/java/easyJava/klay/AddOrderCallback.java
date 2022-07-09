package easyJava.klay;

import com.alibaba.fastjson.JSON;
import com.klaytn.caver.methods.response.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddOrderCallback implements Callback {
    private static final Logger logger = LoggerFactory.getLogger(AddOrderCallback.class);
    String method;
    ClearOrdersRedisThread cache;

    public AddOrderCallback(String method, ClearOrdersRedisThread cache) {
        this.method = method;
        this.cache = cache;
    }

    @Override
    public void accept(Object result) {
        logger.info("AddOrderCallback::" + JSON.toJSONString(result));
        cache.start();
    }

    @Override
    public void exception(Exception exception) {

    }
}
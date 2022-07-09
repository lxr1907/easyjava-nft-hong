package easyJava.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@EnableScheduling
public class ScheduledController {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledController.class);

    //开启EnableScheduling注解，设置定时任务
    @Scheduled(cron = "* * * * * ?")
    public void matchOrders() {
        new matchOrders().start();
    }

    class matchOrders extends Thread {
        @Override
        public void run() {
            long begin = new Date().getTime();

            try {
                SCNGameCoinController.matchBuyOrder();
            } catch (Exception e) {
                logger.error("matchBuyOrder thread error", e);
            }
            long step1End = new Date().getTime();
            logger.info("matchBuyOrder end, time:" + (step1End - begin));
            try {
                SCNGameCoinController.matchSaleOrder();
            } catch (Exception e) {
                logger.error("matchSaleOrder thread error", e);
            }
            long step2End = new Date().getTime();
            logger.info("matchSaleOrder end, time:" + (step2End - step1End));
        }
    }
}

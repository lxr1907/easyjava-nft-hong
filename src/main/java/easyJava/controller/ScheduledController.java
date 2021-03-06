package easyJava.controller;

import easyJava.utils.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableScheduling
public class ScheduledController {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledController.class);
    int corePoolSize = 20;
    int maximumPoolSize = 40;
    long keepAliveTime = 20;
    TimeUnit unit = TimeUnit.SECONDS;
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5000);
    ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
            workQueue);

    //开启EnableScheduling注解，设置定时任务
    @Scheduled(cron = "0/10 * * * * ?")
    public void matchOrders() {
        executor.execute(new MatchOrders());
    }

    class MatchOrders extends Thread {
        @Override
        public void run() {
            long begin = new Date().getTime();

            try {
                SpringContextUtil.getBean(SCNGameCoinController.class).matchOrder();
            } catch (Exception e) {
                logger.error("matchBuyOrder thread error", e);
            }
            long step1End = new Date().getTime();
            logger.info("matchBuyOrder end, time:" + (step1End - begin));

        }
    }
}

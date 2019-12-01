package easyJava.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
//@EnableScheduling
public class ScheduledController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledController.class);

	//开启EnableScheduling注解，设置定时任务
	@Scheduled(cron = "0 */1 * * * ?")
	public void createTask() {

	}

}

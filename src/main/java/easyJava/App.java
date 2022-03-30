package easyJava;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class App {

	private static final Logger logger = LogManager.getLogger(App.class);

	public static void main(String[] args) {
		logger.info("开始启动，logger测试-----------------------------");
		SpringApplication.run(App.class, args);
	}

	/**
	 * 配置javamelody监控 spring boot 会按照order值的大小，从小到大的顺序来依次过滤
	 */
	@Bean
	@Order(Integer.MAX_VALUE - 1)
	public FilterRegistrationBean monitoringFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new MonitoringFilter());
		registration.addUrlPatterns("/*");
		registration.setName("monitoring");
		return registration;
	}

	/**
	 * 配置javamelody监听器sessionListener
	 */
	@Bean
	public ServletListenerRegistrationBean<SessionListener> servletListenerRegistrationBean() {
		ServletListenerRegistrationBean<SessionListener> slrBean = new ServletListenerRegistrationBean<SessionListener>();
		slrBean.setListener(new SessionListener());
		return slrBean;
	}
}

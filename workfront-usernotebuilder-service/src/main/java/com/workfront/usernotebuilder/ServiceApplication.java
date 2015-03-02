package com.workfront.usernotebuilder;

import com.workfront.usernotebuilder.config.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;
import org.springframework.core.task.*;
import org.springframework.integration.annotation.*;
import org.springframework.scheduling.concurrent.*;

/**
 * The User Note Builder Service Application.
 * <p>This application provides JMS consumers for WorkFront application-events
 * with the goal of producing user-notification updates.
 * This is a Spring Boot application, and relies heavily on Spring
 * annotation-driven Java configuration, classpath component-scanning,
 * classpath/property driven auto-configuration features and the Spring
 * Integration Java DSL extension.
 * <p>We also use the lombok project for boiler-plate reduction in POJO and
 * Java-bean conventional types.
 */
@SpringBootApplication
@IntegrationComponentScan
@EnableConfigurationProperties
public class ServiceApplication {

	public static void main(String[] args) {
		final ApplicationContext ctx = new SpringApplicationBuilder(ServiceApplication.class)
			.showBanner(Boolean.FALSE)
			.web(Boolean.TRUE)//using actuator endpoints during dev for profiling/analysis.
			.run(args);
	}

	/**
	 * @return
	Central Spring-managed ThreadPoolExecutor appropriate for JMX management
	 */
	@Bean
	public AsyncTaskExecutor taskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Constants.POOL_CORE_SIZE);
		executor.setMaxPoolSize(Constants.POOL_MAX_SIZE);
		return executor;
	}

	@Bean
	public ServiceMessageSource messageSource() {
		final ServiceMessageSource messageSource = new ServiceMessageSource();
		messageSource.setBasenames("classpath:locale/messages");
		messageSource.setDefaultEncoding("UTF-8");
		//CNC debugging messageSource.setUseCodeAsDefaultMessage(Boolean.TRUE);
		return messageSource;
	}
}

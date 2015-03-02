package com.workfront.usernotebuilder.config;

import com.workfront.usernotebuilder.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.config.*;
import org.springframework.context.annotation.*;

/**
 * Event-handler oriented configuration bean. Provides the
 * requisite service-locator factory beans for event-handler lookup.
 */
@Configuration("handlerConfig")
@CommonsLog
public class EventHandlerConfig {
	@Autowired
	ConsumerProperties consumerProperties;

	/**
	 * @return A proxy generating factory that will use our supplied interface to locate components.
	 */
	@Bean
	public ServiceLocatorFactoryBean handlerLocatorFactoryBean() {
		ServiceLocatorFactoryBean slf = new ServiceLocatorFactoryBean();
		slf.setServiceLocatorInterface(EventHandlerFactory.class);
		return slf;
	}

	/**
	 * @return a proxied implementation of {@link com.workfront.usernotebuilder.EventHandlerFactory}
	 */
	@Bean
	public EventHandlerFactory eventHandlerFactory() {
		return (EventHandlerFactory) handlerLocatorFactoryBean().getObject();
	}
}

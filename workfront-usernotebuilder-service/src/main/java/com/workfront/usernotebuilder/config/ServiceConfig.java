package com.workfront.usernotebuilder.config;

import com.attask.config.*;
import com.attask.event.transformer.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.health.*;
import org.apache.activemq.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.core.task.*;
import org.springframework.http.client.*;
import org.springframework.integration.annotation.*;
import org.springframework.integration.dsl.channel.*;
import org.springframework.jms.config.*;
import org.springframework.jms.connection.*;
import org.springframework.jms.listener.*;
import org.springframework.jms.support.destination.*;
import org.springframework.jmx.support.*;
import org.springframework.messaging.*;
import org.springframework.web.client.*;

import javax.jms.*;
import javax.management.*;
import javax.naming.*;

/**
 * Central application-wide configuration bean. Additional component scanning is
 * specified to incorporate configuration defaults for AtTask Component API and
 * AtTask Configuration frameworks. For example, we will setup a
 * {@link com.attask.config.DefaultConfigurationManager} instance and our
 * {@link com.attask.component.api.ApiFactory.DefaultApiFactory}.
 */
@Configuration(value = "serviceConfig")
@ComponentScan(
	basePackages = {
		"com.attask.config", "com.attask.component.api"
	},
	excludeFilters={
		@ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=DefaultConfigurationManager.class)
	}
)
public class ServiceConfig {
	@Autowired
	private ConsumerProperties consumerProperties;

	@Autowired
	private JmsExceptionHealth jmsExceptionHealthIndicator;

	@Autowired
	private TaskExecutor taskExecutor;

	//CNC consider that this bean may be redundant
	@Bean
	public JsonMessageTransformer jsonMessageTransformer() {
		return new JsonMessageTransformer();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(clientHttpRequestFactory());
	}

	/**
	 * Uses defaults from HttpClientBuilder which has 5 connection limit
	 * PoolingHttpClientConnectionManager, and InternalHttpClient.
	 * - Adjustable through http.* properties.
	 */
	private ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory =
			new HttpComponentsClientHttpRequestFactory();

		//CNC consider using a property for this
		factory.setReadTimeout(Constants.HTTP_TIMEOUT);
		factory.setConnectTimeout(Constants.HTTP_TIMEOUT);
		return factory;
	}

	/**
	 * @return {@link AppEventTopicDestinationResolver}
	 */
	@Bean
	public DestinationResolver destinationResolver() {
		return new AppEventTopicDestinationResolver(
			consumerProperties.getName(), consumerProperties.getTopicPattern());
	}

	/**
	 * @return the application-wide ActiveMQ JMS-provider connection factory
	 */
	@Bean
	@Profile("connection")
	public ConnectionFactory jmsConnectionFactory() {
		/* CNC consider effect of using connection pooling/caching via
		org.springframework.jms.connection.CachingConnectionFactory or org.apache.activemq.pool.PooledConnectionFactory
		...see example below
		*/
		final ActiveMQConnectionFactory connectionFactory =
			new ActiveMQConnectionFactory(consumerProperties.getBrokerLocation());

		connectionFactory.setDispatchAsync(Boolean.TRUE);
		connectionFactory.setUseAsyncSend(Boolean.TRUE);
		connectionFactory.setExceptionListener(jmsExceptionHealthIndicator);
		connectionFactory.setMaxThreadPoolSize(Constants.POOL_MAX_SIZE);
		return connectionFactory;
	}

	/**
	 * @return a Caching Connection Factory that will cache and re-use JMS sessions
	 * @throws NamingException
	 */
	@Bean
	@Profile("cacheConnection")
	public ConnectionFactory cachingConnectionFactory() throws NamingException {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setTargetConnectionFactory(jmsConnectionFactory());
		//Up session caching when concurrent demand requires...
		connectionFactory.setSessionCacheSize(Constants.JMS_SESSIONCACHE_SIZE);
		return connectionFactory;
	}

	/**
	 * @return the DMLC factory used for our {@link org.springframework.jms.annotation.JmsListener}s
	 * @see org.springframework.jms.config.JmsListenerContainerFactory
	 */
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(jmsConnectionFactory());
		factory.setDestinationResolver(destinationResolver());
		//CNC TEST! consider using our central executor taskExecutor()
		factory.setConcurrency(Constants.POOL_RANGE);
		factory.setTaskExecutor(taskExecutor);
		factory.setSessionTransacted(Boolean.TRUE);
		factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
		factory.setReceiveTimeout(consumerProperties.getReceiveTimeout());
		factory.setRecoveryInterval(
			DefaultMessageListenerContainer.DEFAULT_RECOVERY_INTERVAL);

		return factory;
	}

	/**
	 * @return The internal message gateway default request channel
	 */
	@Bean
	public MessageChannel rbqRequestChannel() {
		//CNC consider using ExecutorChannel if synchronous dispatch is unimportant
		return MessageChannels.direct(consumerProperties.getRequestChannel()).get();
	}

	/**
	 * Our "internalFlow" internal messaging gateway.
	 */
	@MessagingGateway(defaultRequestChannel = "#{consumerProperties.getRequestChannel()}")
	public interface UserNoteGateway {
		void send(ServiceEvent event);
	}

	@Profile("prototype")
	@Bean
	public MessageChannel sourceChannel() {
		return MessageChannels.direct(consumerProperties.getSourceChannel()).get();
	}

	/**
	 * Our "short-circuit" internal messaging gateway.
	 */
	@Profile("prototype")
	@MessagingGateway(defaultRequestChannel = "#{consumerProperties.getSourceChannel()}")
	public interface UserNoteSourceGateway {
		void send(UserNoteSource event);
	}

	@Bean
	public ConnectorServerFactoryBean serverConnector() throws MalformedObjectNameException {
		ConnectorServerFactoryBean bean = new ConnectorServerFactoryBean();
		bean.setObjectName(Constants.JMX_CONNECTOR);
		bean.setServiceUrl(Constants.JMX_SERVICE_URL);
		return bean;
	}
}

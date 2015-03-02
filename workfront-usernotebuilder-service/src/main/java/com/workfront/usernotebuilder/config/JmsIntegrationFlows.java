package com.workfront.usernotebuilder.config;

import com.attask.component.api.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.*;
import com.workfront.usernotebuilder.event.*;
import lombok.extern.apachecommons.*;
import org.apache.activemq.command.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.core.task.*;
import org.springframework.integration.core.*;
import org.springframework.integration.dsl.*;
import org.springframework.integration.dsl.jms.*;
import org.springframework.integration.dsl.support.*;
import org.springframework.integration.transformer.*;

import javax.jms.*;

/**
 * Instantiate the various message flows used for JMS and related internal
 * message handling. Uses the Spring Integration 4.0 Java DSL extension.
 */
@Configuration
@CommonsLog
public class JmsIntegrationFlows {
	@Autowired
	private ApiFactory apiFactory;

	@Autowired
	private ConnectionFactory jmsConnectionFactory;

	@Autowired
	private ConsumerProperties consumerProperties;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private UserNoteServiceHandler userNoteServiceHandler;

	@Bean
	public UserNoteTransformer userNoteTransformer() {
		return new UserNoteTransformer();
	}

	/**
	 * This is our current forward integration flow.
	 * @return {@link org.springframework.integration.dsl.IntegrationFlow} for our
	 * {@link ServiceConfig.UserNoteGateway}
	 */
	@Bean
	@DependsOn("errorFlow")
	@Profile("development")
	public IntegrationFlow internalFlow() {
		return IntegrationFlows.from(consumerProperties.getRequestChannel())
			//.channel(MessageChannels.executor(taskExecutor))
			.transform(userNoteTransformer())
			.filter(new GenericSelector<UserNote>() {
				@Override
				public boolean accept(UserNote source) {
					final boolean isValidNotable = AppGUID.isGUID(source.getUserNotableID());
					final boolean isValidType = (null != source.getEventType());
					return (isValidNotable && isValidType);
				}
			})
			.handle(this.userNoteServiceHandler)
			.get();
	}

	@Bean
	@DependsOn("errorFlow")
	@Profile("prototype")
	public IntegrationFlow sourceFlow() {
		return IntegrationFlows.from(consumerProperties.getSourceChannel())
			.filter(new GenericSelector<UserNote>() {
				@Override
				public boolean accept(UserNote source) {
					final boolean isValidNotable = AppGUID.isGUID(source.getUserNotableID());
					final boolean isValidType = (null != source.getEventType());
					return (isValidNotable && isValidType);
				}
			})
			.handle(new UserNoteServiceHandler(apiFactory))
			.get();
	}

	//CNC test and remove (redundant flows)
	@Bean
	@DependsOn("errorFlow")
	@Profile("prototype")
	public IntegrationFlow inboundFlow() {
		return IntegrationFlows.from(
			Jms.inboundGateway(jmsConnectionFactory).destination(
				new ActiveMQQueue(consumerProperties.getQueueName())))
			.transform(new GenericTransformer<Object, Object>() {
				@Override
				public Object transform(Object source) {
					log.debug("Received INBOUND (RBQ) message: " + source.toString());
					return source;
				}
			})
			//.transform(Transformers.fromJson(UserNoteBuilderEvent.class))
			.transform(userNoteTransformer())
			.filter(new GenericSelector<UserNote>() {
				@Override
				public boolean accept(UserNote source) {
					return AppGUID.isGUID(source.getUserNotableID());
				}
			})
			.handle(new UserNoteServiceHandler(apiFactory))
			.get();
	}

	//CNC test and remove (redundant flows)
	@Bean
	@Profile("prototype")
	public IntegrationFlow outboundFlow() {
		return IntegrationFlows.from("fakeChannel")
			.handle(Jms.outboundGateway(jmsConnectionFactory)
				.replyContainer(new Consumer<JmsOutboundGatewaySpec.ReplyContainerSpec>() {
					@Override
					public void accept(JmsOutboundGatewaySpec.ReplyContainerSpec replyContainerSpec) {
						replyContainerSpec.concurrentConsumers(3);
						replyContainerSpec.sessionTransacted(Boolean.TRUE);
					}
				})
				.requestDestination(consumerProperties.getQueueName()))
			.get();
	}

	/**
	 * CNC
	 * @return
	 */
	@Bean
	public IntegrationFlow errorFlow() {
		//CNC do something useful here
		return IntegrationFlows.from("rejected")
			.transform("'Error with Message Flow: ' + payload")
			.get();
	}
}


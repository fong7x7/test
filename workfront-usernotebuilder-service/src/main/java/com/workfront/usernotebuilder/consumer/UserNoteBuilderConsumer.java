package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.event.transformer.*;
import com.workfront.usernotebuilder.config.ServiceConfig.*;
import com.workfront.usernotebuilder.event.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jms.listener.*;

import javax.jms.*;
import java.io.*;

/**
 * Base-type for our application event consumers. Responsible for initial message consumption
 * from (JMS) destination, session transaction management, and for preliminary deserialization transforms.
 * Concrete listeners are {@link com.workfront.usernotebuilder.config.ServiceConfig#jmsListenerContainerFactory()}
 */
@CommonsLog
abstract class UserNoteBuilderConsumer<T extends AbstractApplicationEvent> implements SessionAwareMessageListener<TextMessage>, Serializable {
	@Autowired
	private UserNoteGateway gateway;

	@Autowired
	private JsonMessageTransformer transformer;

	private final Class<T> eventClass;
	private final String eventType;

	UserNoteBuilderConsumer(Class<T> eventClass, String eventType) {
		this.eventClass = eventClass;
		this.eventType = eventType;
	}

	public abstract String getTargetObjCode();

	@Override
	public void onMessage(TextMessage message, Session session) throws JMSException {
		//CNC consider flow in which we evaluate event handler here, without intermediary gateway
		try {
			final String messageText = message.getText();
			log.debug("received message with text: " + messageText);

			T event = getTransformer().deserialize(getEventClass(), messageText);
			// This has become largely unnecessary. We could simply pass through the deserialized app event.
			// TODO consider deprecation
			final ServiceEvent<T> queueEvent = buildServiceEvent(event);

			// Direct to the next stage of internal message handling.
			getGateway().send(queueEvent);
			session.commit();
		//CNC consider other exceptions...
		} catch (JMSException e) {
			log.error("error processing message", e);
			session.rollback();
		}
	}

	public Class<T> getEventClass() {
		return eventClass;
	}

	public String getEventType() {
		return eventType;
	}

	public JsonMessageTransformer getTransformer() {
		return transformer;
	}

	UserNoteGateway getGateway() {
		return gateway;
	}

	protected UserNoteBuilderEvent<T> buildServiceEvent(T event) {
		return UserNoteBuilderEvent.<T>builder()
			.eventClass(getEventClass())
			.payload(event)
			.customerID(event.getCustomerId())
			.targetObjCode(getTargetObjCode())
			.build();
	}

	/* CNC consider per-consumer tunables...
	public int getMaxConcurrentConsumers() {
		return 1;
	}
	*/

}

package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.like
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component
public class LikeConsumer extends UserNoteBuilderConsumer<LikeEvent> {
	public LikeConsumer() {
		super(LikeEvent.class, LikeEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return Like.OBJCODE;
	}

	@Override
	@JmsListener(destination = "like")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}
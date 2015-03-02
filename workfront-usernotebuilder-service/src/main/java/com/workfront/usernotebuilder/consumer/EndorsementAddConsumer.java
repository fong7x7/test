package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.endorsement.add
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component("endorsementAddConsumer")
public class EndorsementAddConsumer extends UserNoteBuilderConsumer<EndorsementAddEvent> {
	public EndorsementAddConsumer() {
		super(EndorsementAddEvent.class, EndorsementAddEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode(){
		return Endorsement.OBJCODE;
	}

	@Override
	@JmsListener(destination = "endorsement.add")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}

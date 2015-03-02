package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.endorsement.shared
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component("endorsementSharedConsumer")
public class EndorsementSharedConsumer extends UserNoteBuilderConsumer<EndorsementShared> {
	public EndorsementSharedConsumer() {
		super(EndorsementShared.class, EndorsementShared.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return EndorsementShare.OBJCODE;
	}

	//CNC fix this @JmsListener(destination = "Consumer.RBQ.VirtualTopic.endorsement.shared")
	//@JmsListener(destination = "#{T(com.attask.event.EndorsementShared).TYPE_NAME}")
	//@JmsListener(destination = "#{endorsementSharedConsumer.getEventType()}")
	@Override
	@JmsListener(destination = "endorsement.shared")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}

package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.document.request
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component
public class DocumentRequestConsumer extends UserNoteBuilderConsumer<DocumentRequestEvent> {
	public DocumentRequestConsumer() {
		super(DocumentRequestEvent.class, DocumentRequestEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return DocumentRequest.OBJCODE;
	}

	@Override
	@JmsListener(destination = "document.request")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}
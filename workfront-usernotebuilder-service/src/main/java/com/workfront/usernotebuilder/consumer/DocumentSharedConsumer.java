package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.document.shared
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component
public class DocumentSharedConsumer extends UserNoteBuilderConsumer<DocumentShareEvent> {
	public DocumentSharedConsumer() {
		super(DocumentShareEvent.class, DocumentShareEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return DocumentShare.OBJCODE;
	}

	@Override
	@JmsListener(destination = "document.shared")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}
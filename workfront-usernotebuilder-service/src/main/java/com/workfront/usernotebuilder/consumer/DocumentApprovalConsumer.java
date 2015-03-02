package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.document.approve
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component
public class DocumentApprovalConsumer extends UserNoteBuilderConsumer<DocumentApproveEvent> {
	public DocumentApprovalConsumer() {
		super(DocumentApproveEvent.class, DocumentApproveEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return DocumentApproval.OBJCODE;
	}

	@Override
	@JmsListener(destination = "document.approve")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}
package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.issue.add
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component
public class IssueAddConsumer extends UserNoteBuilderConsumer<IssueAddEvent> {
	public IssueAddConsumer() {
		super(IssueAddEvent.class, IssueAddEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return OpTask.OBJCODE;
	}

	@Override
	@JmsListener(destination = "issue.add")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}
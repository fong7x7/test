package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.project.add
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component
public class ProjectAddConsumer extends UserNoteBuilderConsumer<ProjectAddEvent> {
	public ProjectAddConsumer() {
		super(ProjectAddEvent.class, ProjectAddEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return Project.OBJCODE;
	}

	@Override
	@JmsListener(destination = "project.add")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}

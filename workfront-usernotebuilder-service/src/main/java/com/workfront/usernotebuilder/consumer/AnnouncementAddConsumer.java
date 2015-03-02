package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.announcement.add
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component
public class AnnouncementAddConsumer extends UserNoteBuilderConsumer<AnnouncementAddEvent> {
	public AnnouncementAddConsumer() {
		super(AnnouncementAddEvent.class, AnnouncementAddEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return Announcement.OBJCODE;
	}

	@Override
	@JmsListener(destination = "announcement.add")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}
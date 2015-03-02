package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import org.springframework.jms.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;

/**
 * JMS message listener for Consumer.[name].VirtualTopic.note.add
 * sourced "events."
 * <p>Will use the default application jmsConnectionFactory bean.
 */
@Component
public class NoteConsumer extends UserNoteBuilderConsumer<NoteAddEvent> {
	public NoteConsumer() {
		super(NoteAddEvent.class, NoteAddEvent.getTypeName());
	}

	@Override
	public String getTargetObjCode() {
		return Note.OBJCODE;
	}

	@Override
	@JmsListener(destination = "note.add")
	public void onMessage(TextMessage message, Session session) throws JMSException {
		super.onMessage(message, session);
	}
}
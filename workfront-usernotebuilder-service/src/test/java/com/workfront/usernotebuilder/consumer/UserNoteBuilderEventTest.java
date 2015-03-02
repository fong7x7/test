package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
//import com.attask.event.service.UserNoteBuilderEvent;
import com.attask.event.transformer.*;
import com.attask.sdk.model.internal.*;
import org.junit.*;

import static org.junit.Assert.*;

public class UserNoteBuilderEventTest {

	private static final String CUSTOMER_ID = "1234556789abcdefghijklmnopqrst";

	private JsonMessageTransformer transformer = new JsonMessageTransformer();

	@Test
	public void testBuilder() throws Exception {
		ProjectAddEvent event = ProjectAddEvent.builder()
			.customerId(CUSTOMER_ID)
			.build();

		final String payload = transformer.serialize(event);

		//final com.attask.event.service.UserNoteBuilderEvent<ProjectAddEvent> queueEvent =
		//	UserNoteBuilderEvent.<ProjectAddEvent>builder()
		//		.eventClass(ProjectAddEvent.class)
		//		.payload(payload)
		//		.targetObjCode(Project.OBJCODE)
		//		.build();
		//
		//assertTrue(payload.equals(queueEvent.getPayload()));
		//assertTrue(ProjectAddEvent.class.equals(queueEvent.getEventClass()));
		//assertEquals(event.getId(), transformer.deserialize(ProjectAddEvent.class, payload).getId());
	}
}

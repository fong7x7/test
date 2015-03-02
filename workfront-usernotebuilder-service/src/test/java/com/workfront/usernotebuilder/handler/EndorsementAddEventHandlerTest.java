package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.util.*;
import org.junit.*;
import org.mockito.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EndorsementAddEventHandlerTest extends AbstractEventHandlerTest<EndorsementAddEvent> {
	@InjectMocks
	private final EndorsementAddEventHandler endorsementAddEventHandler = new EndorsementAddEventHandler();

	@Test
	public void testEndorsementAddEventHandling() throws AtTaskException {
		final EndorsementAddEvent endorsementAddEvent = buildEvent(EndorsementAddEvent.class);
		final int numberOfIDs = 1;
		final String mockReceiverID = generateGUID();
		endorsementAddEvent.setId(MOCK_ID);
		endorsementAddEvent.setReceiverID(mockReceiverID);

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = endorsementAddEventHandler.handleEvent(endorsementAddEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(UserNoteEvent.ENDORSEMENT_ADD, handledSource.getEventType());

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, mockReceiverID)));
	}
}

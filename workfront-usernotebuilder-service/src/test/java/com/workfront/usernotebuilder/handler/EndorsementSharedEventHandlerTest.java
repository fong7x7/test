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

public class EndorsementSharedEventHandlerTest extends AbstractEventHandlerTest<EndorsementShared> {
	@InjectMocks
	private final EndorsementSharedEventHandler endorsementSharedEventHandler = new EndorsementSharedEventHandler();

	@Test
	public void testEndorsementSharedEventHandler() throws AtTaskException {
		final EndorsementShared endorsementSharedEvent = buildEvent(EndorsementShared.class);
		final int numberOfIDs = 1;
		final String accessorID = generateGUID();
		endorsementSharedEvent.setId(MOCK_ID);
		endorsementSharedEvent.setAccessorID(accessorID);
		endorsementSharedEvent.setAccessorObjCode(User.OBJCODE);

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = endorsementSharedEventHandler.handleEvent(endorsementSharedEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(UserNoteEvent.ENDORSEMENT_SHARE, handledSource.getEventType());

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, accessorID)));
	}
}

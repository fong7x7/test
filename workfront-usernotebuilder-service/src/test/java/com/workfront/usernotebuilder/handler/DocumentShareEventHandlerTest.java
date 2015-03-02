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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * This tests the DocumentShareEventHandler as a whole and tests both the DocumentApprovalAddHandler
 * and the DocumentSharedHandler.
 */
public class DocumentShareEventHandlerTest extends AbstractEventHandlerTest<DocumentShareEvent> {
	@Spy private DocumentApproverShareHandler documentApproverShareHandler = new DocumentApproverShareHandler(apiFactory);

	@InjectMocks
	private final DocumentSharedHandler documentShareEventHandler = new DocumentSharedHandler();

	@Test
	public void testDocumentShareAddHandling() throws AtTaskException {
		final DocumentShareEvent documentShareEvent = buildEvent(DocumentShareEvent.class);
		final int numberOfIDs = 1;
		final String mockUserID = generateGUID();
		final String mockTeamID = generateGUID();
		documentShareEvent.setId(MOCK_ID);
		documentShareEvent.setUserID(mockUserID);

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = documentShareEventHandler.handleEvent(documentShareEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, mockUserID)));
		assertEquals(UserNoteEvent.DOCUMENTSHARE_ADD_USER, handledSource.getEventType());

		// Test proofer
		documentShareEvent.setProofer(true);
		source = UserNoteSource.builder().build();
		handledSource = documentShareEventHandler.handleEvent(documentShareEvent, source);
		assertEquals(UserNoteEvent.DOCUMENTSHARE_ADD_USER_PROOFING, handledSource.getEventType());

		// Test team
		documentShareEvent.setProofer(false);
		documentShareEvent.setTeamID(mockTeamID);
		source = UserNoteSource.builder().build();
		handledSource = documentShareEventHandler.handleEvent(documentShareEvent, source);
		assertEquals(UserNoteEvent.DOCUMENTSHARE_ADD_TEAM, handledSource.getEventType());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(Team.OBJCODE, mockTeamID)));
	}

	@Test
	public void testDocumentShareAddFiltering() throws AtTaskException {
		final DocumentShareEvent documentShareEvent = buildEvent(DocumentShareEvent.class);
		final int numberOfIDs = 0;
		final String mockUserID = generateGUID();
		documentShareEvent.setId(MOCK_ID);
		documentShareEvent.setUserID(MOCK_TRANSACTION_USER_ID);

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = documentShareEventHandler.handleEvent(documentShareEvent, source);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertFalse(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, mockUserID)));

		//Remove user if userID is the same as the editedByID
		documentShareEvent.setUserID(mockUserID);
		documentShareEvent.setEditedByID(mockUserID);

		source = UserNoteSource.builder().build();
		handledSource = documentShareEventHandler.handleEvent(documentShareEvent, source);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertFalse(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, mockUserID)));

	}

	@Test
	public void testDocumentApprovalAddHandling() throws AtTaskException {
		final DocumentShareEvent documentShareEvent = buildEvent(DocumentShareEvent.class);
		final int numberOfIDs = 1;
		final String mockUserID = generateGUID();
		documentShareEvent.setId(MOCK_ID);
		documentShareEvent.setApprover(true);
		documentShareEvent.setUserID(mockUserID);

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = documentShareEventHandler.handleEvent(documentShareEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, mockUserID)));

		verify(documentApproverShareHandler).handleEvent(documentShareEvent, source);
		assertEquals(UserNoteEvent.DOCUMENTAPPROVAL_ADD_USER, handledSource.getEventType());

		documentShareEvent.setProofer(true);
		source = UserNoteSource.builder().build();
		handledSource = documentShareEventHandler.handleEvent(documentShareEvent, source);
		assertEquals(UserNoteEvent.DOCUMENTAPPROVAL_ADD_USER_PROOFING, handledSource.getEventType());
	}
}

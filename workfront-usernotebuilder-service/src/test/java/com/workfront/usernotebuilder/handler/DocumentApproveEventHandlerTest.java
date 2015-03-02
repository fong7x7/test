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

public class DocumentApproveEventHandlerTest extends AbstractEventHandlerTest<DocumentApproveEvent> {
	@InjectMocks
	private DocumentApproveEventHandler documentApproveEventHandler = new DocumentApproveEventHandler();

	@Test
	public void testDocumentApprovalNotifyApprover() throws AtTaskException {
		final DocumentApproveEvent documentApproveEvent = buildEvent(DocumentApproveEvent.class);
		final int numberOfIDs = 1;
		final String approverID = generateGUID();
		documentApproveEvent.setId(MOCK_ID);
		documentApproveEvent.setApproverID(approverID);
		documentApproveEvent.setStatus(DocumentApprovalStatus.CANCELED.getValue());

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = documentApproveEventHandler.handleEvent(documentApproveEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, approverID)));
		assertEquals(UserNoteEvent.DOCUMENTAPPROVAL_CANCELED, handledSource.getEventType());
	}

	@Test
	public void testDocumentApprovalNotifyRequestor() throws AtTaskException {
		final DocumentApproveEvent documentApproveEvent = buildEvent(DocumentApproveEvent.class);
		final int numberOfIDs = 1;
		final String requestorID = generateGUID();
		documentApproveEvent.setId(MOCK_ID);
		documentApproveEvent.setRequestorID(requestorID);
		documentApproveEvent.setStatus(DocumentApprovalStatus.APPROVED.getValue());

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = documentApproveEventHandler.handleEvent(documentApproveEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, requestorID)));
		assertEquals(UserNoteEvent.DOCUMENTAPPROVAL_APPROVED, handledSource.getEventType());

		//Test Approved with changes
		documentApproveEvent.setStatus(DocumentApprovalStatus.APPROVED_WITH_CHANGES.getValue());
		handledSource = documentApproveEventHandler.handleEvent(documentApproveEvent, UserNoteSource.builder().build());

		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, requestorID)));
		assertEquals(UserNoteEvent.DOCUMENTAPPROVAL_APPROVED_WITH_CHANGES, handledSource.getEventType());

		//Test Rejected
		documentApproveEvent.setStatus(DocumentApprovalStatus.REJECTED.getValue());
		handledSource = documentApproveEventHandler.handleEvent(documentApproveEvent, UserNoteSource.builder().build());

		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, requestorID)));
		assertEquals(UserNoteEvent.DOCUMENTAPPROVAL_REJECTED, handledSource.getEventType());
	}
}

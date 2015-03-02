package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.util.*;
import org.junit.*;
import org.mockito.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class NoteAddEventHandlerTest extends AbstractEventHandlerTest<NoteAddEvent> {
	@Spy private final WorkRequestReplyHandler workRequestReplyHandler = new WorkRequestReplyHandler(apiFactory);
	@Spy private final HelpDeskRequestHandler helpDeskRequestHandler = new HelpDeskRequestHandler(apiFactory);
	@Spy private final TimeSheetCommentHandler timeSheetCommentHandler = new TimeSheetCommentHandler(apiFactory);
	@Spy private final JournalEntryCommentedHandler journalEntryCommentedHandler = new JournalEntryCommentedHandler(apiFactory);
	@Spy private final ThreadCommentHandler threadCommentHandler = new ThreadCommentHandler(apiFactory);

	@InjectMocks
	private final NoteAddEventHandler noteAddEventHandler = new NoteAddEventHandler();

	@Test
	public void testWorkRequestReplyHandler() throws AtTaskException {
		final NoteAddEvent noteAddEvent = buildEvent(NoteAddEvent.class);
		final int numberOfIDs = 2;
		final String taggedUserID = generateGUID();
		final String refAssignedByID = generateGUID();
		final String refObjID = generateGUID();
		noteAddEvent.setId(MOCK_ID);
		noteAddEvent.setSubject("Proposed");
		noteAddEvent.setRefObjID(refObjID);
		noteAddEvent.setRefObjCode(Task.OBJCODE);
		noteAddEvent.setNoteTagUserIDs(Collections.singleton(taggedUserID));
		noteAddEvent.setRefObjAssignedByIDs(Collections.singletonList(refAssignedByID));

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = noteAddEventHandler.handleEvent(noteAddEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());

		assertTrue(
			containsTestGUIDs(
				handledSource,
				CollectorUtil.generateAppGUID(User.OBJCODE, taggedUserID),
				CollectorUtil.generateAppGUID(User.OBJCODE, refAssignedByID)
			)
		);

		verify(workRequestReplyHandler).handleEvent(noteAddEvent, source);
		assertEquals(UserNoteEvent.WORK_REQUEST_REPLY, handledSource.getEventType());

		//Test teamAssignedBy
		final String teamAssignedByID = generateGUID();
		noteAddEvent.setRefObjAssignedByIDs(null);
		noteAddEvent.setRefObjTeamAssignedByID(teamAssignedByID);

		source = UserNoteSource.builder().build();
		handledSource = noteAddEventHandler.handleEvent(noteAddEvent, source);
		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(
			containsTestGUIDs(
				handledSource,
				CollectorUtil.generateAppGUID(User.OBJCODE, taggedUserID),
				CollectorUtil.generateAppGUID(User.OBJCODE, teamAssignedByID)
			)
		);
	}

	@Test
	public void testHelpDeskRequestHandler() throws AtTaskException {
		final NoteAddEvent noteAddEvent = buildEvent(NoteAddEvent.class);
		final int numberOfIDs = 2;
		final String ownerID = generateGUID();
		final String taggedUserID = generateGUID();
		noteAddEvent.setId(MOCK_ID);
		noteAddEvent.setOwnerID(ownerID);
		noteAddEvent.setNoteTagUserIDs(Collections.singleton(taggedUserID));
		noteAddEvent.setTopNoteObjCode(OpTask.OBJCODE);
		noteAddEvent.setProjectID(MOCK_ID);
		noteAddEvent.setProjectStatus(ProjectStatus.CURRENT.getValue());

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = noteAddEventHandler.handleEvent(noteAddEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());

		assertTrue(
			containsTestGUIDs(
				handledSource,
				CollectorUtil.generateAppGUID(User.OBJCODE, ownerID),
				CollectorUtil.generateAppGUID(User.OBJCODE, taggedUserID)
			)
		);

		verify(helpDeskRequestHandler).handleEvent(noteAddEvent, source);
		assertEquals(UserNoteEvent.HELP_REQUEST_COMMENT, handledSource.getEventType());
	}

	@Test
	public void testTimeSheetCommentHandler() throws AtTaskException {
		final NoteAddEvent noteAddEvent = buildEvent(NoteAddEvent.class);
		final int numberOfIDs = 1;
		final String timesheetApproverID = generateGUID();
		final String topObjID = generateGUID();
		noteAddEvent.setId(MOCK_ID);
		noteAddEvent.setTopNoteObjCode(Timesheet.OBJCODE);
		noteAddEvent.setTopObjID(topObjID);
		noteAddEvent.setNoteText("Text");
		noteAddEvent.setTopObjStatus(TimesheetStatus.SUBMITTED.getValue());
		noteAddEvent.setTimesheetApproverIDs(Collections.singletonList(timesheetApproverID));

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = noteAddEventHandler.handleEvent(noteAddEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());

		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, timesheetApproverID)));

		verify(timeSheetCommentHandler).handleEvent(noteAddEvent, source);
		assertEquals(UserNoteEvent.TIMESHEET_COMMENT_APPROVER, handledSource.getEventType());
	}

	@Test
	public void testJournalEntryCommentedHandler() throws AtTaskException {
		final NoteAddEvent noteAddEvent = buildEvent(NoteAddEvent.class);
		final int numberOfIDs = 2;
		final String editedByID = generateGUID();
		final String taggedUserID = generateGUID();
		noteAddEvent.setId(MOCK_ID);
		noteAddEvent.setParentJournalEntryID(MOCK_ID);
		noteAddEvent.setParentJournalEntryEditedByID(editedByID);
		noteAddEvent.setNoteTagUserIDs(Collections.singleton(taggedUserID));

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = noteAddEventHandler.handleEvent(noteAddEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());

		assertTrue(
			containsTestGUIDs(
				handledSource,
				CollectorUtil.generateAppGUID(User.OBJCODE, editedByID),
				CollectorUtil.generateAppGUID(User.OBJCODE, taggedUserID)
			)
		);

		verify(journalEntryCommentedHandler).handleEvent(noteAddEvent, source);
		assertEquals(UserNoteEvent.MESSAGE, handledSource.getEventType());
	}

	@Test
	public void testThreadCommentedHandler() throws AtTaskException {
		final NoteAddEvent noteAddEvent = buildEvent(NoteAddEvent.class);
		final int numberOfIDs = 6;
		final String parentEnteredByID = generateGUID();
		final String taggedUserID = generateGUID();
		final String ownerID = generateGUID();
		noteAddEvent.setId(MOCK_ID);
		noteAddEvent.setOwnerID(ownerID);
		noteAddEvent.setParentNoteID(MOCK_ID);
		noteAddEvent.setParentEnteredByID(parentEnteredByID);
		noteAddEvent.setNoteTagUserIDs(Collections.singletonList(taggedUserID));

		final String replyOwnerID = generateGUID();
		final String replyTaggedUserID = generateGUID();
		final String replyNoteUserID = generateGUID();
		final NoteAddEvent replyNote = NoteAddEvent.builder()
			.ownerID(replyOwnerID)
			.noteTagUserIDs(Collections.singletonList(replyTaggedUserID))
			.userIDs(Collections.singletonList(replyNoteUserID))
			.build();

		noteAddEvent.setReplyNoteEvents(Collections.singletonList(replyNote));

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = noteAddEventHandler.handleEvent(noteAddEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());

		assertTrue(
			containsTestGUIDs(
				handledSource,
				CollectorUtil.generateAppGUID(User.OBJCODE, ownerID),
				CollectorUtil.generateAppGUID(User.OBJCODE, parentEnteredByID),
				CollectorUtil.generateAppGUID(User.OBJCODE, taggedUserID),
				CollectorUtil.generateAppGUID(User.OBJCODE, replyOwnerID),
				CollectorUtil.generateAppGUID(User.OBJCODE, replyTaggedUserID),
				CollectorUtil.generateAppGUID(User.OBJCODE, replyNoteUserID)
			)
		);

		verify(threadCommentHandler).handleEvent(noteAddEvent, source);
		assertEquals(UserNoteEvent.THREAD, handledSource.getEventType());
	}
}

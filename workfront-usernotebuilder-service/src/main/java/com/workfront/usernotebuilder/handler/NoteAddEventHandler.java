package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * Delegating event handler for comment-related events which routes to various handlers like:
 * <p>{@link HelpDeskRequestHandler}
 * <p>{@link JournalEntryCommentedHandler}
 * <p>{@link ThreadCommentHandler}
 * <p>{@link TimeSheetCommentHandler}
 * <p>{@link WorkRequestReplyHandler}
 */
@Component("NOTEADD")
@Handler(UserNoteEventHandlerEnum.NOTEADD)
public class NoteAddEventHandler extends AbstractApplicationEventUserNoteHandler<NoteAddEvent> {
	@Autowired private WorkRequestReplyHandler workRequestReplyHandler;
	@Autowired private HelpDeskRequestHandler helpDeskRequestHandler;
	@Autowired private TimeSheetCommentHandler timesheetCommentHandler;
	@Autowired private JournalEntryCommentedHandler journalEntryCommentedHandler;
	@Autowired private ThreadCommentHandler threadCommentHandler;

	public NoteAddEventHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public NoteAddEventHandler() {}

	@Override
	public UserNoteSource handleEvent(final NoteAddEvent event, final UserNoteSource outNotification) {
		if (isWorkRequestReply(event)) {
			return workRequestReplyHandler.handleEvent(event, outNotification);
		} else if(isIssueNote(event)) {
			return helpDeskRequestHandler.handleEvent(event, outNotification);
		} else if(isTimeSheetCommentForApproval(event)) {
			return timesheetCommentHandler.handleEvent(event, outNotification);
		} else if(isJournalEntryComment(event)) {
			return journalEntryCommentedHandler.handleEvent(event, outNotification);
		} else if(isThreadComment(event)) {
			return threadCommentHandler.handleEvent(event, outNotification);
		}
		return super.handleEvent(event, outNotification);
	}

	@Override
	protected Collection<AppGUID> collect(final NoteAddEvent event, final Collection<AppGUID> userIDs) throws AtTaskException {
		return userIDs;
	}

	@Override
	protected Collection<AppGUID> filter(final NoteAddEvent event, final Collection<AppGUID> userIDs) throws AtTaskException {
		return userIDs;
	}

	@Override
	protected UserNoteEvent getNotificationType(NoteAddEvent event) {
		return UserNoteEvent.MESSAGE;
	}

	private boolean isWorkRequestReply(final NoteAddEvent event) {
		if("Proposed".equals(event.getSubject())) {
			boolean hasRefObjAssignedBy = event.getRefObjAssignedByIDs() != null && !event.getRefObjAssignedByIDs().isEmpty();
			return hasRefObjAssignedBy || event.getRefObjTeamAssignedByID() != null;
		}
		return false;
	}

	private boolean isIssueNote(final NoteAddEvent event) {
		if(OpTask.OBJCODE.equals(event.getTopNoteObjCode())) {
			return event.getProjectID() != null && ProjectStatus.CURRENT.getValue().equals(event.getProjectStatus());
		}
		return false;
	}

	private boolean isTimeSheetCommentForApproval(final NoteAddEvent event) {
		return Timesheet.OBJCODE.equals(event.getTopNoteObjCode())
			&& event.getTopObjID() != null
			&& event.getNoteText() != null
			&& TimesheetStatus.SUBMITTED.getValue().equals(event.getTopObjStatus());
	}

	private boolean isJournalEntryComment(final NoteAddEvent event) {
		return event.getParentNoteID() == null && event.getParentJournalEntryID() != null;
	}

	private boolean isThreadComment(final NoteAddEvent event) {
		return event.getParentNoteID() != null
			|| event.getParentJournalEntryID() != null;
	}

	@Override
	public String getEventHandlerName(NoteAddEvent event) {
		return "default.usernoteeventenum.message";
	}
}
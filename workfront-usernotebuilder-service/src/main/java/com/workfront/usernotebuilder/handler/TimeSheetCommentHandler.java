package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.filter.*;
import com.workfront.usernotebuilder.util.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * Handler for timesheet comments.
 */
@Component("TIMESHEETCOMMENT")
public class TimeSheetCommentHandler extends AbstractApplicationEventUserNoteHandler<NoteAddEvent> {
	public TimeSheetCommentHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public TimeSheetCommentHandler() {
		super();
		addFilter(new NoteAddSecurityFilter(new ActionUserFilter()));
	}

	@Override
	protected Collection<AppGUID> collect(NoteAddEvent event, Collection<AppGUID> userIDs) throws AtTaskException {
		userIDs.addAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, event.getTimesheetApproverIDs()));
		return userIDs;
	}

	/* CNC using filter defined in constructor via base method
	@Override
	protected Collection<AppGUID> filter(NoteAddEvent event, Collection<AppGUID> filterIDs) throws AtTaskException {
		Filter<AppGUID> filter = new ActionUserFilter(event)
			.append(new NoteSecurityFilter(event));
		return FluentIterable.from(filterIDs).filter(filter.predicate()).toSet();
	}
	*/

	@Override
	protected UserNoteEvent getNotificationType(final NoteAddEvent event) {
		//CNC need to determine when to use the other Timesheet comment enums
		return UserNoteEvent.TIMESHEET_COMMENT_APPROVER;
	}

	@Override
	public String getEventHandlerName(NoteAddEvent event) {
		return "default.usereventhandler.timesheetcomment.approver";
	}
}

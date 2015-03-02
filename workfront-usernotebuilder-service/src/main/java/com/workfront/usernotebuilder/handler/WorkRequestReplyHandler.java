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

//CNC This time, there is no enum correlation. Enum lookup is one class per event, so what to do with this?
/**
 * Work request replies are handled here...
 */
@Component("WORKREQUESTREPLY")
public class WorkRequestReplyHandler extends AbstractApplicationEventUserNoteHandler<NoteAddEvent> {
	public WorkRequestReplyHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public WorkRequestReplyHandler() {
		super();
		addFilter(new NoteAddSecurityFilter(new ActionUserFilter()));
	}

	@Override
	protected Collection<AppGUID> collect(final NoteAddEvent event, Collection<AppGUID> userIDs) throws AtTaskException {
		// WorkRequestRepliersCollector
		userIDs.addAll(collectWorkRequestRepliers(event));

		//NoteTagCollector
		userIDs.addAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, event.getNoteTagUserIDs()));
		return userIDs;
	}

	/* CNC using filter defined in constructor via base method
	@Override
	protected Collection<AppGUID> filter(final NoteAddEvent event, Collection<AppGUID> filterIDs) throws AtTaskException {
		Filter<AppGUID> filter = new ActionUserFilter(event)
			.append(new NoteSecurityFilter(event));
		return FluentIterable.from(filterIDs).filter(filter.predicate()).toSet();
	}
	*/

	@Override
	protected UserNoteEvent getNotificationType(final NoteAddEvent event) {
		return UserNoteEvent.WORK_REQUEST_REPLY;
	}

	private boolean hasAssignableReferenceObject(final NoteAddEvent event) {
		final String refObjID = event.getRefObjID();
		if (AppGUID.isGUID(refObjID)) {
			final String refObjCode = event.getRefObjCode();
			return Task.OBJCODE.equals(refObjCode) || OpTask.OBJCODE.equals(refObjCode);
		}
		return false;
	}

	private Collection<AppGUID> collectWorkRequestRepliers(final NoteAddEvent event) throws AtTaskException {
		Collection<AppGUID> userIDs = new HashSet<>();
		if (hasAssignableReferenceObject(event)) {
			if(event.getRefObjAssignedByIDs() != null && !event.getRefObjAssignedByIDs().isEmpty()) {
				String firstAssignedByID = event.getRefObjAssignedByIDs().iterator().next();
				userIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, firstAssignedByID));
			} else if(event.getRefObjTeamAssignedByID() != null) {
				userIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, event.getRefObjTeamAssignedByID()));
			}
		}
		return userIDs;
	}

	@Override
	public String getEventHandlerName(NoteAddEvent event) {
		return "default.usereventhandler.workrequestreply";
	}
}

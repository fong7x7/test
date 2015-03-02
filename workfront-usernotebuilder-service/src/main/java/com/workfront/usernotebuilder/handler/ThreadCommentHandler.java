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
 * A handler for nested comments. Generally delegate from {@link NoteAddEventHandler}.
 * Must collect note owner, commentors, tagged recipients, and filter with
 * {@link com.workfront.usernotebuilder.filter.NoteAddSecurityFilter}, {@link com.workfront.usernotebuilder.filter.ActionUserFilter}
 * & defaults...
 */
@Component("THREADCOMMENT")
public class ThreadCommentHandler extends AbstractApplicationEventUserNoteHandler<NoteAddEvent> {
	public ThreadCommentHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public ThreadCommentHandler() {
		super();
		addFilter(new NoteAddSecurityFilter(new ActionUserFilter()));
	}

	@Override
	protected Collection<AppGUID> collect(NoteAddEvent event, Collection<AppGUID> collectIDs) throws AtTaskException {
		collectIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, event.getOwnerID()));

		//WorkItemCommentersCollecter
		collectIDs.addAll(collectWorkItemCommenters(event));

		//NoteTagCollector
		collectIDs.addAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, event.getNoteTagUserIDs()));
		return collectIDs;
	}

	/* CNC using composed filter from constructor definition
	@Override
	protected Collection<AppGUID> filter(NoteAddEvent event, Collection<AppGUID> filterIDs) throws AtTaskException {
		Filter<AppGUID> filter = new ActionUserFilter(event)
			.append(new NoteSecurityFilter(event));
		return FluentIterable.from(filterIDs).filter(filter.predicate()).toSet();
	}
	*/

	@Override
	protected UserNoteEvent getNotificationType(NoteAddEvent event) {
		return UserNoteEvent.THREAD;
	}

	private Collection<AppGUID> collectWorkItemCommenters(NoteAddEvent event) throws AtTaskException {
		Collection<AppGUID> userIDs = new HashSet<>();
		if (event.getParentEnteredByID() != null) {
			userIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, event.getParentEnteredByID()));
		}
		for (NoteAddEvent replyNote : event.getReplyNoteEvents()) {
			userIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, replyNote.getOwnerID()));
			userIDs.addAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, replyNote.getNoteTagUserIDs()));
			userIDs.addAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, replyNote.getUserIDs()));
		}
		userIDs.remove(CollectorUtil.generateAppGUID(User.OBJCODE, event.getOwnerID()));
		userIDs.removeAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, event.getRefObjAssignedByIDs()));
		userIDs.removeAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, event.getNoteTagUserIDs()));
		return userIDs;
	}

	@Override
	public String getEventHandlerName(NoteAddEvent event) {
		return "default.usereventhandler.threadcommented.participants";
	}
}

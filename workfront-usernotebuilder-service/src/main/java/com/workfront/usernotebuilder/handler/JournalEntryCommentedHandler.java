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
 * A handler for notes on visible journal entries. Generally delegate from {@link NoteAddEventHandler}
 * Must collect entry recipients and tagged recipients and filter with
 * {@link com.workfront.usernotebuilder.filter.NoteAddSecurityFilter} & defaults...
 */
@Component("JOURNALENTRYCOMMENT")
public class JournalEntryCommentedHandler extends AbstractApplicationEventUserNoteHandler<NoteAddEvent> {

	public JournalEntryCommentedHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public JournalEntryCommentedHandler() {
		super();
		addFilter(new NoteAddSecurityFilter(new ActionUserFilter()));
	}

	@Override
	protected Collection<AppGUID> collect(NoteAddEvent event, Collection<AppGUID> userIDs) throws AtTaskException {
		userIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, event.getParentJournalEntryEditedByID()));

		//NoteTagCollector
		userIDs.addAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, event.getNoteTagUserIDs()));
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
	protected UserNoteEvent getNotificationType(NoteAddEvent event) {
		return UserNoteEvent.MESSAGE;
	}

	@Override
	public String getEventHandlerName(NoteAddEvent event) {
		return "default.usernoteeventenum.message";
	}
}

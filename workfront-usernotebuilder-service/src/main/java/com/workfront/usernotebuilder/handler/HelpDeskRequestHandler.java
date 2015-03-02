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
 * A handler for help-desk request events. Generally delegate from {@link NoteAddEventHandler}
 * Must collect note owner and tagged recipients and filter on defaults...
 */
@Component("HELPDESKREQUEST")
public class HelpDeskRequestHandler extends AbstractApplicationEventUserNoteHandler<NoteAddEvent> {
	public HelpDeskRequestHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public HelpDeskRequestHandler() {
		super();
		addFilter(new NoteAddSecurityFilter(new ActionUserFilter()));
	}

	@Override
	protected Collection<AppGUID> collect(NoteAddEvent event, Collection<AppGUID> userIDs) throws AtTaskException {
		//get RKNote ownerID
		userIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, event.getOwnerID()));

		//Collect by NoteTagCollector
		userIDs.addAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, event.getNoteTagUserIDs()));
		return userIDs;
	}

	//CNC doesn't this need more filtering?

	@Override
	protected UserNoteEvent getNotificationType(NoteAddEvent event) {
		return UserNoteEvent.HELP_REQUEST_COMMENT;
	}

	@Override
	public String getEventHandlerName(NoteAddEvent event) {
		return "default.usereventhandler.helpdeskrequestnoteadd.issueowner";
	}
}

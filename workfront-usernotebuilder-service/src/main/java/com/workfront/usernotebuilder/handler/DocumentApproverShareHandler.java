package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.util.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * A handler for document shared events - specifically shares with approver.
 * Must collect document approval participants to be notified and filter on defaults.
 */
@Component("DOCUMENTAPPROVERSHARE")
public class DocumentApproverShareHandler extends AbstractApplicationEventUserNoteHandler<DocumentShareEvent> {
	public DocumentApproverShareHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public DocumentApproverShareHandler() {}

	@Override
	protected Collection<AppGUID> collect(DocumentShareEvent event, Collection<AppGUID> userIDs) throws AtTaskException {
		if(event.getTeamID() != null) {
			userIDs.add(CollectorUtil.generateAppGUID(Team.OBJCODE, event.getTeamID()));
		} else { //if isDocumentShare or isProofer or isApprover or isApproverAndProofer, add the user id
			userIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, event.getUserID()));
		}
		return userIDs;
	}

	@Override
	protected UserNoteEvent getNotificationType(DocumentShareEvent event) {
		UserNoteEvent eventType = UserNoteEvent.DOCUMENTAPPROVAL_ADD_USER;
		if(event.isProofer()) {
			eventType = UserNoteEvent.DOCUMENTAPPROVAL_ADD_USER_PROOFING;
		}
		return eventType;
	}

	@Override
	public String getEventHandlerName(DocumentShareEvent event) {
		String handlerName = "default.usereventhandler.documentapprovaladd.user";
		if(event.isProofer()) {
			handlerName =  "default.usereventhandler.documentapprovaladd.user.proofing";
		}
		return handlerName;
	}
}

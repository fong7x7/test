package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.filter.*;
import com.workfront.usernotebuilder.stereotype.*;
import com.workfront.usernotebuilder.util.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * A handler for document share events - delegates approver share events to {@link DocumentApproverShareHandler}
 * Must collect document approval participants to be notified and filter on defaults.
 */
@Component("DOCUMENTSHARE")
@Handler(UserNoteEventHandlerEnum.DOCUMENTSHARE)
@CommonsLog
public class DocumentSharedHandler extends AbstractApplicationEventUserNoteHandler<DocumentShareEvent> {
	@Autowired
	DocumentApproverShareHandler documentApproverShareHandler;

	public DocumentSharedHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public DocumentSharedHandler() {
		super();
		addFilter(new ActionUserFilter(new EditedByFilter()));
	}

	@Override
	public UserNoteSource handleEvent(DocumentShareEvent event, UserNoteSource outNotification) {
		if(event.isApprover()) {
			return documentApproverShareHandler.handleEvent(event, outNotification);
		}
		return super.handleEvent(event, outNotification);
	}

	//CNC this call is the same for DocumentApproverShareHandler, abstract common code.
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
		UserNoteEvent eventType = UserNoteEvent.DOCUMENTSHARE_ADD_USER;
		if(event.isProofer()) {
			eventType = UserNoteEvent.DOCUMENTSHARE_ADD_USER_PROOFING;
		} else if(event.getTeamID() != null ) {
			eventType = UserNoteEvent.DOCUMENTSHARE_ADD_TEAM;
		}
		return eventType;
	}

	@Override
	public String getEventHandlerName(DocumentShareEvent event) {
		String handlerName = "default.journaleventhandler.documentshareadd.user";
		if(event.isProofer()) {
			handlerName = "default.journaleventhandler.documentshareadd.user.proofing";
		} else if(event.getTeamID() != null ) {
			handlerName = "default.journaleventhandler.documentshareadd.team";
		}
		return handlerName;
	}
}

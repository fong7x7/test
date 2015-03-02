package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.stereotype.*;
import com.workfront.usernotebuilder.util.*;
import lombok.extern.apachecommons.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * A handler for document approval events.
 * Must collect document approval participants and filter on defaults.
 */
@Component("DOCUMENTAPPROVE")
@Handler(UserNoteEventHandlerEnum.DOCUMENTAPPROVE)
@CommonsLog
public class DocumentApproveEventHandler extends AbstractApplicationEventUserNoteHandler<DocumentApproveEvent> {
	public DocumentApproveEventHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public DocumentApproveEventHandler() {}

	@Override
	protected Collection<AppGUID> collect(final DocumentApproveEvent event, final Collection<AppGUID> userIDs) throws AtTaskException {
		String status = event.getStatus();
		if (status != null) {
			final DocumentApprovalStatus docApprovalStatus =
				DocumentApprovalStatus.valueOf(status);
			//Currently we will only find one id to notify...
			AppGUID guid = null;
			switch (docApprovalStatus) {
				case CANCELED:
					guid = CollectorUtil.generateAppGUID(User.OBJCODE, event.getApproverID());
					break;
				case APPROVED:
				case APPROVED_WITH_CHANGES:
				case REJECTED:
					guid = CollectorUtil.generateAppGUID(User.OBJCODE, event.getRequestorID());
					break;
				default:
					break;
			}
			if (null != guid) {
				userIDs.add(guid);
			}
		}
		return userIDs;
	}

	/* CNC currently using only base filter
	@Override
	protected Collection<AppGUID> filter(final DocumentApproveEvent event, final Collection<AppGUID> userIDs) throws AtTaskException {
		//No filters as of now
		return userIDs;
	}
	*/

	@Override
	protected UserNoteEvent getNotificationType(DocumentApproveEvent event) {
		final String status = event.getStatus();
		//CNC consider adding UserNoteEvent.UNKONWN
		//Pessimistic
		UserNoteEvent notificationType = null;
		// Or optimistic:
		//UserNoteEvent notificationType = UserNoteEvent.MESSAGE;
		try {
			final DocumentApprovalStatus docApprovalStatus = DocumentApprovalStatus.valueOf(status);
			switch (docApprovalStatus) {
				case APPROVED:
					notificationType = UserNoteEvent.DOCUMENTAPPROVAL_APPROVED;
					break;
				case APPROVED_WITH_CHANGES:
					notificationType = UserNoteEvent.DOCUMENTAPPROVAL_APPROVED_WITH_CHANGES;
					break;
				case CANCELED:
					notificationType = UserNoteEvent.DOCUMENTAPPROVAL_CANCELED;
					break;
				case REJECTED:
					notificationType = UserNoteEvent.DOCUMENTAPPROVAL_REJECTED;
					break;
				default:
					notificationType = null;
					break;
			}
		} catch (final Exception e) {
			log.error(
				"Failed to establish DocumentApprovalStatus from string: " + status, e);
		}
		return notificationType;
	}

	@Override
	public String getEventHandlerName(DocumentApproveEvent event) {
		final DocumentApprovalStatus docApprovalStatus = DocumentApprovalStatus.valueOf(event.getStatus());
		String handlerName = null;
		switch (docApprovalStatus) {
			case APPROVED:
				handlerName = "default.usereventhandler.documentapproval.approved";
				break;
			case APPROVED_WITH_CHANGES:
				handlerName = "default.usereventhandler.documentapproval.approvedwithchanges";
				break;
			case CANCELED:
				handlerName = "default.usereventhandler.documentapproval.canceled";
				break;
			case REJECTED:
				handlerName = "default.usereventhandler.documentapproval.rejected";
				break;
		}
		return handlerName;
	}
}

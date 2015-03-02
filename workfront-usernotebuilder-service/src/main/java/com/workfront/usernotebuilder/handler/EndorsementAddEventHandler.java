package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.stereotype.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * A handler for endorsement events.
 * Must collect endorsement receiver to be notified and filter on defaults.
 */
@Component("ENDORSEMENTADD")
@Handler(UserNoteEventHandlerEnum.ENDORSEMENTADD)
public class EndorsementAddEventHandler extends AbstractApplicationEventUserNoteHandler<EndorsementAddEvent> {
	public EndorsementAddEventHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public EndorsementAddEventHandler() {}

	@Override
	protected Collection<AppGUID> collect(final EndorsementAddEvent event, final Collection<AppGUID> collectIDs) throws AtTaskException {
		collectIDs.add(AppGUID.builder()
			.id(event.getReceiverID())
			.objCode(User.OBJCODE)
			.build());

		return collectIDs;
	}

	// There is no additional filtering at this point, so using abstract filter method

	/**
	 * Assign the given endorsement event type to the user notification
	 * Called in super.handleEvent before collection and filtering
	 * @param event the event to drive our note builder
	 * @param outNotification the current state of the new user note
	 * @return an updated User Note
	 */
	@Override
	protected UserNoteEvent getNotificationType(EndorsementAddEvent event) {
		return UserNoteEvent.ENDORSEMENT_ADD;
	}

	@Override
	public String getEventHandlerName(EndorsementAddEvent endorsementAddEvent) {
		return "default.endorsementeventhandler.endorsement.add";
	}
}

package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.stereotype.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * A handler for endorsement sharing events.
 * Must collect endorsement-share accessor to be notified and filter on defaults.
 */
@Component("ENDORSEMENTSHARED")
@Handler(UserNoteEventHandlerEnum.ENDORSEMENTSHARED)
//@Component("#{T(com.attask.usernotebuilder.config.UserNoteEventHandlerEnum).ENDORSEMENTSHARED}")
public class EndorsementSharedEventHandler extends AbstractApplicationEventUserNoteHandler<EndorsementShared> {
	public EndorsementSharedEventHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public EndorsementSharedEventHandler() {}

	@Override
	protected Collection<AppGUID> collect(final EndorsementShared event, final Collection<AppGUID> collectIDs) throws AtTaskException {
		//CNC use com.attask.biz.event.EndorsementShareUserCollector
		final String accessorObjCode = event.getAccessorObjCode();
		//Current only collecting the accessor ID
		collectIDs.add(AppGUID.builder()
			.id(event.getAccessorID())
			.objCode(accessorObjCode)
			.build());

		return collectIDs;
	}

	// There is no additional filtering at this point
	@Override
	protected Collection<AppGUID> filter(final EndorsementShared event, final Collection<AppGUID> filterIDs) {
		//CNC this is bypassing the default filters...
		return filterIDs;
	}

	/**
	 * Assign the endorsement-shared event type to the user notification source
	 * Called in super.handleEvent before collection and filtering
	 * @param event the event to drive our note builder
	 * @param outNotification the current state of the new user note
	 * @return an updated User Note
	 */
	@Override
	public UserNoteEvent getNotificationType(final EndorsementShared event) {
		return UserNoteEvent.ENDORSEMENT_SHARE;
	}

	@Override
	public String getEventHandlerName(EndorsementShared event) {
		return "default.endorsementshareeventhandler.endorsementshare.add";
	}
}

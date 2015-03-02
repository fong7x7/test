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
 * A handler for likes of endorsements. Generally delegate from {@link LikeEventHandler}.
 * Must collect entry recipients and tagged recipients and filter with
 * {@link com.workfront.usernotebuilder.filter.ActionUserFilter} & defaults...
 */
@Component("LIKEENDORSEMENT")
public class LikeEndorsementHandler extends AbstractApplicationEventUserNoteHandler<LikeEvent> {
	public LikeEndorsementHandler(ApiFactory factory) {
		this();
		this.apiFactory = factory;
	}

	public LikeEndorsementHandler() {
		super();
		addFilter(new ActionUserFilter());
	}

	/**
	 * Collect the endorsement based notifiable guids provided from the like event.
	 * -collect the endorser, endorsement receiver, and all endorsement shared users/teams-
	 * @param event the event to drive our note builder
	 * @param collectIDs the collection of guids that are being handled for notification for the user note
	 * @return an updated collection of the previous collectIDs
	 * @throws com.attask.common.AtTaskException
	 */
	@Override
	protected Collection<AppGUID> collect(LikeEvent event, Collection<AppGUID> collectIDs) throws AtTaskException {
		collectIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, event.getEndorserID()));
		collectIDs.add(CollectorUtil.generateAppGUID(User.OBJCODE, event.getEndorsementReceiverID()));
		if (event.getEndorsementSharedUserIDs() != null) {
			collectIDs.addAll(CollectorUtil.generateAppGUIDs(User.OBJCODE, event.getEndorsementSharedUserIDs()));
		}
		if (event.getEndorsementSharedTeamIDs() != null) {
			collectIDs.addAll(CollectorUtil.generateAppGUIDs(Team.OBJCODE, event.getEndorsementSharedTeamIDs()));
		}
		return collectIDs;
	}

	/**
	 * Filter the notifiable guids provided from the like event.
	 * Currently filters out the user who caused the like event so that user does not get notified.
	 * @param event the event to drive our note builder
	 * @param filterIDs the collection of guids that are being handled for notification for the user note
	 * @return a filtered collection of the previous filterIDs
	 * @throws com.attask.common.AtTaskException
	@Override
	protected Collection<AppGUID> filter(LikeEvent event, Collection<AppGUID> filterIDs) throws AtTaskException {
		Filter<AppGUID> filter = new ActionUserFilter(event);
		return FluentIterable.from(filterIDs).filter(filter.predicate()).toSet();
	}
	 */

	/**
	 * Assign the given like event type to the user notification
	 * Called in super.handleEvent before collection and filtering
	 * @param event the event to drive our note builder
	 * @param outNotification the current state of the new user note
	 * @return an updated User Note
	 */
	@Override
	protected UserNoteEvent getNotificationType(LikeEvent event) {
		return UserNoteEvent.LIKE_ENDORSER;
	}

	@Override
	public String getEventHandlerName(LikeEvent event) {
		return "default.likeeventhandler.like.endorser";
	}
}
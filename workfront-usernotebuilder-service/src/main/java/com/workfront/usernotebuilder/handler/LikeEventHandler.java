package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * Delegating event handler that handles like related events and routes them to
 * {@link LikeEndorsementHandler} or {@link LikeNoteHandler}
 */
@Component("LIKEADD")
@Handler(UserNoteEventHandlerEnum.LIKEADD)
public class LikeEventHandler extends AbstractApplicationEventUserNoteHandler<LikeEvent> {
	@Autowired private LikeEndorsementHandler likeEndorsementHandler;
	@Autowired private LikeNoteHandler likeNoteHandler;

	public LikeEventHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public LikeEventHandler() {}

	/**
	 * Handles the given like event type to update the user notification
	 * @param event the event to drive our note builder
	 * @param outNotification the current state of the new user note
	 * @return an updated User Note
	 */
	@Override
	public UserNoteSource handleEvent(LikeEvent event, UserNoteSource outNotification) {
		if(event.getEndorsementID() != null) {
			return likeEndorsementHandler.handleEvent(event, outNotification);
		} else if(event.getNoteOwnerID() != null) {
			return likeNoteHandler.handleEvent(event, outNotification);
		}
		return super.handleEvent(event, outNotification);
	}

	//CNC noop
	@Override
	protected Collection<AppGUID> collect(final LikeEvent event, final Collection<AppGUID> collectIDs) throws AtTaskException {
		return collectIDs;
	}

	@Override
	protected UserNoteEvent getNotificationType(LikeEvent event) {
		//CNC review correctness of this type
		return UserNoteEvent.LIKE_NOTE;
	}

	@Override
	public String getEventHandlerName(LikeEvent event) {
		return "default.likeeventhandler.like.add";
	}
}
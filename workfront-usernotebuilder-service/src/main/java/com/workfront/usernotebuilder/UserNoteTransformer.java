package com.workfront.usernotebuilder;

import com.attask.event.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.handler.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.integration.transformer.*;
import org.springframework.stereotype.*;

/**
 * A custom message transformer that converts {@link com.workfront.usernotebuilder.event.UserNoteBuilderEvent}
 * into {@link com.workfront.usernotebuilder.event.UserNoteSource} via
 * {@link com.workfront.usernotebuilder.handler.ApplicationEventHandler} invocation.
 * The resulting UserNoteSource object is used by the {@link UserNoteServiceHandler} to
 * produce user notifications.
 */
@Component
@CommonsLog
public class UserNoteTransformer implements GenericTransformer<UserNoteBuilderEvent, UserNoteSource> {
	@Autowired
	private EventHandlerFactory eventHandlerFactory;

	/**
	 * Create a new {@link com.workfront.usernotebuilder.event.UserNoteSource} using
	 * the given {@link com.workfront.usernotebuilder.event.UserNoteBuilderEvent}
	 * @param incoming the event whose properties will drive the creation of a new UserNoteSource
	 * @return a new {@link com.workfront.usernotebuilder.event.UserNoteSource}
	 */
	@Override
	public UserNoteSource transform(final UserNoteBuilderEvent incoming) {
		log.debug(String.format("Transforming message from RBQ: %s", incoming));
		final String targetObjCode = incoming.getTargetObjCode();

		final UserNoteSource builderNote = UserNoteSource.builder()
			.customerID(incoming.getCustomerID())
			.userNotableObjCode(targetObjCode)
			.build();

		// Though we know the generic type of the app event, our factory needs to lookup using the class.
		final AbstractApplicationEvent appEvent = incoming.getPayload();
		final ApplicationEventHandler<AbstractApplicationEvent, UserNoteSource> eventHandler =
			eventHandlerFactory.getEventHandler(UserNoteEventHandlerEnum.valueOf(appEvent.getClass()));

		return eventHandler.handleEvent(appEvent, builderNote);
	}
}

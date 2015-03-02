package com.workfront.usernotebuilder;

import com.attask.component.api.*;
import com.attask.sdk.api.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.attask.sdk.services.internal.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.util.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.actuate.metrics.*;
import org.springframework.integration.dsl.support.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * The user-note builder handler responsible for generating the user notifications requests.
 * Currently, this handler will call into the remote {@link com.attask.sdk.services.internal.UserNoteService}
 * using AddSupported and specialized bulk-operation endpoints to orchestrate the creation of
 * User Notifications.
 * <p>This handler acts as a MessageHandler in an IntegrationFlow chain.
 */
@Component
@CommonsLog
public class UserNoteServiceHandler implements GenericHandler<UserNoteSource> {
	private final ApiFactory apiFactory;

	@Autowired
	private CounterService counterService;

	@Autowired
	public UserNoteServiceHandler(ApiFactory apiFactory) {
		this.apiFactory = apiFactory;
	}

	/**
	 * Message handler stage method intended for fluent invocations via message integration flows.
	 * @param payload the message content
	 * @param headers the SI message headers
	 * @return the processed UserNote IDs
	 */
	@Override
	public Object handle(final UserNoteSource payload, final Map<String, Object> headers) {
		//CNC payload null checks?
		//CNC REMOVE!
		log.debug("Found payload: " + payload.toString());
		Collection<String> createdNotes = Collections.emptyList();
		String cid = payload.getCustomerID();
		try {
			AppGUID customerID = AppGUID.builder().id(cid).objCode(Customer.OBJCODE).build();
			createdNotes = addUserNotes(customerID, payload);
			if (null == createdNotes || createdNotes.isEmpty()) {
				log.error("Failed to create a new user note.");
				//CNC fix. Improved error logging?
			}
			incrementCount();
		} catch (final Exception e) {
			log.error("Failed to create a new user note from payload.", e);
			//CNC fix. Improved error logging?
		}
		return createdNotes;
	}

	/**
	 * Increment the notification build count metric
	 */
	private void incrementCount() {
		counterService.increment("services.notification.builder.built");
	}

	/**
	 * Call into the WorkFront SDK API {@link com.attask.sdk.services.internal.UserNoteService}
	 * to create user notifications from the provided {@link com.workfront.usernotebuilder.event.UserNoteSource}
	 * @param customerID the customer GUID to use for retrieving an API service reference
	 * @param payload the source for generated notifications
	 * @return the processed UserNote IDs
	 */
	//CNC add throws... this call should be migrated into an adapter
	private Collection<String> addUserNotes(final AppGUID customerID, final UserNoteSource payload) {
		//CNC how to get better re-use on APIFactory?
		final API adminApi = apiFactory.getCustomerAdminApi(customerID.getId());
		final ImmutableSet.Builder<String> outBuilder = ImmutableSet.builder();
		if (null != adminApi) {
			final UserNoteService apiService =
				(UserNoteService) adminApi.getAPIService(UserNote.OBJCODE);

			outBuilder.addAll(
				notify(apiService, payload, User.OBJCODE));

			outBuilder.addAll(
				notify(apiService, payload, Team.OBJCODE));
		}
		final ImmutableSet<String> outIDs = outBuilder.build();
		log.debug(outIDs.size() + " user-notes generated");
		return outIDs;
	}

	private Collection<String> notify(
		final UserNoteService apiService, final UserNoteSource payload, final String objCode)
	{
		Collection<String> builtIDs = Collections.emptyList();
		final Collection<String> ids = CollectorUtil.collectIDs(payload.getNotifyGUIDs(), objCode);
		if (! ids.isEmpty()) {
			final String userNotableObjCode = payload.getUserNotableObjCode();
			final String userNotableID = payload.getUserNotableID();
			final UserNoteEvent type = payload.getEventType();
			switch (objCode) {
				case Team.OBJCODE:
					// This should only be used for team-granular filtered notifications
					builtIDs = apiService.addTeamsNotes(
						ids, userNotableObjCode, userNotableID, type);

					break;
				case User.OBJCODE:
					builtIDs = apiService.addUsersNotes(
						ids, userNotableObjCode, userNotableID, type);

					break;
				default:
					break;
			}
		}
		return builtIDs;
	}
}

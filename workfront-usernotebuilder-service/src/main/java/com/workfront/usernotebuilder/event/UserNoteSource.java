package com.workfront.usernotebuilder.event;

import com.attask.common.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import lombok.*;
import lombok.extern.apachecommons.*;

import java.util.*;

/**
 * A service-event intermediary object for the builder to use while generating user-notifications.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@CommonsLog
public class UserNoteSource extends UserNote {
	private final Collection<AppGUID> notifyGUIDs = new HashSet<>();

	//CNC remove...
	private UserNoteSource() {}

	@Builder
	private UserNoteSource(
		String ID, String customerID, String userNotableID,
		String userNotableObjCode,
		UserNoteEvent eventType,
		Collection<AppGUID> notifyGUIDs)
	{
		super();
		setID(ID);
		setCustomerID(customerID);
		setEventType(eventType);
		setUserNotableID(userNotableID);
		setUserNotableObjCode(userNotableObjCode);
		if (null != notifyGUIDs && ! notifyGUIDs.isEmpty()) {
			this.notifyGUIDs.addAll(notifyGUIDs);
		}
	}

	@Override
	public void setUserID(String userID) {
		addNotifyID(userID, User.OBJCODE);
		super.setUserID(userID);
	}

	public void addNotifyIDs(Collection<AppGUID> guids) {
		notifyGUIDs.addAll(guids);
	}

	public void addNotifyID(AppGUID guid) {
		notifyGUIDs.add(guid);
	}

	public void addNotifyID(String id, String objCode) {
		try {
			final AppGUID guid = AppGUID.builder().id(id).objCode(objCode).build();
			notifyGUIDs.add(guid);
		} catch (AtTaskException e) {
			log.error("Failed to add ID for user notification: ", e);
			//CNC report
		}
	}
}

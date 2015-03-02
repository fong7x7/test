package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.attask.sdk.api.*;
import com.attask.sdk.model.internal.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.handler.*;
import lombok.*;

import javax.annotation.*;
import java.util.*;

public class UserOptOutFilter extends AbstractEventFilter<AppGUID, ApplicationObjectEvent> {
	private static final String INACTIVE_NOTIFICATIONS = "inactiveNotifications";

	private AbstractApplicationEventUserNoteHandler handler;

	public UserOptOutFilter(AbstractApplicationEventUserNoteHandler handler) {
		this.handler = handler;
	}

	public UserOptOutFilter(AbstractApplicationEventUserNoteHandler handler, Filter<AppGUID> anotherFilter) {
		super(anotherFilter);
		this.handler = handler;
	}

	@Override
	public Predicate<AppGUID> getBasePredicate(final Optional<ApplicationObjectEvent> optional) {
		if (optional.isPresent()) {
			final ApplicationObjectEvent event = optional.get();
			return withEvent(event);
		}
		return Predicates.notNull();
	}

	private Predicate<AppGUID> withEvent(@NonNull final ApplicationObjectEvent event) {
		return new Predicate<AppGUID>() {
			@Override
			public boolean apply(@Nullable AppGUID appGUID) {
				if (!User.OBJCODE.equals(appGUID.getObjCode())) { return true; }
				return !getOptedOutNotifications(appGUID).contains(handler.getEventHandlerName(event));
			}
		};
	}

	private List<String> getOptedOutNotifications(final AppGUID appGUID) {
		final API adminAPI = handler.getApiFactory().getAspApi();
		List<String> fields = ImmutableList.<String>builder()
			.add("userPrefValues")
			.add("userPrefValues:name")
			.add("userPrefValues:value")
			.build();
		User user = (User)adminAPI.get(User.OBJCODE, appGUID.getId(), fields).getData();
		UserPrefValue userPref = getOptedOutNotificationUserPref(user.getUserPrefValues());
		return parseNotifications(userPref);
	}

	private UserPrefValue getOptedOutNotificationUserPref(List<UserPrefValue> prefValues) {
		if(prefValues != null) {
			for (UserPrefValue value : prefValues) {
				if (INACTIVE_NOTIFICATIONS.equals(value.getName())) {
					return value;
				}
			}
		}
		return null;
	}

	private List<String> parseNotifications(UserPrefValue userPref) {
		String[] optedOutNotifications = new String[0];
		if(userPref != null && userPref.getValue() != null) {
			optedOutNotifications = userPref.getValue().split(",");
		}
		return Arrays.asList(optedOutNotifications);
	}
}
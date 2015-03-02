package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.attask.sdk.api.*;
import com.attask.sdk.model.internal.*;
import com.google.common.base.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.handler.*;

import javax.annotation.*;
import java.util.*;

/**
 * Filter based on the existence of a user accesslevel...
 */
public class UserAccessLevelFilter extends AbstractEventFilter<AppGUID, AbstractApplicationEvent> {
	private AbstractApplicationEventUserNoteHandler handler;

	public UserAccessLevelFilter(AbstractApplicationEventUserNoteHandler handler) {
		this.handler = handler;
	}

	public UserAccessLevelFilter(AbstractApplicationEventUserNoteHandler handler, Filter<AppGUID> anotherFilter) {
		super(anotherFilter);
		this.handler = handler;
	}

	@Override
	public Predicate<AppGUID> getBasePredicate(final Optional<AbstractApplicationEvent> optional) {
		return new Predicate<AppGUID>() {
			@Override
			public boolean apply(@Nullable AppGUID appGUID) {
				final API api = handler.getApiFactory().getAspApi();
				User user = (User)api.get(User.OBJCODE, appGUID.getId(), Collections.singletonList("accessLevelGUID")).getData();
				return user.getAccessLevelID() != null;
			}
		};
	}
}

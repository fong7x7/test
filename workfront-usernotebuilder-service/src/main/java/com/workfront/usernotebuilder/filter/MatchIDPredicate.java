package com.workfront.usernotebuilder.filter;

import com.google.common.base.*;
import com.workfront.usernotebuilder.event.*;
import lombok.*;

/**
 * A simple predicate that will match any {@link com.workfront.usernotebuilder.event.AppGUID}
 * ID against the provided one.
 */
@EqualsAndHashCode
public class MatchIDPredicate implements Predicate<AppGUID> {
	/**
	 * The GUID string for the actor that generated the app event.
	 * Typically the ID of a user that performed some action which triggers notifications.
	 */
	private final String matchingID;

	public MatchIDPredicate(String id) {
		this.matchingID = id;
	}

	@Override
	public boolean apply(AppGUID input) {
		return matchingID.equals(input.getId());
	}
}

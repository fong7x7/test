package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.google.common.base.*;
import com.workfront.usernotebuilder.event.*;

/**
 * Evaluate {@link com.workfront.usernotebuilder.event.AppGUID}s that correspond
 * with recipients that are either not tagged on the event reference object,
 * or do not have sufficient access level to audit the notification.
 */
public class PrivateUpdateUserFilter extends AbstractEventFilter<AppGUID, NoteAddEvent> {
	public Predicate<AppGUID> getBasePredicate(final Optional<NoteAddEvent> optional) {
		if (optional.isPresent()) {
			final NoteAddEvent event = optional.get();
			return new Predicate<AppGUID>() {
				@Override
				public boolean apply(AppGUID input) {
					boolean isPrivate = event.isPrivate() || event.isParentPrivate();
					if (isPrivate) {
						/* CNC implement this filter
						return <user>.getAccessLevelObject().isAdmin()
							|| event.getNoteTagUserIDs().contains(guid.getId())
							//|| lucid/legacy security model check all filters
							;
						*/
					}
					return true;
				}
			};
		}
		return Predicates.notNull();
	}
}

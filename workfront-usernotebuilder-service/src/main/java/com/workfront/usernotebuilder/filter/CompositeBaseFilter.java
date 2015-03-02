package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.google.common.base.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.handler.*;

/**
 * A composite filter using predicates from {@link UserAccessLevelFilter} and {@link UserOptOutFilter}
 * This combined filter acts as the base filter for many of our event handlers.
 */
public class CompositeBaseFilter extends AbstractEventFilter<AppGUID, AbstractApplicationEvent> {
	public CompositeBaseFilter(AbstractApplicationEventUserNoteHandler handler) {
		chain(new UserOptOutFilter(handler));
		chain(new UserAccessLevelFilter(handler));
	}

	public CompositeBaseFilter(AbstractApplicationEventUserNoteHandler handler, final Filter<AppGUID> anotherFilter) {
		this(handler);
		chain(anotherFilter);
	}

	@Override
	public Predicate<AppGUID> getBasePredicate(Optional<AbstractApplicationEvent> abstractApplicationEventOptional) {
		return Predicates.notNull();
	}
}
package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.google.common.base.*;
import com.workfront.usernotebuilder.event.*;
import lombok.*;

/**
 * Filter out {@link com.workfront.usernotebuilder.event.AppGUID}s that correspond
 * with recipients matching the event transactionUserID.
 * <p>Ostensibly this would remove those recipients that generated the notification
 * source.
 */
public class ActionUserFilter extends AbstractEventFilter<AppGUID, AbstractApplicationEvent> {
	public ActionUserFilter() {}

	public ActionUserFilter(Filter<AppGUID> anotherFilter) {
		super(anotherFilter);
	}

	@Override
	public Predicate<AppGUID> getBasePredicate(Optional<AbstractApplicationEvent> optional) {
		if (optional.isPresent()) {
			final AbstractApplicationEvent event = optional.get();
			final String transactionUserID = event.getTransactionUserID();
			if (AppGUID.isGUID(transactionUserID)) {
				return Predicates.not(new MatchIDPredicate(transactionUserID));
			}
		}
		return Predicates.notNull();
	}
}
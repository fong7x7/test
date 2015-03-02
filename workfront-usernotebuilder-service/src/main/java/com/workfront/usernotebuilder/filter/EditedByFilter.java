package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.google.common.base.*;
import com.workfront.usernotebuilder.event.*;
import lombok.*;


public class EditedByFilter extends AbstractEventFilter<AppGUID, DocumentShareEvent> {
	@Override
	public Predicate<AppGUID> getBasePredicate(final Optional<DocumentShareEvent> optional) {
		if (optional.isPresent()) {
			final DocumentShareEvent event = optional.get();
			final String editedByID = event.getEditedByID();
			if (AppGUID.isGUID(editedByID)) {
				return Predicates.not(new MatchIDPredicate(editedByID));
			}
		}
		return Predicates.notNull();
	}
}

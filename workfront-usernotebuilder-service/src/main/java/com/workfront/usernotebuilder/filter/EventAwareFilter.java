package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.google.common.base.*;

/**
 * Extend {@link Filter} to incorporate conditional objects that wrap application events.
 * Relies on {@link com.google.common.base.Optional} to manage nullable references.
 */
public interface EventAwareFilter<T, V extends AbstractApplicationEvent> extends Filter<T> {
	//CNC consider if we need other conditional parameters for predicate beyond the event itself.
	public Predicate<T> getBasePredicate(final Optional<V> conditional);
}

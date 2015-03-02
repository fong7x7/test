package com.workfront.usernotebuilder.filter;

import com.attask.event.*;
import com.google.common.base.*;

/**
 * Abstract base class for filtering with conditional properties of application events.
 * This means that the filter predicates require some data in the event in order to function.
 */
public abstract class AbstractEventFilter<T, V extends AbstractApplicationEvent>
	extends AbstractFilter<T> implements EventAwareFilter<T, V>
{
	public AbstractEventFilter() {}

	public AbstractEventFilter(final Filter<T> anotherFilter) {
		//CNC I would prefer to use static builders for this
		super(anotherFilter);
	}

	@Override
	public Predicate<T> getBasePredicate(Object conditional) {
		if(conditional instanceof Optional) {
			return getBasePredicate((Optional) conditional);
		}
		return Predicates.notNull();
	}
	
	/* CNC remove this method @Synchronized
	private Collection<Filter<T>> buildChain(Filter<T> anotherFilter) {
		return chainFilters.isEmpty() ? ImmutableSet.of(anotherFilter) :
			//CNC consider how much ordering of the applied filters will matter
			ImmutableSet.<Filter<T>>builder().addAll(chainFilters).add(anotherFilter).build();
	}
	*/

}

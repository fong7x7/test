package com.workfront.usernotebuilder.filter;

import com.google.common.base.*;
import lombok.Synchronized;

import java.util.*;

/**
 * A base type for all implemented filters. Supplies the default
 * {@link Filter.PredicatePolicy} (PredicatePolicy.AND)
 * & default {@link com.google.common.base.Predicate} (ObjectPredicate.NOT_NULL)
 * <p>
 *     Moreover this abstraction supplies the basic filter chaining strategy. A filter object
 *     can be added to the internal synchronized collection and that filter will be evaluated
 *     with any conditional objects at runtime on calls to {@link #baseRule}
 * </p>
 */
public abstract class AbstractFilter<T> implements Filter<T> {
	// Consider immutable sets, or Sets.newConcurrentHashSet()
	protected Collection<Filter<T>> chainFilters = Collections.<Filter<T>>synchronizedSet(
		new LinkedHashSet<Filter<T>>());

	protected PredicatePolicy defaultPolicy = PredicatePolicy.AND;

	public AbstractFilter() {
		this.defaultPolicy = PredicatePolicy.AND;
		// Consider Predicates.alwaysFalse()
	}

	public AbstractFilter(final Filter<T> anotherFilter) {
		this();
		chainFilters.add(anotherFilter);
	}

	abstract public Predicate<T> getBasePredicate(final Object conditional);

	@Override
	final public Predicate<T> predicate(final Object conditional) {
		Predicate<T> appliedPredicate = getBasePredicate(conditional);
		// chainFilters is a synchronized collection
		synchronized(chainFilters) {
			if (! chainFilters.isEmpty()) {
				//CNC consider ordering of the applied filters - look at LinkedHashSet
				for (final Filter<T> chainFilter : chainFilters) {
					// Note, we never introspect child filters to determine if they contain redundant predicates
					final Predicate<T> predicate = chainFilter.predicate(conditional);
					appliedPredicate = append(appliedPredicate, predicate, defaultPolicy);
				}
			}
		}
		return appliedPredicate;
	}

	@Override
	final public Filter<T> chain(final Filter<T> anotherFilter) {
		//chainFilters = buildChain(anotherFilter);
		synchronized(chainFilters) {
			chainFilters.add(anotherFilter);
		}
		return this;
	}

	/**
	 * Convenience method for
	 * {@link #append(com.google.common.base.Predicate, Filter.PredicatePolicy)}
	 * that will supply this filter's default {@link Filter.PredicatePolicy}
	 * @param conditional the conditional object necessary for this filter's base predicate
	 * @param inPredicate the predicate object to join to this filter's existing predicate
	 * @return this filter
	 */
	final public Predicate<T> append(Object conditional, final Predicate<T> inPredicate) {
		return append(conditional, inPredicate, defaultPolicy);
	}

	@Synchronized
	final public Predicate<T> append(Object conditional, final Predicate<T> inPredicate, final PredicatePolicy policy) {
		return append(getBasePredicate(conditional), inPredicate, policy);
	}

	/**
	 * Append the provided predicate rules using the specified policy.
	 * @param baseRule the first rule
	 * @param addRule the rule to combine with the first rule
	 * @param policy the policy that dictates the rule combination strategy
	 * @return a new rule that expresses a combination of the provided rules according to the specified policy.
	 */
	final public Predicate<T> append(
		final Predicate<T> baseRule, final Predicate<T> addRule, final PredicatePolicy policy)
	{
		// Results in a new predicate that joins the provided rules by the policy
		return policy.apply(addRule, baseRule);
	}
}

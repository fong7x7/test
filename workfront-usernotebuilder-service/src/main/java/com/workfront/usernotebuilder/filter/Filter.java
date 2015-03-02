package com.workfront.usernotebuilder.filter;

import com.google.common.base.*;

/**
 * A Filter will apply a series of {@link com.google.common.base.Predicate} stages against a supplied object to
 * determine pass/fail evaluation of some declarative rule.
 * <p>Filters are used extensively by the handler components, see
 * {@link com.workfront.usernotebuilder.handler.AbstractApplicationEventUserNoteHandler}
 */
public interface Filter<T> {
	//CNC consider if we need other conditional parameters for predicate beyond the event itself.
	public Predicate<T> predicate(final Object conditional);

	/**
	 * Appends another filter's predicate with this filter's predicate. The actual predicate join will be
	 * directed by the {@link Filter.PredicatePolicy} and the supplied
	 * conditional object on calls to {@link #predicate}.
	 * @param anotherFilter the filter whose predicate will be appended with this filter's predicate
	 * @return this filter with chained anotherFilter
	 *
	 * @see #append(com.google.common.base.Predicate, Filter.PredicatePolicy)
	 */
	public Filter<T> chain(final Filter<T> anotherFilter);

	/**
	 * Shortcut to append predicates that require no conditional object. The supplied predicate
	 * will immediately mutate the predicate logic of the concrete filter.
	 * @param inPredicate the predicate object to join to this filter's existing predicate
	 * @param policy the predicate joining policy, see {@link Filter.PredicatePolicy}
	 * @return this filter
	 */
	public Predicate<T> append(Object conditional, final Predicate<T> inPredicate, final PredicatePolicy policy);

	/**
	 * Append the provided predicate rules using the specified policy.
	 * @param baseRule the first rule
	 * @param addRule the rule to combine with the first rule
	 * @param policy the policy that dictates the rule combination strategy
	 * @return a new rule that expresses a combination of the provided rules according to the specified policy.
	 */
	public Predicate<T> append(final Predicate<T> baseRule, final Predicate<T> addRule, final PredicatePolicy policy);

	//CNC extract this

	/**
	 * The {@link com.google.common.base.Predicate} joining policy to be used for {@link #append} operations
	 */
	enum PredicatePolicy {
		/**
		 * @see com.google.common.base.Predicates#and(com.google.common.base.Predicate, com.google.common.base.Predicate)
		 */
		AND {
			@Override
			public Predicate apply(Predicate inPredicate, Predicate predicate) {
				return Predicates.and(predicate, inPredicate);
			}
		}
		/**
		 * @see com.google.common.base.Predicates#or(com.google.common.base.Predicate, com.google.common.base.Predicate)
		 */
		,OR {
			@Override
			public Predicate apply(Predicate inPredicate, Predicate predicate) {
				return Predicates.or(predicate, inPredicate);
			}
		}
		/**
		 * @see com.google.common.base.Predicates#not(com.google.common.base.Predicate)
		 */
		,NOT {
			@Override
			public Predicate apply(Predicate inPredicate, Predicate predicate) {
				return Predicates.and(predicate, Predicates.not(inPredicate));
			}
		};

		/**
		 * Apply this predicate joining rule to the given predicates.
		 * @param inPredicate the predicate to append
		 * @param predicate the base predicate
		 * @return a new {@link com.google.common.base.Predicate} that applies
		 *   both declarative evaluations according to the chosen policy.
		 */
		public abstract Predicate apply(Predicate inPredicate, Predicate predicate);
	}
}

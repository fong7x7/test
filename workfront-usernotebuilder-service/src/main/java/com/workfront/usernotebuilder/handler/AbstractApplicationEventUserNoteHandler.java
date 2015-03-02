package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.filter.*;
import org.apache.commons.logging.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

/**
 * Base-type for our event-handlers. Responsible for three stages of handling:
 * <li> Condition: handling is conditional upon some rule.</li>
 * <li> Collector: handling will incorporate an appropriate recipients list.</li>
 * <li> Filter: handling will apply rules to filter recipients.</li>
 * Concrete event handlers will be returned from
 * {@link com.workfront.usernotebuilder.EventHandlerFactory}
 */
public abstract class AbstractApplicationEventUserNoteHandler<P extends ApplicationObjectEvent>
	implements ApplicationEventHandler<P, UserNoteSource>
{
	private static final Log LOG = LogFactory.getLog(AbstractApplicationEventUserNoteHandler.class);

	@Autowired
	protected ApiFactory apiFactory;

	protected Filter<AppGUID> filter;

	public AbstractApplicationEventUserNoteHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public AbstractApplicationEventUserNoteHandler() {
		this.filter = new CompositeBaseFilter(this);
	}

	@Override
	public UserNoteSource handleEvent(final P event, UserNoteSource outNotification) {
		outNotification.setEventType(getNotificationType(event));
		outNotification.setUserNotableID(event.getId());
		Collection<AppGUID> notifyIDs = Collections.emptySet();

		try {
			notifyIDs = filter(event, collect(event, new HashSet<AppGUID>()));
		} catch (final AtTaskException e) {
			LOG.error("Failed to handle app-event of type: " + event.getEventType(), e);
		}
		outNotification.addNotifyIDs(notifyIDs);
		return outNotification;
	}

	protected void addFilter(final Filter<AppGUID> anotherFilter) {
		this.filter.chain(anotherFilter);
	}

	/**
	 * Filter the notifiable guids provided from the event.
	 * {@link com.workfront.usernotebuilder.filter.CompositeBaseFilter} is applied by default
	 * @param event the event to drive our note builder
	 * @param filterIDs the collection of guids being handled for notification
	 * @return a filtered collection of the previous filterIDs
	 * @throws com.attask.common.AtTaskException
	 */
	protected Collection<AppGUID> filter(
		final P event, Collection<AppGUID> filterIDs) throws AtTaskException
	{
		return FluentIterable.from(filterIDs)
			.filter(
				filter.predicate(Optional.fromNullable(event)))
			.toSet();
	}

	protected abstract Collection<AppGUID> collect(
		final P event, Collection<AppGUID> collectIDs) throws AtTaskException;

	/**
	 * Used to get the appropriate event type to the user notification
	 * Called in the super constructor before collection and filtering
	 * This serves as a contract to always define the event type for the user note
	 * Part of the condition stage.
	 * @param event the event to drive our note builder
	 * @return the notification type
	 */
	protected abstract UserNoteEvent getNotificationType(final P event);

	public abstract String getEventHandlerName(final P event);

	public ApiFactory getApiFactory() {
		return apiFactory;
	}
}

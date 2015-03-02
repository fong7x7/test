package com.workfront.usernotebuilder.handler;

import com.attask.event.*;

/**
 * Event-handler interface. Handlers consume application-events and perform some set of
 * processing functions prescribed by the event data structure, the handler requirements
 * and the associated business rules. Common functions include:
 * <li> Condition: handling is conditional upon some rule.</li>
 * <li> Collector: handling will incorporate an appropriate recipients list.</li>
 * <li> Filter: handling will apply rules to filter recipients.</li>
 * Concrete event handlers will be returned from
 * {@link com.workfront.usernotebuilder.EventHandlerFactory}
 * @see AbstractApplicationEventUserNoteHandler
 */
public interface ApplicationEventHandler<P extends AbstractApplicationEvent, T> {
	T handleEvent(P event, T data);
}

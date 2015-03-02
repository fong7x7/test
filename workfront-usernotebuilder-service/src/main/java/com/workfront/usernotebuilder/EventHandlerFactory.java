package com.workfront.usernotebuilder;


import com.attask.event.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.handler.*;

/**
 * A service-locator factory interface to be implemented as a dynamic proxy from
 * {@link org.springframework.beans.factory.config.ServiceLocatorFactoryBean}
 */
public interface EventHandlerFactory {
	/**
	 * Locate an event-handler registered with respect to the given enum constant
	 * @param name the managed constant to match
	 * @return the event handler
	 */
	public <T extends AbstractApplicationEvent> ApplicationEventHandler<T, UserNoteSource>
		getEventHandler(UserNoteEventHandlerEnum name);
}

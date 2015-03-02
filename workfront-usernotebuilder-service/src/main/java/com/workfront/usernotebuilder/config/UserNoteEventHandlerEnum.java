package com.workfront.usernotebuilder.config;

import com.attask.event.*;
import com.google.common.collect.*;

import java.util.*;

/**
 * Used to manage event-handler references.
 * @see com.workfront.usernotebuilder.EventHandlerFactory
 */
public enum UserNoteEventHandlerEnum {
	//CNC we could also consider using the bean name ("endorsementSharedEventHandler") instead of class...
	ANNOUNCEMENT(AnnouncementAddEvent.class)
	,DOCUMENTAPPROVE(DocumentApproveEvent.class)
	,DOCUMENTSHARE(DocumentShareEvent.class)
	,ENDORSEMENTADD(EndorsementAddEvent.class)
	,ENDORSEMENTSHARED(EndorsementShared.class)
	,LIKEADD(LikeEvent.class)
	,NOTEADD(NoteAddEvent.class)
	,WORKREQUEST(WorkRequestEvent.class)
	;

	private final Class<? extends AbstractApplicationEvent> eventClass;

	private static final Set<UserNoteEventHandlerEnum> handlerEnums = EnumSet.allOf(UserNoteEventHandlerEnum.class);

	private static final Map<Class<? extends AbstractApplicationEvent>, UserNoteEventHandlerEnum> enumByEventMap;

	static {
		final ImmutableMap.Builder<Class<? extends AbstractApplicationEvent>, UserNoteEventHandlerEnum> builder =
			ImmutableMap.builder();

		for (UserNoteEventHandlerEnum handlerEnum : handlerEnums) {
			builder.put(handlerEnum.getEventClass(), handlerEnum);
		}
		enumByEventMap = builder.build();
	}

	private UserNoteEventHandlerEnum(
		final Class<? extends AbstractApplicationEvent> eventClass)
	{
		this.eventClass = eventClass;
	}

	public Class<? extends AbstractApplicationEvent> getEventClass() {
		return this.eventClass;
	}

	//CNC remove

	/**
	 * If handlers do not manage derived events, but only a single event type,
	 * this may be a better solution.
	 * @param eventClass
	 * @return
	 */
	public static <T extends AbstractApplicationEvent> UserNoteEventHandlerEnum fastValueOf(
		Class<T> eventClass)
	{
		return enumByEventMap.get(eventClass);
	}

	/**
	 * Evaluate a matching enum instance for the provided application event class.
	 * Determines if the a registered event class is assignable from the given one.
	 * This is more reliable in a sub-typing scenario...
	 * @param eventClass the event type to match against
	 * @return a matching UserNoteEventHandlerEnum type
	 */
	public static <T extends AbstractApplicationEvent> UserNoteEventHandlerEnum valueOf(
		Class<T> eventClass)
	{
		final UserNoteEventHandlerEnum fastEnum = fastValueOf(eventClass);
		if (null == fastEnum) {
			return findInstanceForEvent(eventClass);
		}
		return fastEnum;
	}

	private static <T extends AbstractApplicationEvent> UserNoteEventHandlerEnum findInstanceForEvent(Class<T> eventClass) {
		for (final UserNoteEventHandlerEnum eventHandlerEnum : UserNoteEventHandlerEnum.values()) {
			if (eventHandlerEnum.getEventClass().isAssignableFrom(eventClass)) {
				return eventHandlerEnum;
			}
		}
		throw new IllegalArgumentException(
			"No matching enum type found for event class: " + eventClass.getCanonicalName());
	}
}

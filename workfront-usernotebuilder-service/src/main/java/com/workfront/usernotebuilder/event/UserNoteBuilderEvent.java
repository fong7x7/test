package com.workfront.usernotebuilder.event;

import com.attask.event.*;
import lombok.*;

//CNC deprecate and remove
/**
 * Represents a user note builder service event. This event is used to decouple the JMS
 * application event from our service logic.
 * Wraps an {@link com.attask.event.AbstractApplicationEvent} with service-relevant data.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserNoteBuilderEvent<T extends AbstractApplicationEvent> extends ServiceEvent<T> {
    private static final long serialVersionUID = -1L;

    private String targetObjCode;

    public UserNoteBuilderEvent() {}

    @Builder
    public UserNoteBuilderEvent(
        Class<T> eventClass, T payload, String customerID, String targetObjCode)
    {
        super(eventClass, payload, customerID);
        this.targetObjCode = targetObjCode;
    }
}

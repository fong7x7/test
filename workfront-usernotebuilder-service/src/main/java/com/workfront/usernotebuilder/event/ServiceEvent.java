package com.workfront.usernotebuilder.event;

import com.attask.event.*;
import lombok.*;

import java.io.*;

/**
 * Represents an event for a service-specific consumer.
 * Provide any service-event specific enrichment in derived types.
 * @see UserNoteBuilderEvent
 */
@Data
@AllArgsConstructor
public abstract class ServiceEvent<T extends AbstractApplicationEvent> implements Serializable {
    private static final long serialVersionUID = -1L;

    protected Class<T> eventClass;
    // The application event - consider using more abstraction
    protected T payload;
    protected String customerID;

    public ServiceEvent() {}
}

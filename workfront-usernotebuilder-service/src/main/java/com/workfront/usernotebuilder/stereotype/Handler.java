package com.workfront.usernotebuilder.stereotype;

import com.workfront.usernotebuilder.config.*;
import org.springframework.beans.factory.annotation.*;
import java.lang.annotation.*;

/**
 * A custom qualifier to retrieve handlers by an associated enum constant
 *
 * CNC consider adding @Component here with custom (SpEL) bean resolver...
 */
@Target({ElementType.FIELD,
	ElementType.METHOD,
	ElementType.TYPE,
	ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Handler {
	UserNoteEventHandlerEnum value();
}

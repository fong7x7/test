package com.workfront.usernotebuilder.config;

import org.springframework.context.support.*;

import java.util.*;

/**
 * Dead simple message source for our service.
 * CNC Consider license implications for using ReloadableResourceBundleMessageSource (Apache v2.0)
 * ...it may be better to use an adapter delegate.
 */
public class ServiceMessageSource extends ReloadableResourceBundleMessageSource {
	public static final String DEFAULT_MESSAGE = " MESSAGE ERROR ";

	public String getMessage(final String code) {
		// With a default, this will not throw NoSuchMessageException. Consider if this is what we want...
		return getMessage(code, null, DEFAULT_MESSAGE, Locale.getDefault());
	}

	//CNC consider localized default message...
	public String getDefaultMessage() {
		return DEFAULT_MESSAGE;
	}
}

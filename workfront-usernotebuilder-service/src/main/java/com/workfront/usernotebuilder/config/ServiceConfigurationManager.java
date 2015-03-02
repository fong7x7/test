package com.workfront.usernotebuilder.config;

import com.attask.config.*;
import com.attask.util.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.env.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 */
@Component
@CommonsLog
public class ServiceConfigurationManager extends DefaultConfigurationManager {
	@Autowired
	private ConfigurableEnvironment environment;

	@Override
	public boolean containsKey(final String key) {
		return (super.containsKey(key) ?
			Boolean.TRUE :
			environment.containsProperty(key));
	}

	@Override
	public Object getProperty(final String key) {
		if (containsKey(key)) {
			Object value = null;
			try {
				value = super.getProperty(key);
			} catch (final Exception e) {
				log.error(e);
			}
			if (StringUtils.isBlank(value)) {
				value = environment.getProperty(key);
			}
			return value;
		}
		return super.getProperty(key);
	}

	@Override
	public String getString(final String key) {
		if (containsKey(key)) {
			String value = "";
			try {
				value = super.getString(key);
			} catch (final Exception e) {
				log.error(e);
			}
			if (StringUtils.isBlank(value)) {
				value = environment.getProperty(key);
			}
			return value;
		}
		throw new NoSuchElementException(
			String.format("'%s' doesn't map to an existing object", key));
	}
}

package com.workfront.usernotebuilder.health;

import com.attask.common.*;
import com.attask.util.*;
import com.workfront.usernotebuilder.config.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.actuate.endpoint.mvc.*;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

import javax.annotation.*;
import java.util.*;

/**
 * Manages custom {@link HealthStatus} values and their i18n messages.
 */
@Component
@Lazy
@CommonsLog
public class HealthStatusManager {
	@Autowired
	private ServiceMessageSource messageSource;

	@Autowired
	private HealthMvcEndpoint healthMvcEndpoint;

	private static final EnumMap<HealthStatus, Status> statusMap =
		new EnumMap<>(HealthStatus.class);

	@PostConstruct
	public void buildStatusMap() {
		for (final HealthStatus eachStatus : HealthStatus.values()) {
			try {
				statusMap.put(eachStatus, buildCustomStatus(eachStatus));
			} catch (InvalidParameterException e) {
				log.error("Error attempting to locate custom status for " + eachStatus, e);
			}
		}
		if (null != healthMvcEndpoint) {
			for (final Map.Entry<HealthStatus, Status> entry : statusMap.entrySet()) {
				healthMvcEndpoint.addStatusMapping(entry.getValue(), entry.getKey().getHttpStatus());
			}
		}
	}

	//CNC look at HealthMvcEndpoint#addStatusMapping
	/**
	 * This is meant to get statuses with custom localized messages.
	 * @param type the application {@link HealthStatus} that provides our i18n message
	 * @return a custom actuator Status for the given internal {@link HealthStatus}
	 */
	public Status getCustomStatus(final HealthStatus type) {
		Status outStatus = Status.UNKNOWN;
		if (statusMap.containsKey(type)) {
			outStatus = statusMap.get(type);
		}
		return outStatus;
	}

	private Status buildCustomStatus(final HealthStatus eachStatus) throws InvalidParameterException {
		final String messageKey = eachStatus.getStatusKey();
		final String message = messageSource.getMessage(messageKey);
		final String messageDescription = messageSource.getMessage(messageKey + ".description");
		if (isValidMessage(message)) {
			return getStatusForMessageSources(message, messageDescription);
		}
		throw new InvalidParameterException("messageKey", messageKey);
	}

	private Status getStatusForMessageSources(final String message, final String messageDescription) {
		if (isValidMessage(messageDescription)) {
			return new Status(message, messageDescription);
		}
		return new Status(message);
	}

	private boolean isValidMessage(String message) {
		return ! (StringUtils.isBlank(message) ||
			StringUtils.equal(messageSource.getDefaultMessage(), message));
	}

}

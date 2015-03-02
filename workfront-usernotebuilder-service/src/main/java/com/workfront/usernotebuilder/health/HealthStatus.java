package com.workfront.usernotebuilder.health;

import lombok.*;
import org.springframework.http.*;

/**
 * Type managed custom health indicator status.
 * This is intended to provide localized messages for status contents.
 *
 * @see org.springframework.boot.actuate.health.Health
 * @see org.springframework.boot.actuate.health.Status
 */
public enum HealthStatus {
	// TODO consider adding explicit HTTP status code mappings...
	UNKNOWN("healthstatus.unknown", HttpStatus.NO_CONTENT),
	UNAVAILABLE("healthstatus.unavailable", HttpStatus.SERVICE_UNAVAILABLE),
	DEGRADED("healthstatus.degraded", HttpStatus.PARTIAL_CONTENT),
	DOWN("healthstatus.down", HttpStatus.SERVICE_UNAVAILABLE),
	UP("healthstatus.up", HttpStatus.OK);

	@Getter
	private final String statusKey;

	@Getter
	private final HttpStatus httpStatus;

	private HealthStatus(String key, HttpStatus httpStatus) {
		//CNC use a message key to localize status
		this.statusKey = key;
		this.httpStatus = httpStatus;
	}
}

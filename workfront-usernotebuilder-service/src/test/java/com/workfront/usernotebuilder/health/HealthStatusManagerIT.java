package com.workfront.usernotebuilder.health;

import com.workfront.usernotebuilder.*;
import com.workfront.usernotebuilder.config.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.actuate.health.*;

import static org.junit.Assert.*;

public class HealthStatusManagerIT extends AbstractSpringTest {

	@Autowired
	private HealthStatusManager manager;

	@Autowired
	private ServiceMessageSource messageSource;

	@Test
	public void getCustomStatus() throws Exception {
		for (final HealthStatus eachStatus : HealthStatus.values()) {
			checkCustomStatus(eachStatus);
		}
	}

	private void checkCustomStatus(final HealthStatus healthStatus) {
		Status customStatus = manager.getCustomStatus(healthStatus);
		assertEquals(
			messageSource.getMessage(healthStatus.getStatusKey()),
			customStatus.getCode());

		assertEquals(
			messageSource.getMessage(healthStatus.getStatusKey() + ".description"),
			customStatus.getDescription());
	}
}
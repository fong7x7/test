package com.workfront.usernotebuilder.health;

import com.workfront.usernotebuilder.*;
import com.workfront.usernotebuilder.config.*;
import org.junit.*;
import org.mockito.*;
import org.springframework.boot.actuate.health.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;

public class HealthStatusManagerTest extends AbstractUnitTest {
	private static final String MOCK_VALUE = "ozymandias";

	@Mock
	private ServiceMessageSource messageSource;

	@InjectMocks
	private HealthStatusManager manager =
		Mockito.spy(new HealthStatusManager());

	@Before
	public void setup() {
		Mockito.doReturn(MOCK_VALUE).when(messageSource).getMessage(anyString());
		Mockito.doReturn(ServiceMessageSource.DEFAULT_MESSAGE)
			.when(messageSource).getDefaultMessage();

		manager.buildStatusMap();
	}

	@Test
	public void getCustomStatus() throws Exception {
		final Status customStatus = manager.getCustomStatus(HealthStatus.DEGRADED);
		assertEquals(MOCK_VALUE, customStatus.getCode());
		assertEquals(MOCK_VALUE, customStatus.getDescription());
	}
}
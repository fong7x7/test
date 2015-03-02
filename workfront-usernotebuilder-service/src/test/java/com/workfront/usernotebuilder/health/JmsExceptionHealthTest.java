package com.workfront.usernotebuilder.health;

import com.workfront.usernotebuilder.*;
import org.junit.*;
import org.mockito.*;
import org.springframework.boot.actuate.health.*;

import javax.jms.*;

import static org.junit.Assert.*;

public class JmsExceptionHealthTest extends AbstractUnitTest {
	private JMSException jmsException = new JMSException("TEST");

	@Mock
	private HealthStatusManager healthStatusManager;

	@InjectMocks
	@Spy
	private JmsExceptionHealth jmsExceptionHealth;

	@Before
	public void setUp() throws Exception {
		Mockito.doReturn(Status.OUT_OF_SERVICE).when(
			healthStatusManager).getCustomStatus(HealthStatus.DEGRADED);
	}

	@Test
	public void testDoHealthCheck() throws Exception {
		final Health health = jmsExceptionHealth.health();
		assertEquals(Status.UP, health.getStatus());
	}

	@Test
	public void testDoHealthCheck_onException() throws Exception {
		testOnException();
		final Health health = jmsExceptionHealth.health();
		assertEquals(Status.OUT_OF_SERVICE, health.getStatus());
	}

	@Test
	public void testOnException() throws Exception {
		jmsExceptionHealth.onException(jmsException);
		assertTrue(jmsExceptionHealth.getHasException().get());
		assertEquals(jmsException, jmsExceptionHealth.getLastException());
	}
}
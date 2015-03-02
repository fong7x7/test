package com.workfront.usernotebuilder.health;

import com.workfront.usernotebuilder.*;
import org.junit.*;
import org.mockito.*;
import org.springframework.boot.actuate.health.*;

import javax.jms.*;

import static org.junit.Assert.*;

public class JmsProviderHealthTest extends AbstractUnitTest {
	@Mock
	private ConnectionFactory connectionFactory;

	@Mock
	private Connection connection;

	@Spy
	@InjectMocks
	private JmsProviderHealth jmsProviderHealth;

	@Before
	public void setUp() throws Exception {
		Mockito.doReturn(connection).when(
			connectionFactory).createConnection();
	}

	@Test
	public void doHealthCheck() throws Exception {
		final Health health = jmsProviderHealth.health();
		Mockito.verify(connectionFactory).createConnection();
		Mockito.verify(connection).start();
		Mockito.verify(connection).close();
		assertEquals(Status.UP, health.getStatus());
	}

	@Test
	public void doHealthCheck_withException() throws Exception {
		Mockito.doThrow(new JMSException("TEST")).when(connection).start();
		final Health health = jmsProviderHealth.health();
		Mockito.verify(connectionFactory).createConnection();
		Mockito.verify(connection).start();
		Mockito.verify(connection).close();
		assertEquals(Status.DOWN, health.getStatus());
	}
}
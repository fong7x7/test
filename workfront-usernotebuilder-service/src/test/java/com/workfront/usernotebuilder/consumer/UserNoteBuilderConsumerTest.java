package com.workfront.usernotebuilder.consumer;

import com.attask.event.*;
import com.attask.event.transformer.*;
import com.attask.util.guids.*;
import com.workfront.usernotebuilder.*;
import org.junit.*;
import org.mockito.*;

import javax.jms.*;

import org.springframework.beans.factory.annotation.*;

public class UserNoteBuilderConsumerTest extends AbstractSpringTest {
	private static final String MOCK_ID = GUIDGeneratorFactory.getInstance().createGUID();

	@Autowired
	private EndorsementSharedConsumer consumer;

	@Autowired
	private JsonMessageTransformer transformer;

	@Mock
	private Session session = Mockito.mock(Session.class);

	@Mock
	private TextMessage message = Mockito.mock(TextMessage.class);

	@Before
	public void setUp() throws Exception {
		EndorsementShared event = buildEndorsementSharedEvent();

		final String s = transformer.serialize(event);
		Mockito.doReturn(s).when(message).getText();
	}

	@Test
	public void onMessage() throws Exception {
		consumer.onMessage(message, session);
		Mockito.verify(session).commit();
	}

	@Test
	public void onMessageOnMessage() throws Exception {
		TextMessage message2 = Mockito.mock(TextMessage.class);
		EndorsementShared event = buildEndorsementSharedEvent();
		final String s = transformer.serialize(event);
		Mockito.doReturn(s).when(message2).getText();

		consumer.onMessage(message, session);
		Mockito.verify(session).commit();
		Thread.sleep(1000);
		consumer.onMessage(message2, session);
	}

	private EndorsementShared buildEndorsementSharedEvent() {
		return EndorsementShared.builder()
			.accessorID(MOCK_ID)
			.customerId(MOCK_ID)
			.endorsementID(MOCK_ID)
			.enteredByID(MOCK_ID)
			.id(MOCK_ID)
			.accessorObjCode("USER")
			.build();
	}
}
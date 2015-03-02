package com.workfront.usernotebuilder.config;

import com.workfront.usernotebuilder.*;
import junit.framework.TestCase;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.*;
import org.springframework.test.context.junit4.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceApplication.class)
public class ConsumerPropertiesTest extends TestCase {
	@Autowired
	private ConsumerProperties consumerProperties;

	@Value("${attask.consumer.name}")
	private String consumerName;

	@Value("${attask.consumer.queueName}")
	private String queueName;

	@Value("${attask.consumer.requestChannel}")
	private String requestChannel;

	@Value("${attask.consumer.topicPattern}")
	private String topicPattern;

	@Test
	public void testGetBrokerLocation() throws Exception {
		assertEquals(Constants.DEFAULT_BROKER_LOCATION, consumerProperties.getBrokerLocation());
	}

	@Test
	public void testGetConsumerName() throws Exception {
		assertEquals(consumerName, consumerProperties.getName());
	}

	@Test
	public void testGetQueueName() throws Exception {
		assertEquals(queueName, consumerProperties.getQueueName());
	}

	@Test
	public void testGetRequestChannel() throws Exception {
		assertEquals(requestChannel, consumerProperties.getRequestChannel());
	}

	@Test
	public void testGetTopicPattern() throws Exception {
		assertEquals(topicPattern, consumerProperties.getTopicPattern());
	}
}
package com.workfront.usernotebuilder.config;

import org.springframework.jms.support.destination.*;

import javax.jms.*;

/**
 * Customized JMS destination resolution strategy. Uses the consumer properties
 * to resolve App-Event VT consumer queues.
 * After resolving the destination name from our "pattern" property, this simply
 * delegates to the default destination resolver for the
 * {@link org.springframework.jms.core.JmsTemplate}
 *
 * @see org.springframework.jms.config.AbstractJmsListenerContainerFactory#setDestinationResolver(DestinationResolver)
 * @see ServiceConfig#jmsListenerContainerFactory()
 */
class AppEventTopicDestinationResolver implements DestinationResolver {

	private final String consumerName;

	private final String topicPattern;

	// Our destination resolver delegate.
	private final DynamicDestinationResolver resolver;

	/**
	 * Configure our destination resolver. The topic-pattern and consumer-name should be
	 * consistent between service instances, unless duplicate message processing or VT
	 * partitioning strategies are expected.
	 * @param consumerName the simple service-consumer name.
	 * @param topicPattern the pattern to use when applying consumername and destination-name.
	 */
	public AppEventTopicDestinationResolver(String consumerName, String topicPattern) {
		this.consumerName = consumerName;
		this.topicPattern = topicPattern;
		this.resolver = new DynamicDestinationResolver();
	}


	@Override
	public Destination resolveDestinationName(
		Session session, String destinationName, boolean pubSubDomain) throws JMSException
	{
		final String parsedDestination = String.format(
			topicPattern, consumerName, destinationName);

		return resolver.resolveDestinationName(session, parsedDestination, pubSubDomain);
	}
}

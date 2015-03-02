package com.workfront.usernotebuilder.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;

/**
 * JMS consumer properties that are injected from Spring environment.
 * This is a component scanned configuration bean.
 * @see configuration properties files like application.yml
 */
@Configuration("consumerProperties")
@ConfigurationProperties(prefix="attask.consumer")
@Getter @Setter
public class ConsumerProperties {
	// Object Integration Flows internal message queue poller interval
	public Integer pollInterval;

	// Default internal message queue size
	public Integer queueSize;

	//JMS DMLC receive operation timeout
	public Long receiveTimeout;

	//Consider vm://localhost - see http://activemq.apache.org/vm-transport-reference.html
	@Value("${jms.broker.url:#{constants.DEFAULT_BROKER_LOCATION}}")
	public String brokerLocation;

	// Our service-specific app-event consumer name
	public String name;

	// Intermediary service-specific queue name
	public String queueName;

	// Internal gateway transformation/processing channel
	public String requestChannel;

	// Alternate internal gateway channel (short-circuit transform)
	public String sourceChannel;

	// ActiveMQ VT consumer queue name pattern (to be used with String substitution)
	public String topicPattern;
}

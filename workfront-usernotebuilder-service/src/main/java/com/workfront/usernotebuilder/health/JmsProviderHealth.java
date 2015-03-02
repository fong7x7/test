package com.workfront.usernotebuilder.health;

import javax.jms.*;

import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

/**
 * An actuator {@link org.springframework.boot.actuate.health.HealthIndicator}
 * to report on our JMS provider connection availability. Attempt to create a
 * connection using the application default connection factory (to ActiveMQ) &
 * adjust the indicator health status depending on reported errors.
 */
@Component
@Lazy
@CommonsLog
public class JmsProviderHealth extends AbstractHealthIndicator {

	//CNC consider adding a qualifier
	@Autowired
	@Lazy
	private ConnectionFactory jmsConnectionFactory;

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		Connection connection = null;
		try {
			connection = jmsConnectionFactory.createConnection();
			connection.start();
			builder.up();
		} catch (final JMSException errJms) {
			builder.down(errJms);
			log.error("Error while creating connection: ", errJms);
		} finally {
			if (null != connection) {
				try {
					connection.close();
				} catch (final JMSException jmsErr) {
					builder.unknown().withException(jmsErr);
					log.error("Error while closing connection: ", jmsErr);
				}
			}
		}
	}
}


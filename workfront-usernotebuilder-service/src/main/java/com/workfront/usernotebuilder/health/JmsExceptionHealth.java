package com.workfront.usernotebuilder.health;

import lombok.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

import javax.jms.*;
import java.util.concurrent.atomic.*;

/**
 * Report on JMX exceptions that are routed to this class via {@link javax.jms.ExceptionListener}
 * This bean will hold onto the last exception indefinitely. We need to do some analysis
 * on exception frequency and introduce mechanisms to clear the status.
 */
@Component
@CommonsLog
public class JmsExceptionHealth extends AbstractHealthIndicator implements ExceptionListener {
	@Autowired
	@Lazy
	private HealthStatusManager healthStatus;

	@Getter
	private AtomicBoolean hasException = new AtomicBoolean();

	// This is often a bad idea, but the health endpoint is polled every 1000ms.
	@Getter
	private Exception lastException;

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		if (hasException.get()) {
			builder.status(
					healthStatus.getCustomStatus(HealthStatus.DEGRADED))
				.withException(lastException);
		} else {
			builder.up();
		}
	}

	@Override
	public void onException(JMSException e) {
		log.error("JMS exception detected from provider connection: ", e);
		// Don't know much about the rate of exceptions to expect.
		// Currently taking only one exception to set the status.
		if (hasException.compareAndSet(Boolean.FALSE, Boolean.TRUE)) {
			lastException = e;
		}
	}

	//TODO consider adding reset endpoint?
}

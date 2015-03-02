package com.workfront.usernotebuilder.config;

import org.springframework.context.annotation.*;

/**
 * As much as possible, constants should be centralized in this project.
 * This will prevent configuration errors and assist in tuning, scaling &
 * migrating important application behaviours.
 * <p>Constants from this class are candidates for tuneable application properties.
 * <p>This class is marked as a Configuration class so that constants may be
 * referenced by bean name in SpEL expressions.
 */
@Configuration("constants")
public class Constants {
	public static final String DEFAULT_BROKER_LOCATION = "failover:(tcp://localhost:61616)";
	public static final String JMX_CONNECTOR = "connector:name=jmxmp";
	public static final String JMX_SERVICE_URL = "service:jmx:jmxmp://localhost:12008";
	// This will set the maximum concurrent consumers for each DMLC,
	//   consider scaling this number for those queues with very high volume
	public static final String POOL_RANGE = "1-3";

	public static final int HTTP_TIMEOUT = 2000;
	public static final int JMS_SESSIONCACHE_SIZE = 1;
	// Our executor service core pool size
	public static final int POOL_CORE_SIZE = 3;
	//CNC consider upping to 20+. Generally need max-concurrent-consumer+1
	// Note that we are not currently applying CACHE_CONSUMERS due to prefetch, which needs testing
	// This is the maximum thread pool size for our executor service
	//   and the maximum connection pool size for any Session
	public static final int POOL_MAX_SIZE = 15;
}

package com.workfront.usernotebuilder.health;

import com.attask.component.api.*;
import com.attask.sdk.api.*;
import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.*;

/**
 * Check the health of API-SDK interactions with the web-application
 */
@Component
@CommonsLog
public class WorkfrontApiHealth extends AbstractHealthIndicator {
	@Autowired
	private ApiFactory apiFactory;

	//CNC use UserApi / ApiFactory to call the monolith... need a stable test-customer to do so
	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		try {
			/* CNC what to call?
			final API adminApi = apiFactory.getCustomerAdminApi(STABLE_CUSTOMER_ID);
			adminApi.getVersion();
			*/
			builder.up();
		} catch (final Exception e) {
			builder.down(e);
			log.error("Error while trying to reach Workfront API.", e);
		}
	}
}


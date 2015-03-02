package com.workfront.usernotebuilder.health;

import lombok.extern.apachecommons.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.actuate.health.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

/**
 * Check the availability of the Workfront web-application via REST calls.
 */
@Component
@CommonsLog
public class WorkfrontAvailableHealth extends AbstractHealthIndicator {
	static final String URL_VERSION = "%s/version";
	static final String URL_INFO = "%s/attask/api-internal/info";

	@Autowired
	private RestTemplate restTemplate;

	@Value("${component.api.baseUrl:http://localhost:8080}")
	public String workfrontHost;

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		try {
			//CNC consider a more directed request and response structure
			final ResponseEntity<String> response =
				restTemplate.getForEntity(
					String.format(URL_VERSION, workfrontHost), String.class);

			final String responseBody = response.getBody();
			if (HttpStatus.OK.equals(response.getStatusCode())) {
				builder.up().withDetail("version", responseBody);
			} else {
				final HttpStatus statusCode = response.getStatusCode();
				//CNC the following is much too verbose...
				builder.outOfService()
					.withDetail("HTTP status",
						statusCode.value() + statusCode.getReasonPhrase())
					.withDetail("version", responseBody);
			}
		} catch (final Exception e) {
			builder.down(e);
			log.error("Error while trying to reach Workfront at host: " + workfrontHost, e);
		}
	}
}


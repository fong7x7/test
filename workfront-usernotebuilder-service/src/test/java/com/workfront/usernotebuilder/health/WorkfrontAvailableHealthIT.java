package com.workfront.usernotebuilder.health;

import com.workfront.usernotebuilder.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.actuate.health.*;
import org.springframework.http.*;
import org.springframework.test.web.client.*;
import org.springframework.web.client.*;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class WorkfrontAvailableHealthIT extends AbstractSpringTest {
	public static final String RESPONSE_BODY = "{version: TEST}";

	@Value("${component.api.baseUrl:http://localhost:8080}")
	public String workfrontHost;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private WorkfrontAvailableHealth workfrontAvailableHealth;

	private MockRestServiceServer mockServer;

	@Before
	public void setUp() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	public void doHealthCheck() throws Exception {
		mockServer.expect(
			requestTo(String.format(
					WorkfrontAvailableHealth.URL_VERSION, workfrontHost)))
			.andExpect(
				method(HttpMethod.GET))
			.andRespond(
				withSuccess(RESPONSE_BODY, MediaType.APPLICATION_JSON));

		final Health health = workfrontAvailableHealth.health();
		assertTrue(health.getDetails().containsKey("version"));
		assertEquals(RESPONSE_BODY, health.getDetails().get("version"));
		assertEquals(Status.UP, health.getStatus());
	}
}
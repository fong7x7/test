package com.workfront.usernotebuilder.config;

import com.attask.cache.api.*;
import com.attask.cache.core.*;
import com.attask.common.*;
import com.attask.sdk.api.*;
import com.attask.sdk.model.internal.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.util.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.test.context.*;

import java.util.*;

import static org.junit.Assert.*;

@ActiveProfiles({"ehcache", "develop"})
public class CacheConfigIT extends AbstractSpringTest {
	@Value("${core.rest.api.hostname}")
	private String coreHostname;

	@Autowired
	private CacheImpl userSessionCache;

	@Autowired
	private UserApiFactory userApiFactory;

	@Autowired
	private CustomerAdminApiFactory customerApiFactory;

	@Test
	public void testWiring() {
		assertNotNull(userSessionCache);
		assertNotNull(userApiFactory);
		//userApiFactory.createUserApi();
		final String customerID = "533da2820000005e7e25052b15a539f0";
		final String userID = "533da2830000011b3f22a511c59f4822";
		final String projectID = "533da2d20000012a6f7305f3fccc1d8b";

		final UserApi userApi = userApiFactory.createUserApi(customerID, userID);
		final Project apiObject = userApi.getApiObject(
			Project.class, Project.OBJCODE, projectID, Collections.<String>emptyList());

		assertEquals(projectID, apiObject.getID());

		final Map<String, Object> adminsQuery = getCustomerAdminsQuery(customerID);
		final CustomerAdminApi potentialAPI = customerApiFactory.createCustomerAdminApi(customerID);
		final API customerAdminApi = potentialAPI.getCachedCustomerAdminApi(customerID);
		final RequestResult<List<User>> requestResult = customerAdminApi.search(
			User.OBJCODE, adminsQuery, Collections.singletonList(QueryConstants.ID));

		final List<User> users = requestResult.getData();
		Collection<AppGUID> collectIDs = Collections.emptySet();
		if (null != users && ! users.isEmpty()) {
			collectIDs = CollectorUtil.collectGUIDs(users);
		}
		assertTrue(collectIDs.size() > 0);
	}

	private Map<String, Object> getCustomerAdminsQuery(final String customerID) {
		// Get all admins for all active customers by default
		final ImmutableMap<String, Object> queryMap = ImmutableMap.<String, Object>builder()
			.put("accessLevel:isAdmin", QueryConstants.TRUE)
			.put("customer:isDisabled", Boolean.FALSE)
			.put("isActive", Boolean.TRUE)
			.put("customerID", customerID)
			.build();

		return queryMap;
	}

}

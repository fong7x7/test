package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.event.*;
import com.attask.sdk.api.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.util.*;
import org.junit.*;
import org.mockito.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AnnouncementEventHandlerTest extends AbstractEventHandlerTest<AnnouncementAddEvent> {
	private final User MOCK_USER = new User();
	private final String MOCK_USER_ID = generateGUID();
	private final String MOCK_CUSTOMER_ID = generateGUID();
	private final RequestResult<List<User>> MOCK_CUSTOMER_ADMINS = new RequestResult<List<User>>(
		Arrays.asList(MOCK_USER),
		new APIException("Result list exception.")
	);

	@Mock private API customerApi;

	@InjectMocks
	private AnnouncementEventHandler announcementEventHandler;

	@Before
	public void setup() throws AtTaskException {
		super.setup();
		MOCK_USER.setID(MOCK_USER_ID);
		final Map<String, Object> query = getCustomerAdminsQuery(
			CollectorUtil.generateAppGUID(User.OBJCODE, MOCK_CUSTOMER_ID));

		final List<String> ids = Collections.singletonList(QueryConstants.ID);

		Mockito.doReturn(MOCK_CUSTOMER_ADMINS)
			.when(customerApi)
			.search(User.OBJCODE, query, ids);

		Mockito.doReturn(customerApi)
			.when(apiFactory)
			.getCustomerAdminApi(MOCK_CUSTOMER_ID);
	}

	@Test
	public void testAnnouncementEventHandler() throws AtTaskException {
		final AnnouncementAddEvent announcementAddEvent = buildEvent(AnnouncementAddEvent.class);
		final int numberOfIDs = 1;
		announcementAddEvent.setId(MOCK_ID);
		announcementAddEvent.setCustomerId(MOCK_CUSTOMER_ID);

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = announcementEventHandler.handleEvent(announcementAddEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);
		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertEquals(UserNoteEvent.ANNOUNCEMENT_ADD, handledSource.getEventType());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, MOCK_USER_ID)));
	}

	private Map<String, Object> getCustomerAdminsQuery(final AppGUID customerID) {
		// Get all admins for all active customers by default
		final ImmutableMap<String, Object> queryMap = ImmutableMap.<String, Object>builder()
			.put("accessLevel:isAdmin", QueryConstants.TRUE)
			.put("customer:isDisabled", Boolean.FALSE)
			.put("isActive", Boolean.TRUE)
			.put("customerID", customerID.getId())
			.build();

		return queryMap;
	}
}

package com.workfront.usernotebuilder.filter;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.api.*;
import com.attask.sdk.model.internal.*;
import com.attask.util.guids.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.handler.*;
import com.workfront.usernotebuilder.util.*;
import org.junit.*;
import org.mockito.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;

public class CompositeBaseFilterTest extends AbstractFilterTest {
	private LikeEvent mockEvent = new LikeEvent();
	private AppGUID passableUserGUID, userWithOutAccessLevelGUID, userWithOptOutNotificationGUID;

	@Mock private API api;
	@Mock private User passableUser, userWithOutAccessLevel, userWithOptOutNotification;
	@Mock private ApiFactory apiFactory;
	@Spy private ActionUserFilter mockAppendFilter = new ActionUserFilter();
	/*Spy*/ private CompositeBaseFilter compositeBaseFilter;
	
	@Before
	public void setup() throws AtTaskException{
		initMocks();
		Mockito.doReturn(api).when(apiFactory).getAspApi();
		
		LikeEndorsementHandler handler = new LikeEndorsementHandler(apiFactory);
		compositeBaseFilter = Mockito.spy(new CompositeBaseFilter(handler));

		passableUserGUID = generateGUID();
		userWithOutAccessLevelGUID = generateGUID();
		userWithOptOutNotificationGUID = generateGUID();

		setupPassableUser();
		setupMockUser(userWithOutAccessLevel, userWithOutAccessLevelGUID.getId());
		setupUserWithOptOutNotifications(handler);
	}
	
	private void setupPassableUser() {
		setupMockUser(passableUser, passableUserGUID.getId());
		Mockito.doReturn("mockID").when(passableUser).getAccessLevelID();
	}
	
	private void setupUserWithOptOutNotifications(AbstractApplicationEventUserNoteHandler handler){
		setupMockUser(userWithOptOutNotification, userWithOptOutNotificationGUID.getId());
		UserPrefValue userPrefValue = new UserPrefValue();
		userPrefValue.setName("inactiveNotifications");
		userPrefValue.setValue(handler.getEventHandlerName(mockEvent));
		final List<UserPrefValue> mockUserPrefs = Collections.<UserPrefValue>singletonList(
			userPrefValue
		);
		Mockito.doReturn(mockUserPrefs).when(userWithOptOutNotification).getUserPrefValues();
	}
	
	private void setupMockUser(User mockUser, String id) {
		RequestResult<User> requestResult = new RequestResult<>(mockUser, new APIException(""));
		Mockito.doReturn(id).when(mockUser).getID();
		Mockito.doReturn(requestResult).when(api).get(eq(User.OBJCODE), eq(id), anyList());
	}
	
	private AppGUID generateGUID() throws AtTaskException{
		return CollectorUtil.generateAppGUID(
			User.OBJCODE,
			GUIDGeneratorFactory.getInstance().createGUID()
		);	
	}
	
	@Test
	@Override
	public void predicate() {
		final Optional<LikeEvent> optional = Optional.of(mockEvent);
		final Predicate<AppGUID> predicate = compositeBaseFilter.predicate(optional);
		Collection<AppGUID> guids = Collections2.filter(
			ImmutableSet.of(passableUserGUID, userWithOutAccessLevelGUID, userWithOptOutNotificationGUID),
			predicate
		);

		assertEquals(1, guids.size());
	}

	@Test
	@Override
	public void chain() throws Exception {
		mockEvent.setTransactionUserID(passableUserGUID.getId());
		final Optional<LikeEvent> optional = Optional.of(mockEvent);
		compositeBaseFilter.chain(mockAppendFilter);
		Collection<AppGUID> guids = Collections2.filter(
			ImmutableSet.of(passableUserGUID, userWithOutAccessLevelGUID, userWithOptOutNotificationGUID),
			compositeBaseFilter.predicate(optional));

		//Mockito.verify(compositeBaseFilter).chain(compositePredicate, mockAppendPredicate, Filter.PredicatePolicy.AND);
		Mockito.verify(mockAppendFilter).predicate(eq(optional));

		assertTrue(guids.isEmpty());
	}
}
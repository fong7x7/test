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

public class UserAccessLevelFilterTest extends AbstractFilterTest {
	private LikeEvent mockEvent = new LikeEvent();
	private AppGUID mockGUID;

	@Mock private API api;
	@Mock private User mockUser;
	@Mock private ApiFactory apiFactory;
	@Spy private ActionUserFilter mockAppendFilter = new ActionUserFilter();
	/*Spy*/ private UserAccessLevelFilter userAccessLevelFilter;
	
	@Before
	public void setup() throws AtTaskException{
		initMocks();
		LikeEndorsementHandler handler = new LikeEndorsementHandler(apiFactory);
		userAccessLevelFilter = Mockito.spy(new UserAccessLevelFilter(handler));
		mockGUID = CollectorUtil.generateAppGUID(
			User.OBJCODE,
			GUIDGeneratorFactory.getInstance().createGUID()
		);
		RequestResult<User> requestResult = new RequestResult<>(mockUser, new APIException(""));
		Mockito.doReturn(mockGUID.getId()).when(mockUser).getID();
		Mockito.doReturn(requestResult).when(api).get(eq(User.OBJCODE), anyString(), anyList());
		Mockito.doReturn(api).when(apiFactory).getAspApi();
		mockEvent.setTransactionUserID(mockGUID.getId());
	}
	
	@Test
	@Override
	public void predicate() {
		final Optional<LikeEvent> optional = Optional.of(mockEvent);
		final Predicate<AppGUID> predicate = userAccessLevelFilter.predicate(optional);
		Mockito.verify(userAccessLevelFilter).predicate(eq(optional));
		Collection<AppGUID> guids = Collections2.filter(
			ImmutableSet.of(mockGUID),
			predicate
		);

		assertTrue(guids.isEmpty());
	}
	
	@Test
	public void predicate_with_pass() {
		Mockito.doReturn(mockGUID.getId()).when(mockUser).getAccessLevelID();
		final Optional<LikeEvent> optional = Optional.of(mockEvent);
		final Predicate<AppGUID> predicate = userAccessLevelFilter.predicate(optional);
		Mockito.verify(userAccessLevelFilter).predicate(eq(optional));
		Collection<AppGUID> guids = Collections2.filter(
			ImmutableSet.of(mockGUID),
			predicate
		);

		assertEquals(1, guids.size());
	}

	@Test
	@Override
	public void chain() throws Exception {
		final Optional<LikeEvent> optional = Optional.of(mockEvent);
		userAccessLevelFilter.chain(mockAppendFilter);

		Collection<AppGUID> guids = Collections2.filter(
			ImmutableSet.of(mockGUID), userAccessLevelFilter.predicate(optional));

		Mockito.verify(userAccessLevelFilter).getBasePredicate(optional);
		Mockito.verify(mockAppendFilter).getBasePredicate(optional);

		assertTrue(guids.isEmpty());
	}
}
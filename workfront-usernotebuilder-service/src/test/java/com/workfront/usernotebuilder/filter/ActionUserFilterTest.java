package com.workfront.usernotebuilder.filter;

import com.attask.common.*;
import com.attask.event.*;
import com.attask.sdk.model.internal.*;
import com.attask.util.guids.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.util.*;
import org.junit.*;
import org.mockito.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class ActionUserFilterTest extends AbstractFilterTest {
	private static final String GUID1 = GUIDGeneratorFactory.getInstance().createGUID();
	private static final String GUID2 = GUIDGeneratorFactory.getInstance().createGUID();

	@Mock private AbstractApplicationEvent mockEvent;
	@Spy private ActionUserFilter mockAppendFilter;
	/*Spy*/ private ActionUserFilter filter;

	@Before
	public void setUp() throws Exception {
		initMocks();
		filter = Mockito.spy(new ActionUserFilter());
		Mockito.doReturn(GUID1).when(mockEvent).getTransactionUserID();
	}

	@Test
	@Override
	public void predicate() throws AtTaskException {
		final Optional<AbstractApplicationEvent> optional = Optional.of(mockEvent);
		final Predicate<AppGUID> predicate = filter.predicate(optional);
		Mockito.verify(filter).predicate(eq(optional));
		Collection<AppGUID> guids = Collections2.filter(
			ImmutableSet.of(
				CollectorUtil.generateAppGUID(User.OBJCODE, GUID1)), predicate);

		assertTrue(guids.isEmpty());
	}

	@Test
	public void predicate_withPass() throws AtTaskException {
		//
		final Optional<AbstractApplicationEvent> optional = Optional.of(mockEvent);
		final Predicate<AppGUID> predicate = filter.predicate(optional);
		//Mockito.verify(filter).predicate(eq(optional));
		Collection<AppGUID> guids = Collections2.filter(
			CollectorUtil.generateAppGUIDs(User.OBJCODE, GUID1, GUID2), predicate);

		assertTrue(guids.size() == 1);
		for (AppGUID guid : guids) {
			assertEquals(GUID2, guid.getId());
		}
	}

	@Test
	@Override
	public void chain() throws Exception {
		final Optional<AbstractApplicationEvent> optional = Optional.of(mockEvent);
		filter.chain(mockAppendFilter);
		
		Collection<AppGUID> guids = Collections2.filter(
			ImmutableSet.of(
				CollectorUtil.generateAppGUID(User.OBJCODE, GUID1)), filter.predicate(optional));

		Mockito.verify(filter).getBasePredicate(eq(optional));
		Mockito.verify(mockAppendFilter).getBasePredicate(eq(optional));

		assertTrue(guids.isEmpty());
	}
}
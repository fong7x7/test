package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.api.*;
import com.attask.sdk.model.internal.*;
import com.attask.util.guids.*;
import com.workfront.usernotebuilder.*;
import com.workfront.usernotebuilder.event.*;
import org.apache.commons.logging.*;
import org.junit.*;
import org.mockito.*;

import java.util.*;

public abstract class AbstractEventHandlerTest <P extends AbstractApplicationEvent> extends AbstractUnitTest {
	private static final Log LOG = LogFactory.getLog(AbstractEventHandlerTest.class);
	protected static final GUIDGenerator GENERATOR = GUIDGeneratorFactory.getInstance();

	protected final String MOCK_ID = generateGUID();
	protected final String MOCK_TRANSACTION_USER_ID = generateGUID();

	@Mock
	protected API aspApi;
	@Spy
	protected ApiFactory apiFactory = new ApiFactory() {
		@Override
		public API getAspApi() {
			return aspApi;
		}

		@Override
		public API getCustomerAdminApi(String s) {
			return null;
		}

		@Override
		public API getUserApi(String s) {
			return null;
		}
	};

	@Before
	public void setup() throws AtTaskException {
		MockitoAnnotations.initMocks(this);
		User user = new User();
		user.setAccessLevelID(generateGUID());
		RequestResult<User> result = new RequestResult<User>(user, new APIException("User api exception"));
		Mockito.doReturn(result)
			.when(aspApi)
			.get(Mockito.eq(User.OBJCODE), Mockito.anyString(), Mockito.anyList());
	}

	protected P buildEvent(Class<P> eventClass) {
		P event = null;
		try {
			event = eventClass.newInstance();
			event.setCustomerId(MOCK_ID);
			event.setTransactionUserID(MOCK_TRANSACTION_USER_ID);
		} catch (Exception e) {
			LOG.error("Error occurred in creating a new app event.", e);
		}
		return event;
	}

	protected String generateGUID() {
		return GENERATOR.createGUID();
	}

	protected boolean containsTestGUIDs(UserNoteSource source, AppGUID... testGUIDs) {
		final Collection<AppGUID> notifyGUIDs = source.getNotifyGUIDs();
		for(AppGUID testGUID : testGUIDs) {
			if(! notifyGUIDs.contains(testGUID)) {
				return false;
			}
		}
		return true;
	}
}

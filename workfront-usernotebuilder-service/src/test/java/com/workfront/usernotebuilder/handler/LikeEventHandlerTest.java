package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.event.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.util.*;
import org.junit.*;
import org.mockito.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class LikeEventHandlerTest extends AbstractEventHandlerTest<LikeEvent> {
	@Spy private final LikeEndorsementHandler likeEndorsementHandler = new LikeEndorsementHandler(apiFactory);
	@Spy private final LikeNoteHandler likeNoteHandler = new LikeNoteHandler(apiFactory);

	@InjectMocks
	private final LikeEventHandler likeEventHandler = new LikeEventHandler();

	@Test
	public void testLikeNoteHandler() throws AtTaskException {
		final LikeEvent likeNoteEvent = buildEvent(LikeEvent.class);
		final int numberOfIDs = 1;
		final String mockOwnerID = generateGUID();
		likeNoteEvent.setId(MOCK_ID);
		likeNoteEvent.setNoteOwnerID(mockOwnerID);

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = likeEventHandler.handleEvent(likeNoteEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		verify(likeNoteHandler).handleEvent(likeNoteEvent, source);
		assertEquals(UserNoteEvent.LIKE_NOTE, handledSource.getEventType());

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, mockOwnerID)));
	}

	@Test
	public void testLikeEndorsementHandling() throws AtTaskException {
		final LikeEvent likeEvent = buildEvent(LikeEvent.class);
		final int numberOfIDs = 4;
		final String endorserID = generateGUID();
		final String receiverID = generateGUID();
		final String sharedUserID = generateGUID();
		final String sharedTeamID = generateGUID();
		likeEvent.setId(MOCK_ID);
		likeEvent.setEndorsementID(MOCK_ID);
		likeEvent.setEndorserID(endorserID);
		likeEvent.setEndorsementReceiverID(receiverID);
		likeEvent.setEndorsementSharedUserIDs(Collections.singletonList(sharedUserID));
		likeEvent.setEndorsementSharedTeamIDs(Collections.singletonList(sharedTeamID));

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = likeEventHandler.handleEvent(likeEvent, source);

		assertEquals(handledSource.getUserNotableID(), MOCK_ID);

		verify(likeEndorsementHandler).handleEvent(likeEvent, source);
		assertEquals(UserNoteEvent.LIKE_ENDORSER, handledSource.getEventType());

		AppGUID[] testGUIDS = {
			CollectorUtil.generateAppGUID(User.OBJCODE, endorserID),
			CollectorUtil.generateAppGUID(User.OBJCODE, receiverID),
			CollectorUtil.generateAppGUID(User.OBJCODE, sharedUserID),
			CollectorUtil.generateAppGUID(Team.OBJCODE, sharedTeamID)
		};

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertTrue(containsTestGUIDs(handledSource, testGUIDS));
	}

	@Test
	public void testLikeHandlerFiltering() throws AtTaskException {
		final LikeEvent likeEvent = buildEvent(LikeEvent.class);
		final int numberOfIDs = 0;
		likeEvent.setNoteOwnerID(MOCK_TRANSACTION_USER_ID);

		UserNoteSource source = UserNoteSource.builder().build();
		UserNoteSource handledSource = likeEventHandler.handleEvent(likeEvent, source);

		assertEquals(numberOfIDs, handledSource.getNotifyGUIDs().size());
		assertFalse(containsTestGUIDs(handledSource, CollectorUtil.generateAppGUID(User.OBJCODE, MOCK_ID)));
	}
}

package com.workfront.usernotebuilder.config;

import com.attask.event.*;
import org.junit.*;

import static org.junit.Assert.*;

public class UserNoteEventHandlerEnumTest {

	@Test
	public void testGetEventClass() throws Exception {
		assertEquals(AnnouncementAddEvent.class,
			UserNoteEventHandlerEnum.ANNOUNCEMENT.getEventClass());
	}

	@Test
	public void testFastValueOf() throws Exception {
		assertEquals(UserNoteEventHandlerEnum.ANNOUNCEMENT,
			UserNoteEventHandlerEnum.fastValueOf(AnnouncementAddEvent.class));

		assertEquals(UserNoteEventHandlerEnum.DOCUMENTAPPROVE,
			UserNoteEventHandlerEnum.fastValueOf(DocumentApproveEvent.class));

		assertEquals(UserNoteEventHandlerEnum.ENDORSEMENTADD,
			UserNoteEventHandlerEnum.fastValueOf(EndorsementAddEvent.class));

		assertEquals(UserNoteEventHandlerEnum.ENDORSEMENTSHARED,
			UserNoteEventHandlerEnum.fastValueOf(EndorsementShared.class));

		assertEquals(UserNoteEventHandlerEnum.LIKEADD,
			UserNoteEventHandlerEnum.fastValueOf(LikeEvent.class));

		assertEquals(UserNoteEventHandlerEnum.NOTEADD,
			UserNoteEventHandlerEnum.fastValueOf(NoteAddEvent.class));

		assertEquals(UserNoteEventHandlerEnum.WORKREQUEST,
			UserNoteEventHandlerEnum.fastValueOf(WorkRequestEvent.class));
	}

	@Test
	public void testValueOf() throws Exception {
		assertEquals(UserNoteEventHandlerEnum.ANNOUNCEMENT,
			UserNoteEventHandlerEnum.valueOf(AnnouncementAddEvent.class));

		assertEquals(UserNoteEventHandlerEnum.DOCUMENTAPPROVE,
			UserNoteEventHandlerEnum.valueOf(DocumentApproveEvent.class));

		assertEquals(UserNoteEventHandlerEnum.ENDORSEMENTADD,
			UserNoteEventHandlerEnum.valueOf(EndorsementAddEvent.class));

		assertEquals(UserNoteEventHandlerEnum.ENDORSEMENTSHARED,
			UserNoteEventHandlerEnum.valueOf(EndorsementShared.class));

		assertEquals(UserNoteEventHandlerEnum.LIKEADD,
			UserNoteEventHandlerEnum.valueOf(LikeEvent.class));

		assertEquals(UserNoteEventHandlerEnum.NOTEADD,
			UserNoteEventHandlerEnum.valueOf(NoteAddEvent.class));

		assertEquals(UserNoteEventHandlerEnum.WORKREQUEST,
			UserNoteEventHandlerEnum.valueOf(WorkRequestEvent.class));
	}
}
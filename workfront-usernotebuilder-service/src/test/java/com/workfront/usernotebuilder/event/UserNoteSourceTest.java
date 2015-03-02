package com.workfront.usernotebuilder.event;

import com.attask.util.guids.*;
import org.junit.*;

public class UserNoteSourceTest {

	public static final String GUID = GUIDGeneratorFactory.getInstance().createGUID();

	@Test
	public void testBuilder() throws Exception {
		UserNoteSource source = UserNoteSource.builder()
			.customerID("").build();
		//CNC test this...
	}
}
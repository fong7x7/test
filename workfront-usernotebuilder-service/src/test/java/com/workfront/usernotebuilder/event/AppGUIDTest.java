package com.workfront.usernotebuilder.event;

import com.attask.common.*;
import com.attask.util.guids.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.*;
import com.workfront.usernotebuilder.util.*;
import org.junit.*;

import static org.junit.Assert.*;

public class AppGUIDTest extends AbstractUnitTest {
	private static final String BAD_ID = "1234556789abcdefghijklmnopqrst";
	private static final String MOCK_ID = GUIDGeneratorFactory.getInstance().createGUID();
	private static final String OBJCODE = "ANY";

	@Test
	public void testBuilder() throws Exception {
		AppGUID guid = AppGUID.builder().id(MOCK_ID).objCode(OBJCODE).build();
		assertEquals(MOCK_ID, guid.getId());
		assertEquals(OBJCODE, guid.getObjCode());
		AppGUID copyGUID = AppGUID.builder().id(MOCK_ID).objCode(OBJCODE).build();
		assertEquals(guid, copyGUID);
		expectException.expect(InvalidParameterException.class);
		guid = AppGUID.builder().id(BAD_ID).build();
	}

	@Test
	public void testHashCode() throws Exception {
		final AppGUID guid1 = CollectorUtil.generateAppGUID(OBJCODE, MOCK_ID);
		final AppGUID guid2 = CollectorUtil.generateAppGUID(OBJCODE, MOCK_ID);
		final AppGUID guid3 = CollectorUtil.generateAppGUID(OBJCODE, MOCK_ID);
		final AppGUID guid4 = CollectorUtil.generateAppGUID(OBJCODE, MOCK_ID);
		final ImmutableSet<AppGUID> guids = ImmutableSet.of(guid1, guid2, guid3, guid4);
		assertEquals(1, guids.size());

	}
}
package com.workfront.usernotebuilder;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.runners.*;
import org.springframework.boot.test.*;
import org.springframework.test.context.junit4.*;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractUnitTest {
	@Rule
	public ExpectedException expectException = ExpectedException.none();

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
}

package com.workfront.usernotebuilder.filter;

import com.workfront.usernotebuilder.*;

public abstract class AbstractFilterTest extends AbstractUnitTest {
	public abstract void predicate() throws Exception;
	public abstract void chain() throws Exception;
}
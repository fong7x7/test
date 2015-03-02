package com.workfront.usernotebuilder.handler;

import com.attask.common.*;
import com.attask.component.api.*;
import com.attask.event.*;
import com.attask.sdk.api.*;
import com.attask.sdk.enums.internal.*;
import com.attask.sdk.model.internal.*;
import com.attask.util.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.config.*;
import com.workfront.usernotebuilder.event.*;
import com.workfront.usernotebuilder.stereotype.*;
import com.workfront.usernotebuilder.util.*;
import lombok.extern.apachecommons.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * A handler for announcement related events.
 * Must collect admins for the customers to be notified and filter on opt-out and defaults.
 */
@Component("ANNOUNCEMENT")
@Handler(UserNoteEventHandlerEnum.ANNOUNCEMENT)
@CommonsLog
public class AnnouncementEventHandler extends AbstractApplicationEventUserNoteHandler<AnnouncementAddEvent> {
	public AnnouncementEventHandler(ApiFactory apiFactory) {
		this();
		this.apiFactory = apiFactory;
	}

	public AnnouncementEventHandler() {}

	@Override
	protected Collection<AppGUID> collect(final AnnouncementAddEvent event, final Collection<AppGUID> collectIDs) throws AtTaskException {
		Collection<AppGUID> notifyIDs = Collections.emptyList();
		if (StringUtils.isInvalidID(event.getPreviewUserID())) {
			notifyIDs = getCustomerAdmins(event.getCustomerId());
		}

		return notifyIDs;
	}

	//CNC need opt-out filter, etc. Currently this will only use the base filter from abstract
	/*
	@Override
	protected Collection<AppGUID> filter(AnnouncementAddEvent event, Collection<AppGUID> filterIDs) {
		//CNC experiments, find a fluent filter...
		return filterIDs;
	}
	*/

	@Override
	protected UserNoteEvent getNotificationType(AnnouncementAddEvent event) {
		return UserNoteEvent.ANNOUNCEMENT_ADD;
	}

	@Cacheable(com.workfront.usernotebuilder.config.CacheConfig.CACHE_HANDLER)
	private Collection<AppGUID> getCustomerAdmins(String customerId) throws AtTaskException {
		final AppGUID customerGUID = AppGUID.builder().id(customerId).objCode(Customer.OBJCODE).build();
		final Map<String, Object> adminsQuery = getCustomerAdminsQuery(customerGUID);
		final API customerAdminApi = apiFactory.getCustomerAdminApi(customerGUID.getId());
		final RequestResult<List<User>> requestResult = customerAdminApi.search(
			User.OBJCODE, adminsQuery, Collections.singletonList(QueryConstants.ID));

		final List<User> users = requestResult.getData();
		Collection<AppGUID> collectIDs = Collections.emptySet();
		if (null != users && ! users.isEmpty()) {
			collectIDs = CollectorUtil.collectGUIDs(users);
		}
		return collectIDs;
	}

	private Map<String, Object> getCustomerAdminsQuery(final AppGUID customerID) {
		// Get all admins for all active customers by default
		final ImmutableMap<String, Object> queryMap = ImmutableMap.<String, Object>builder()
			.put("accessLevel:isAdmin", QueryConstants.TRUE)
			.put("customer:isDisabled", Boolean.FALSE)
			.put("isActive", Boolean.TRUE)
			.put("customerID", customerID.getId())
			.build();

		return queryMap;
	}

	@Override
	public String getEventHandlerName(AnnouncementAddEvent event) {
		return "default.eventhandler.announcement.add";
	}
}

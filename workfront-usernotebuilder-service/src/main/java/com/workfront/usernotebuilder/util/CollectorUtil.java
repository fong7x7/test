package com.workfront.usernotebuilder.util;

import com.attask.common.*;
import com.attask.sdk.model.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import com.workfront.usernotebuilder.event.*;
import lombok.extern.apachecommons.*;

import java.util.*;

/**
 * A collection of utility methods appropriate to message processing
 * handlers, largely focused on collector-stage work.
 * Consider renaming/reworking into alternate class structures if the
 * scope becomes too eager.
 */
@CommonsLog
public class CollectorUtil {
	private static final Function<APIObject, String> GET_IDS = new Function<APIObject, String>() {
		@Override
		public String apply(APIObject object) {
			return object.getID();
		}
	};

	private static final Function<APIObject, AppGUID> GET_GUIDS = new Function<APIObject, AppGUID>() {
		@Override
		public AppGUID apply(APIObject object) {
			try {
				return AppGUID.builder().id(object.getID()).objCode(object.getObjCode()).build();
			} catch (AtTaskException e) {
				log.error("Failed to create AppGUID from APIObject: ", e);
			}
			//return AppGUID.absent();
			return null;
		}
	};

	private static final Function<AppGUID, String> GUID_TO_STRING = new Function<AppGUID, String>() {
		@Override
		public String apply(AppGUID guid) {
			return guid.getId();
		}
	};

	public static Collection<AppGUID> collectGUIDs(Collection<? extends APIObject> objects) {
		return FluentIterable.from(objects)
			.transform(GET_GUIDS)
			.filter(Predicates.notNull())
			.toSet();
	}

	public static Collection<String> collectIDs(Collection<? extends APIObject> objects) {
		return FluentIterable.from(objects)
			.transform(GET_IDS)
			.filter(Predicates.notNull())
			.toSet();
	}

	public static Collection<String> collectIDs(final Collection<AppGUID> notifyIDs, final String objCode) {
		final Predicate<AppGUID> objCodeFilter = createObjCodeFilter(objCode);
		return FluentIterable.from(notifyIDs)
			.filter(objCodeFilter)
			.transform(GUID_TO_STRING)
			.filter(Predicates.notNull())
			.toSet();
	}

	static Predicate<AppGUID> createObjCodeFilter(final String objCode) {
		return new Predicate<AppGUID>() {
			@Override
			public boolean apply(final AppGUID guid) {
				return objCode.equals(guid.getObjCode());
			}
		};
	}

	static <T extends APIObject> Function<T, String> getObjectIDFunction() {
		return new Function<T, String>() {
			@Override
			public String apply(final T input) {
				return input.getID();
			}
		};
	}

	static <T extends APIObject> Collection<String> getObjectIDs(
		final Collection<T> objects)
	{
		Collection<String> objectIDs = Collections.emptyList();
		if (null != objects && (! objects.isEmpty())) {
			final Function<T, String> function = getObjectIDFunction();
			objectIDs = Collections2.transform(objects, function);
		}
		return objectIDs;
	}


	public static AppGUID generateAppGUID(String objCode, String id) throws AtTaskException {
		return id == null ? null : AppGUID.builder().id(id).objCode(objCode).build();
	}

	public static Collection<AppGUID> generateAppGUIDs(String objCode, String... ids) throws AtTaskException{
		return generateAppGUIDs(objCode, Arrays.asList(ids));
	}

	public static Collection<AppGUID> generateAppGUIDs(String objCode, Collection<String> ids) throws AtTaskException{
		//CNC any reason to be using List instead of set? Recommend to use Sets.
		List<AppGUID> guids = new ArrayList<>();
		if(ids != null) {
			for (String id : ids) {
				guids.add(generateAppGUID(objCode, id));
			}
		}
		return guids;
	}
}

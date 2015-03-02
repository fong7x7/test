package com.workfront.usernotebuilder.event;

import com.attask.common.*;
import lombok.*;

import java.util.*;

/**
 * Wrapping String GUIDs. Incorporates our legecy GUID validation rules.
 * A GUID is really a UUID and an ObjCode.
 */
@Data
public class AppGUID {
	private static final AppGUID ABSENT = new AppGUID("","");

	// CNC consider using UUID for id? - consider AppGUID extends UUID?
	private final String id;
	//TODO add rules for objcode validation... (6char, ...)
	private final String objCode;

	private AppGUID(String guid, String objCode) {
		this.id = guid;
		this.objCode = objCode;
	}

	public static AppGUID absent() {
		return ABSENT;
	}

	/**
	 * Copied from StringUtils.isGUID -- should transition here if accepted
	 * @param value the string to test as GUID
	 * @return true if the parameter represents a valid GUID
	 */
	public static boolean isGUID(String value) {
		if (value == null || value.length() != 32) return false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if(c <= 'f' && c >= 'a') {
				continue;
			}
			if(c <= '9' && c >= '0') {
				continue;
			}
			if(c <= 'F' && c >= 'A') {
				continue;
			}
			return false;
		}

		return true;
	}

	public static boolean areValidIDs(Collection<String> ids) {
		if (null != ids && ! ids.isEmpty()) {
			for (final String id : ids) {
				if (! AppGUID.isGUID(id)) {
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.TRUE;
	}

	public static boolean areValidIDs(String... ids) {
		if (null != ids) {
			return areValidIDs(Arrays.asList(ids));
		}
		return Boolean.FALSE;
	}

	public static AppGUIDBuilder builder() {
		return new AppGUIDBuilder();
	}

	/**
	 * Static builder implementation for AppGUID
	 */
	public static class AppGUIDBuilder {
		private String id;
		private String objCode;
		private Boolean isValid;

		private AppGUIDBuilder() {
			isValid = Boolean.FALSE;
		}

		public AppGUIDBuilder id(String id) {
			this.id = id;
			this.isValid = AppGUID.isGUID(id);
			return this;
		}

		public AppGUIDBuilder objCode(String objCode) {
			this.objCode = objCode;
			return this;
		}

		public AppGUID build() throws AtTaskException {
			final Boolean valid = isValid();
			if (null != valid && valid) {
				return new AppGUID(id, objCode);
			}
			throw new InvalidParameterException("id", id);
		}

		public Boolean isValid() {
			return isValid;
		}

		@Override
		public String toString() {
			return "AppGUID.AppGUIDBuilder(id = "
				+ this.id
				+ ", objCode = "
				+ this.objCode
				+ ")";
		}
	}
}

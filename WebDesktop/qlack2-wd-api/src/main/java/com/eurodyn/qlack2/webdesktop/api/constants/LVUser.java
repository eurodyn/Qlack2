package com.eurodyn.qlack2.webdesktop.api.constants;

/**
 * Lookup Values &amp; Constants pertaining to user settings, attributes, etc.
 *
 */
public class LVUser {
	
	// User attributes constants (used in aaa_user_attributes).
	public static enum ATTRIBUTES_LOOKUP {
		USERNAME("USERNAME"),
		FIRST_NAME("NAME"),
		LAST_NAME("SURNAME"),
		EMAIL("EMAIL"),
		PHONE("PHONE"),
		MOBILE("MOBILE");
		
		private String value;
		ATTRIBUTES_LOOKUP(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
	}
}

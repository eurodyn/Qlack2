package com.eurodyn.qlack2.webdesktop.api.constants;

public class LVSettings {
	public static String SYSTEM_OWNER = "WD";
	
	// User settings.
	public static enum USER_LOOKUP {
		SETTING1("setting1");
		
		private String value;
		USER_LOOKUP(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
	}
	
	// System settings.
	public static enum SYSTEM_LOOKUP {
		SETTING1("setting1");
		
		private String value;
		SYSTEM_LOOKUP(String value) {
			this.value = value;
		}
		public String getValue() {
			return value;
		}
	}
}

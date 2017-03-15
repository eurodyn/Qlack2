package com.eurodyn.qlack2.webdesktop.apps.appmanagement.impl.util;

public class AuditConstants {
	public static enum EVENT {
		VIEW, UPDATE;
		@Override
		public String toString() {
			return "AUDIT_EVENT_" + super.toString();
		}
	}

	public static enum GROUP {
		APPLICATION, SECURE_OPERATIONS;
		@Override
		public String toString() {
			return "AUDIT_GROUP_" + super.toString();
		}
	}

	public static enum LEVEL {
		WD_APPMANAGEMENT;
		@Override
		public String toString() {
			return "AUDIT_LEVEL_" + super.toString();
		}
	}
}

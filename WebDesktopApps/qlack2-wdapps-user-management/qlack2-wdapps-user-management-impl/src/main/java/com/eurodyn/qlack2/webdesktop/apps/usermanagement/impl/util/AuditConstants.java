package com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util;

public class AuditConstants {
	public static enum EVENT {
		VIEW, CREATE, UPDATE, DELETE, MOVE;
		@Override
		public String toString() {
			return "AUDIT_EVENT_" + super.toString();
		}
	}

	public static enum GROUP {
		USER, ALL_USERS, GROUP, ALL_GROUPS, DOMAIN, SECURE_OPERATIONS;
		@Override
		public String toString() {
			return "AUDIT_GROUP_" + super.toString();
		}
	}

	public static enum LEVEL {
		WD_USERMANAGEMENT;
		@Override
		public String toString() {
			return "AUDIT_LEVEL_" + super.toString();
		}
	}
}

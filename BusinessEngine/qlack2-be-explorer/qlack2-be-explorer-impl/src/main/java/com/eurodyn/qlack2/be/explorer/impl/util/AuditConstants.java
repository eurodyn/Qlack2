package com.eurodyn.qlack2.be.explorer.impl.util;

public class AuditConstants {
	public static enum EVENT {
		VIEW, VIEW_ALL, CREATE, UPDATE, DELETE;
		@Override
		public String toString() {
			return "AUDIT_EVENT_" + super.toString();
		}
	}

	public static enum GROUP {
		PROJECT, SECURE_OPERATIONS;
		@Override
		public String toString() {
			return "AUDIT_GROUP_" + super.toString();
		}
	}

	public static enum LEVEL {
		QBE_EXPLORER;
		@Override
		public String toString() {
			return "AUDIT_LEVEL_" + super.toString();
		}
	}
}

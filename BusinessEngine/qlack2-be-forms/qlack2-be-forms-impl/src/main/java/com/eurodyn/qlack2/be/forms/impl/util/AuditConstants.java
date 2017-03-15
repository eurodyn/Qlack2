package com.eurodyn.qlack2.be.forms.impl.util;

public class AuditConstants {
	public static enum EVENT {
		VIEW, CREATE, UPDATE, DELETE, LOCK, UNLOCK, FINALISE, ENABLE_TESTING, DISABLE_TESTING, EXPORT, IMPORT;
		@Override
		public String toString() {
			return "AUDIT_EVENT_" + super.toString();
		}
	}

	public static enum GROUP {
		PROJECT, CATEGORY, FORM, FORM_VERSION, ORBEON_FORM_VERSION_DEFINITION, ORBEON_FORM_VERSION, SECURE_OPERATIONS;
		@Override
		public String toString() {
			return "AUDIT_GROUP_" + super.toString();
		}
	}

	public static enum LEVEL {
		QBE_FORMS;
		@Override
		public String toString() {
			return "AUDIT_LEVEL_" + super.toString();
		}
	}
}

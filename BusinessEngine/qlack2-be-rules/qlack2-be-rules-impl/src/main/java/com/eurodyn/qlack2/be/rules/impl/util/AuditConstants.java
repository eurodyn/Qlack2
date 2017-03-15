package com.eurodyn.qlack2.be.rules.impl.util;

public class AuditConstants {
	public static enum EVENT {
		VIEW, VIEW_MODELS_JAR, CREATE, CAN_UPDATE, UPDATE, UPDATE_CONTENT, CAN_DELETE, DELETE,
		LOCK, UNLOCK, CAN_FINALISE, FINALISE, CAN_ENABLE_TESTING, CAN_DISABLE_TESTING, ENABLE_TESTING, DISABLE_TESTING,
		EXPORT, IMPORT,
		EXECUTE, SET, GET, MULTI_SET, MULTI_GET, FIRE, GET_RESULTS;
		@Override
		public String toString() {
			return "AUDIT_EVENT_" + super.toString();
		}
	}

	public static enum GROUP {
		PROJECT, CATEGORY, WORKING_SET, WORKING_SET_VERSION, RULE, RULE_VERSION, DATA_MODEL, DATA_MODEL_VERSION, LIBRARY, LIBRARY_VERSION,
		RUNTIME_KBASE, RUNTIME_KSESSION, RUNTIME_GLOBAL, RUNTIME_FACT, RUNTIME_QUERY, SECURE_OPERATIONS;
		@Override
		public String toString() {
			return "AUDIT_GROUP_" + super.toString();
		}
	}

	public static enum LEVEL {
		QBE_RULES;
		@Override
		public String toString() {
			return "AUDIT_LEVEL_" + super.toString();
		}
	}
}

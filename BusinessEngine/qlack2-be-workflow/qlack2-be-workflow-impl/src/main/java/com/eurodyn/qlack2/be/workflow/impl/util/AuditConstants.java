package com.eurodyn.qlack2.be.workflow.impl.util;

public class AuditConstants {
	public static enum EVENT {
		VIEW, CREATE, UPDATE, DELETE, LOCK, UNLOCK, FINALISE, ENABLE_TESTING, DISABLE_TESTING, EXPORT, IMPORT, START, STOP, RESUME, PAUSE;
		@Override
		public String toString() {
			return "AUDIT_EVENT_" + super.toString();
		}
	}

	public static enum GROUP {
		PROJECT, CATEGORY, WORKFLOW, WORKFLOW_VERSION, WORKFLOW_VERSION_INSTANCE, SECURE_OPERATIONS;
		@Override
		public String toString() {
			return "AUDIT_GROUP_" + super.toString();
		}
	}

	public static enum LEVEL {
		QBE_WORKFLOW;
		@Override
		public String toString() {
			return "AUDIT_LEVEL_" + super.toString();
		}
	}
	
	public static enum RUNTIME_GROUP {
		RUNTIME_WORKFLOW;
		@Override
		public String toString() {
			return "LOG_GROUP_" + super.toString();
		}
	}
}

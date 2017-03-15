package com.eurodyn.qlack2.be.rules.api.client;

public interface RulesResourceConsumer {
	public enum ResourceType {
		RULE_VERSION, DATA_MODEL_VERSION, LIBRARY_VERSION, WORKING_SET_VERSION;
	}
	public boolean canRemoveResource(String resourceId, ResourceType resourceType);
}

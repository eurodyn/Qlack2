package com.eurodyn.qlack2.be.forms.impl.consumer;

import javax.persistence.EntityManager;

import com.eurodyn.qlack2.be.forms.impl.model.Condition;
import com.eurodyn.qlack2.be.rules.api.client.RulesResourceConsumer;

public class RulesResourceConsumerImpl implements RulesResourceConsumer {
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	public boolean canRemoveResource(String resourceId, ResourceType resourceType) {
		boolean retVal = true;
		switch (resourceType) {
		case RULE_VERSION:
			if (Condition.getConditionsCountForRuleVersion(em, resourceId) > 0) {
				retVal = false;
			}
			break;
		case WORKING_SET_VERSION:
			if (Condition.getConditionsCountForWorkingSetVersion(em, resourceId) > 0) {
				retVal = false;
			}
			break;
		// No other rules resources are used by forms so in all other cases do nothing
		default:
			break;
		}
		return retVal;
	}

}

package com.eurodyn.qlack2.be.forms.api.request.version;

import java.util.Map;

import com.eurodyn.qlack2.be.forms.api.dto.ConditionDTO;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class ValidateFormVersionConditionsHierarchyRequest extends
		QSignedRequest {
	private Map<String, ConditionDTO> conditions;

	public Map<String, ConditionDTO> getConditions() {
		return conditions;
	}

	public void setConditions(Map<String, ConditionDTO> conditions) {
		this.conditions = conditions;
	}

}

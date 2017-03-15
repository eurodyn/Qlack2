package com.eurodyn.qlack2.be.forms.client.api.rules;

import java.io.Serializable;

public class PreconditionFact implements Serializable {
	private static final long serialVersionUID = 7843723397755615775L;

	private boolean valid = Boolean.FALSE;

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}

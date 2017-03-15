package com.eurodyn.qlack2.webdesktop.impl;

import com.eurodyn.qlack2.common.util.util.TokenHolder;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public abstract class BaseService {
	
	protected SignedTicket getTicket() {
		return SignedTicket.fromVal(TokenHolder.getToken());
	}
	
	protected String getUserID() {
		return getTicket().getUserID();
	}
	
	protected String getUsername() {
		return getTicket().getUsername();
	}	
	
}

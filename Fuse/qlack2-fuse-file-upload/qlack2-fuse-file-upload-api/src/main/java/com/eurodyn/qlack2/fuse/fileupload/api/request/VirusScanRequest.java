/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.fileupload.api.request;

import com.eurodyn.qlack2.fuse.idm.api.signing.QRequest;

public class VirusScanRequest extends QRequest {
	// The ID of the file to check (as present in FLU_FILE.ID).
	private String id;
	// A scan request can optionally provide the address of the ClamAV server to
	// use. If this is left empty, the default value from the service's cfg file
	// will be used (i.e. localhost:3310).
	private String clamAVHost;
	private int clamAVPort;
	
	public String getClamAVHost() {
		return clamAVHost;
	}

	public void setClamAVHost(String clamAVHost) {
		this.clamAVHost = clamAVHost;
	}

	public int getClamAVPort() {
		return clamAVPort;
	}

	public void setClamAVPort(int clamAVPort) {
		this.clamAVPort = clamAVPort;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public VirusScanRequest(String id) {
		super();
		this.id = id;
	}

	public VirusScanRequest() {
		super();
	}

}

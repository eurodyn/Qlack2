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
package com.eurodyn.qlack2.fuse.fileupload.api.response;


public class VirusScanResponse {
	// The ID of the file that was checked.
	private String id;
	// Indication of whether a virus was found or not
	private boolean virusFree;
	// A description of the result of the virus scan.
	private String virusScanDescription;
	
	public VirusScanResponse() {

	}

	public VirusScanResponse(String id, boolean virusFree,
			String virusScanDescription) {
		super();
		this.id = id;
		this.virusFree = virusFree;
		this.virusScanDescription = virusScanDescription;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isVirusFree() {
		return virusFree;
	}

	public void setVirusFree(boolean virusFree) {
		this.virusFree = virusFree;
	}

	public String getVirusScanDescription() {
		return virusScanDescription;
	}

	public void setVirusScanDescription(String virusScanDescription) {
		this.virusScanDescription = virusScanDescription;
	}

}

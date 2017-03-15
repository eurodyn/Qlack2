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

public class CheckChunkRequest extends QRequest {
	private String fileAlias;
	private long chunkNumber;

	public String getFileAlias() {
		return fileAlias;
	}

	public void setFileAlias(String fileAlias) {
		this.fileAlias = fileAlias;
	}

	public long getChunkNumber() {
		return chunkNumber;
	}

	public void setChunkNumber(long chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	public CheckChunkRequest() {

	}

}

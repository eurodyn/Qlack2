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
package com.eurodyn.qlack2.fuse.fileupload.impl.cleanup;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;

public class CleanupBean implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(CleanupBean.class.getName());
	private Thread cleanupThread;
	private boolean enableCleanup;
	private long cleanupInterval;
	private long cleanupThreshold;
	private int exceptionsCount = 0;
	private boolean status = false;
	private FileUpload fileUploadService;
	
	/**
	 * @param fileUploadService the fileUploadService to set
	 */
	public void setFileUploadService(FileUpload fileUploadService) {
		this.fileUploadService = fileUploadService;
	}

	public boolean isEnableCleanup() {
		return enableCleanup;
	}

	public void setEnableCleanup(boolean enableCleanup) {
		this.enableCleanup = enableCleanup;
	}

	public long getCleanupInterval() {
		return cleanupInterval;
	}

	public void setCleanupInterval(long cleanupInterval) {
		this.cleanupInterval = cleanupInterval;
	}

	public long getCleanupThreshold() {
		return cleanupThreshold;
	}

	public void setCleanupThreshold(long cleanupThreshold) {
		this.cleanupThreshold = cleanupThreshold;
	}

	public void init() {
		// No need to start the cleanup thread if the configuration does not
		// allow us to cleanup any tickets.
		if (enableCleanup) {
			cleanupThread = new Thread(this);
			cleanupThread.start();
			status = true;
			LOGGER.log(Level.FINE, "Started uploaded files cleanup daemon.");
		}
	}

	public void destroy() {
		if (cleanupThread != null) {
			cleanupThread.interrupt();
			status = false;
		}
	}

	public boolean getStatus() {
		return status;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted() && exceptionsCount < 3) {
				fileUploadService.cleanupExpired(System.currentTimeMillis() - cleanupThreshold);
				Thread.sleep(cleanupInterval);
			}
		} catch (InterruptedException ex) {
			status = false;
			LOGGER.log(Level.FINEST, "Uploaded files cleanup daemon interrupted.");
		}
	}

}

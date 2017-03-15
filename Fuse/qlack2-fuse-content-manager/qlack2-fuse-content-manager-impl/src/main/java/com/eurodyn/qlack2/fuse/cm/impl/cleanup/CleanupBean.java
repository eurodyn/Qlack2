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
package com.eurodyn.qlack2.fuse.cm.impl.cleanup;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Value;

import com.eurodyn.qlack2.fuse.cm.api.VersionService;

/**
 * A cleanup scheduler for file-based repositories. Deleting deep structures on
 * a synchronous operation affects negatively the user experience. This
 * scheduled task runs in the background and removes from the underlying
 * filesystem files (e.g. versions of a file) which are not present in the
 * database. This allows the front-end application to reply back to a "delete
 * folder" event very quickly, whereas the housekeeping tasks are performed
 * asynchronously on the background.
 *
 */
@Singleton
public class CleanupBean implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(CleanupBean.class.getName());
	private Thread cleanupThread;

	@Inject
	private VersionService versionService;
	
	@Value("${cleanupInterval}")
	private long cleanupInterval;
	
	@Value("${startupDelay}")
	private long startupDelay;
	
	@Value("${cycleLength}")
	private int cycleLength;
	
	@Value("${storageStrategy}")
	private String storageStrategy;

	@PostConstruct
	public void init() {
		cleanupThread = new Thread(this);
		cleanupThread.start();
		LOGGER.log(Level.FINE, "Started CM cleanup daemon.");
	}

	@PreDestroy
	public void destroy() {
		if (cleanupThread != null) {
			cleanupThread.interrupt();
		}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(startupDelay);
			while (!Thread.currentThread().isInterrupted()) {
				versionService.cleanupFS(cycleLength);
				Thread.sleep(cleanupInterval);
			}
		} catch (InterruptedException ex) {
			LOGGER.log(Level.FINEST, "CM cleanup daemon interrupted.");
		}
	}
}

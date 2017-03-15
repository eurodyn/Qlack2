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
package com.eurodyn.qlack2.fuse.eventpublisher.impl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;

public class EventPublisherServiceImpl implements EventPublisherService {
	private EventAdmin eventAdmin;
	private static final Logger logger = Logger
			.getLogger(EventPublisherServiceImpl.class.getName());

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	@Override
	public void publishAsync(Map<String, Object> data, String topic) {
		logger.log(Level.FINEST, "Publish async: ", new Object[]{topic, data});
		eventAdmin.postEvent(new Event(topic, data));
	}

	@Override
	public void publishSync(Map<String, Object> data, String topic) {
		logger.log(Level.FINEST, "Publish sync: ", new Object[]{topic, data});
		eventAdmin.sendEvent(new Event(topic, data));
	}

}

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
package com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers;

import java.io.Serializable;
import java.util.Date;

/**
 * The scheduler trigger.
 */
public abstract class SchedulerTrigger implements Serializable {

	private static final long serialVersionUID = -4076645891344887334L;

	/** The trigger name */
	private String triggerName;

	/** The trigger group */
	private String triggerGroup;

	/** The trigger start date */
	private Date startOn;

	/** The trigger end date */
	private Date endOn;

	/**
	 * Get the trigger name.
	 *
	 * @return the triggerName
	 */
	public String getTriggerName() {
		return triggerName;
	}

	/**
	 * Set the trigger name.
	 *
	 * @param triggerName the triggerName to set
	 */
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	/**
	 * Get the trigger group.
	 *
	 * @return the triggerGroup
	 */
	public String getTriggerGroup() {
		return triggerGroup;
	}

	/**
	 * Set the trigger group.
	 *
	 * @param triggerGroup the triggerGroup to set
	 */
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	/**
	 * Get the trigger start date.
	 *
	 * @return the startOn
	 */
	public Date getStartOn() {
		return startOn != null ? new Date(startOn.getTime()) : null;
	}

	/**
	 * Set the trigger start date.
	 *
	 * @param startOn the startOn to set
	 */
	public void setStartOn(Date startOn) {
		this.startOn = new Date(startOn.getTime());
	}

	/**
	 * Get the trigger end date.
	 *
	 * @return the endOn
	 */
	public Date getEndOn() {
		return new Date(endOn.getTime());
	}

	/**
	 * Set the trigger end date.
	 *
	 * @param endOn the endOn to set
	 */
	public void setEndOn(Date endOn) {
		this.endOn = new Date(endOn.getTime());
	}

}

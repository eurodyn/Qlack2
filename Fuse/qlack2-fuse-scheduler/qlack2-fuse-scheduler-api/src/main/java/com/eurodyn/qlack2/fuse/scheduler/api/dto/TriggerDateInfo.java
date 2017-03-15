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
package com.eurodyn.qlack2.fuse.scheduler.api.dto;

import java.io.Serializable;
import java.util.Date;

public class TriggerDateInfo implements Serializable {
	private static final long serialVersionUID = 5723489082968828793L;

	// time in reverse chronological order
	private Date endTime;
	private Date finalFireTime;
	private Date nextFireTime;
	private Date previousFireTime;
	private Date starTime;

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the finalFireTime
	 */
	public Date getFinalFireTime() {
		return finalFireTime;
	}

	/**
	 * @param finalFireTime the finalFireTime to set
	 */
	public void setFinalFireTime(Date finalFireTime) {
		this.finalFireTime = finalFireTime;
	}

	/**
	 * @return the nextFireTime
	 */
	public Date getNextFireTime() {
		return nextFireTime;
	}

	/**
	 * @param nextFireTime the nextFireTime to set
	 */
	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	/**
	 * @return the previousFireTime
	 */
	public Date getPreviousFireTime() {
		return previousFireTime;
	}

	/**
	 * @param previousFireTime the previousFireTime to set
	 */
	public void setPreviousFireTime(Date previousFireTime) {
		this.previousFireTime = previousFireTime;
	}

	/**
	 * @return the starTime
	 */
	public Date getStarTime() {
		return starTime;
	}

	/**
	 * @param starTime the starTime to set
	 */
	public void setStarTime(Date starTime) {
		this.starTime = starTime;
	}
}

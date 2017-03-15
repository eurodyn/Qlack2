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

import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;

/**
 * The scheduler wrapped trigger.
 */
public class SchedulerWrappedTrigger implements Serializable {

	private static final long serialVersionUID = -1056626737264339449L;

	/** The trigger start date */
	private Date startOn;

	/** The trigger end date */
	private Date endOn;

	/** The trigger name */
	private String triggerName;

	/** The trigger group */
	private String triggerGroup;

	/** The trigger fire type (daily, monthly, weekly, etc) */
	private Constants.TRIGGERS triggerType;

	/** The trigger cron expression */
	private String cronExpression;

	/** The trigger daily time */
	private String dailyTime;

	/** The trigger day of the week */
	private Constants.TRIGGER_DAYS weeklyDay;

	/** The trigger monthly period */
	private Constants.TRIGGER_MONTH_PERIOD monthlyPeriod;

	/** The trigger mis-fire policy */
	private Constants.TRIGGER_MISFIRE triggerMisfire;

	/**
	 * Get the trigger start date.
	 *
	 * @return the startOn
	 */
	public Date getStartOn() {
		return startOn;
	}

	/**
	 * Set the trigger start date.
	 *
	 * @param startOn the startOn to set
	 */
	public void setStartOn(Date startOn) {
		this.startOn = startOn;
	}

	/**
	 * Get the trigger end date.
	 *
	 * @return the endOn
	 */
	public Date getEndOn() {
		return endOn;
	}

	/**
	 * Set the trigger end date.
	 *
	 * @param endOn the endOn to set
	 */
	public void setEndOn(Date endOn) {
		this.endOn = endOn;
	}

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
	 * Get the trigger cron expression.
	 *
	 * @return the cronExpression
	 */
	public String getCronExpression() {
		return cronExpression;
	}

	/**
	 * Set the trigger cron expression.
	 *
	 * @param cronExpression the cronExpression to set
	 */
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * Get the trigger fire type.
	 *
	 * @return the triggerType
	 */
	public Constants.TRIGGERS getTriggerType() {
		return triggerType;
	}

	/**
	 * Set the trigger fire type (daily, Monthly, Weekly, etc)
	 *
	 * @param triggerType the triggerType to set
	 */
	public void setTriggerType(Constants.TRIGGERS triggerType) {
		this.triggerType = triggerType;
	}

	/**
	 * Get the trigger fire time of the day.
	 *
	 * @return the dailyTime
	 */
	public String getDailyTime() {
		return dailyTime;
	}

	/**
	 * Set the trigger fire time of the day.
	 *
	 * @param dailyTime the dailyTime to set
	 */
	public void setDailyTime(String dailyTime) {
		this.dailyTime = dailyTime;
	}

	/**
	 * Get the trigger fire day of the week.
	 *
	 * @return the weeklyDay
	 */
	public Constants.TRIGGER_DAYS getWeeklyDay() {
		return weeklyDay;
	}

	/**
	 * Set the trigger fire day of the week.
	 *
	 * @param weeklyDay the weeklyDay to set
	 */
	public void setWeeklyDay(Constants.TRIGGER_DAYS weeklyDay) {
		this.weeklyDay = weeklyDay;
	}

	/**
	 * Get the trigger fire period of the month.
	 *
	 * @return the monthlyPeriod
	 */
	public Constants.TRIGGER_MONTH_PERIOD getMonthlyPeriod() {
		return monthlyPeriod;
	}

	/**
	 * Set the trigger fire period of the month.
	 *
	 * @param monthlyPeriod the monthlyPeriod to set
	 */
	public void setMonthlyPeriod(Constants.TRIGGER_MONTH_PERIOD monthlyPeriod) {
		this.monthlyPeriod = monthlyPeriod;
	}

	/**
	 * Get the trigger mis-fire policy.
	 *
	 * @return the triggerMisfire
	 */
	public Constants.TRIGGER_MISFIRE getTriggerMisfire() {
		return triggerMisfire;
	}

	/**
	 * Set the trigger mis-fire policy.
	 *
	 * @param triggerMisfire the triggerMisfire to set
	 */
	public void setTriggerMisfire(Constants.TRIGGER_MISFIRE triggerMisfire) {
		this.triggerMisfire = triggerMisfire;
	}

}

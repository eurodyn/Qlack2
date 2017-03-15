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
package com.eurodyn.qlack2.fuse.scheduler.api.builders;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.eurodyn.qlack2.fuse.scheduler.api.exception.QSchedulerException;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerTrigger;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerWrappedTrigger;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;

/**
 * Builder for {@link SchedulerTrigger}.
 */
public class SchedulerTriggerBuilder {

	/** The trigger's start date */
	private Date startOn;

	/** The trigger's end date */
	private Date endOn;

	/** The trigger's name */
	private String triggerName;

	/** The trigger's group */
	private String triggerGroup;

	/** The trigger's run interval type (daily, weekly, etc.) */
	private Constants.TRIGGERS triggerType;

	/** The trigger's cron expression */
	private String cronExpression;

	/** The time of the day that the trigger will run (if daily) */
	private String dailyTime;

	/** The day of the week that the trigger will run (if weekly) */
	private Constants.TRIGGER_DAYS weeklyDay;

	/** The period of the month (first, last) that the trigger will run (if montlhy) */
	private Constants.TRIGGER_MONTH_PERIOD monthlyPeriod;

	/** The trigger's mis-fire action to be taken */
	private Constants.TRIGGER_MISFIRE triggerMisfire;

	/**
	 * Hide default constructor
	 */
	private SchedulerTriggerBuilder() {
	}

	/**
	 * Create new trigger.
	 *
	 * @return
	 */
	public static SchedulerTriggerBuilder newTrigger() {
		return new SchedulerTriggerBuilder();
	}

    /**
     * Set the trigger Identity (trigger name and trigger group).
     *
     * @param triggerName - the trigger name
     * @param triggerGroup - the trigger group
     * @return
     */
    public SchedulerTriggerBuilder withIdentity(String triggerName, String triggerGroup) {
        this.triggerName = triggerName;
        this.triggerGroup = triggerGroup;
        return this;
    }

    /**
     * Set the trigger start date.
     *
     * @param startOn - the start date
     * @return
     */
    public SchedulerTriggerBuilder startOn(Date startOn) {
        this.startOn = new Date(startOn.getTime());
        return this;
    }

    /**
     * Set the trigger end date.
     *
     * @param endOn - the end date
     * @return
     */
    public SchedulerTriggerBuilder endOn(Date endOn) {
        this.endOn = new Date(endOn.getTime());
        return this;
    }

	/**
	 * Set the trigger run interval type.
	 *
	 * @param triggerType - run interval type (daily, weekly, etc.)
	 * @return
	 */
	public SchedulerTriggerBuilder withTrigger(Constants.TRIGGERS triggerType) {
		this.triggerType = triggerType;
		return this;
	}

	/**
	 * Set the trigger cron expression.
	 *
	 * @param cronExpression - the cron expression
	 * @return
	 */
	public SchedulerTriggerBuilder withCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
		return this;
	}

	/**
	 * Set the trigger daily time (if the trigger runs on a daily, weekly or
	 * monthly basis only).
	 *
	 * @param dailyTime - the time (in HH:mm format, 5 digits)
	 * @return
	 */
	public SchedulerTriggerBuilder dailyTime(String dailyTime) {
		this.dailyTime = dailyTime;
		return this;
	}

    /**
     * Set the trigger weekly day (if the trigger runs on a weekly basis only).
     *
     * @param weeklyDay - the day of the week
     * @return
     */
    public SchedulerTriggerBuilder weeklyDay(Constants.TRIGGER_DAYS weeklyDay) {
        this.weeklyDay = weeklyDay;
        return this;
    }

	/**
	 * Set the trigger monthly period (if the trigger runs on a monthly basis
	 * only).
	 *
	 * @param monthlyPeriod - the monthly period
	 * @return
	 */
	public SchedulerTriggerBuilder monthlyPeriod(Constants.TRIGGER_MONTH_PERIOD monthlyPeriod) {
		this.monthlyPeriod = monthlyPeriod;
		return this;
	}

    /**
     * Set the trigger mis-fire policy.
     *
     * @param triggerMisfire - the mis-fire policy
     * @return
     */
    public SchedulerTriggerBuilder misfirePolicy(Constants.TRIGGER_MISFIRE triggerMisfire) {
        this.triggerMisfire = triggerMisfire;
        return this;
    }

	/**
	 * Build the new trigger.
	 *
	 * @return
	 * @throws QSchedulerException
	 */
	public SchedulerWrappedTrigger build() throws QSchedulerException {
		checkTriggerData();

		SchedulerWrappedTrigger swt = new SchedulerWrappedTrigger();
		if (StringUtils.isEmpty(triggerName)) {
			swt.setTriggerName("Trigger_" + UUID.randomUUID().toString());
		}
		else {
			swt.setTriggerName(triggerName);
		}
		if (StringUtils.isEmpty(triggerGroup)) {
			swt.setTriggerGroup("TriggerGroup_" + UUID.randomUUID().toString());
		}
		else {
			swt.setTriggerGroup(triggerGroup);
		}

		if (startOn == null) {
			swt.setStartOn(Calendar.getInstance().getTime());
		}
		else {
			swt.setStartOn(startOn);
		}
		if (endOn != null) {
			swt.setEndOn(endOn);
		}

		if (!StringUtils.isEmpty(cronExpression)) {
			swt.setCronExpression(cronExpression);
		}
		swt.setTriggerType(triggerType);
		swt.setDailyTime(dailyTime);
		swt.setWeeklyDay(weeklyDay);
		swt.setMonthlyPeriod(monthlyPeriod);
		swt.setTriggerMisfire(triggerMisfire);

		return swt;
	}

	/**
	 * Check that the trigger data passed, are valid.
	 *
	 * @throws QSchedulerException
	 */
	private void checkTriggerData() throws QSchedulerException {
		if (triggerType == null) {
			throw new IllegalArgumentException("Trigger does not have a type.");
		}
		switch (triggerType) {
		case ASAP:
			break;
		case Daily:
			if (StringUtils.isEmpty(dailyTime)) {
				throw new IllegalArgumentException(
						"Trigger type 'Daily' requires a 'dailyTime' attribute defined.");
			}
			if ((dailyTime.length() < 5) || (dailyTime.indexOf(":") != 2)) {
				throw new IllegalArgumentException(
						"Trigger type 'Daily' requires a 'dailyTime' attribute defined as HH:mi, i.e. 07:19.");
			}
			break;
		case Weekly:
			if (StringUtils.isEmpty(dailyTime)) {
				throw new IllegalArgumentException(
						"Trigger type 'Weekly' requires a 'dailyTime' attribute defined.");
			}
			if (weeklyDay == null) {
				throw new IllegalArgumentException(
						"Trigger type 'Weekly' requires a 'weeklyDay' attribute defined.");
			}
			break;
		case Monthly:
			if (StringUtils.isEmpty(dailyTime)) {
				throw new IllegalArgumentException(
						"Trigger type 'Monthly' requires a 'dailyTime' attribute defined.");
			}
			if (monthlyPeriod == null) {
				throw new IllegalArgumentException(
						"Trigger type 'Monthly' requires a 'monthlyPeriod' attribute defined.");
			}
			break;
		case Cron:
			if (StringUtils.isEmpty(cronExpression)) {
				throw new IllegalArgumentException(
						"Trigger type 'Cron' requires a 'cronExpression' attribute defined.");
			}
			break;
		}
	}
}

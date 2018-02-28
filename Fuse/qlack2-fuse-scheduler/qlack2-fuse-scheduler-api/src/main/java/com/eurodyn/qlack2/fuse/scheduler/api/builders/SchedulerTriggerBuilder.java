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

import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerTrigger;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerWrappedTrigger;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Builder for {@link SchedulerTrigger}.
 */
public class SchedulerTriggerBuilder {

  /**
   * The trigger's start date
   */
  private Date startOn;

  /**
   * The trigger's end date
   */
  private Date endOn;

  /**
   * The trigger's name
   */
  private String triggerName;

  /**
   * The trigger's group
   */
  private String triggerGroup;

  /**
   * The trigger's run interval type (daily, weekly, etc.)
   */
  private Constants.TRIGGERS triggerType;

  /**
   * The trigger's cron expression
   */
  private String cronExpression;

  /**
   * The trigger's interval in seconds
   */
  private int intervalInSeconds;

  /**
   * The time of the day that the trigger will run (if daily)
   */
  private String dailyTime;

  /**
   * The day of the week that the trigger will run (if weekly)
   */
  private Constants.TRIGGER_DAYS weeklyDay;

  /**
   * The period of the month (first, last) that the trigger will run (if montlhy)
   */
  private Constants.TRIGGER_MONTH_PERIOD monthlyPeriod;

  /**
   * The trigger's mis-fire action to be taken
   */
  private Constants.TRIGGER_MISFIRE triggerMisfire;

  // The amount of seconds to delay the execution of this trigger. This goes together with the
  // TRIGGERS.DELAYED type and it is only for jobs executing once into the future.
  private long delay = 0;

  /**
   * Hide default constructor
   */
  private SchedulerTriggerBuilder() {
  }

  /**
   * Create new trigger.
   */
  public static SchedulerTriggerBuilder newTrigger() {
    return new SchedulerTriggerBuilder();
  }

  /**
   * Set the trigger Identity (trigger name and trigger group).
   *
   * @param triggerName - the trigger name
   * @param triggerGroup - the trigger group
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
   */
  public SchedulerTriggerBuilder startOn(Date startOn) {
    this.startOn = new Date(startOn.getTime());
    return this;
  }

  /**
   * Set the trigger end date.
   *
   * @param endOn - the end date
   */
  public SchedulerTriggerBuilder endOn(Date endOn) {
    this.endOn = new Date(endOn.getTime());
    return this;
  }

  /**
   * Set the trigger run interval type.
   *
   * @param triggerType - run interval type (daily, weekly, etc.)
   */
  public SchedulerTriggerBuilder withTrigger(Constants.TRIGGERS triggerType) {
    this.triggerType = triggerType;
    return this;
  }

  /**
   * Set the trigger cron expression.
   *
   * @param cronExpression - the cron expression
   */
  public SchedulerTriggerBuilder withCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
    return this;
  }

  /**
   * Set the interval.
   *
   * @param seconds - the interval in seconds
   */
  public SchedulerTriggerBuilder withIntervalInSeconds(int seconds) {
    this.intervalInSeconds = seconds;
    return this;
  }

  /**
   * Set the trigger daily time (if the trigger runs on a daily, weekly or
   * monthly basis only).
   *
   * @param dailyTime - the time (in HH:mm format, 5 digits)
   */
  public SchedulerTriggerBuilder dailyTime(String dailyTime) {
    this.dailyTime = dailyTime;
    return this;
  }

  /**
   * Set the trigger weekly day (if the trigger runs on a weekly basis only).
   *
   * @param weeklyDay - the day of the week
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
   */
  public SchedulerTriggerBuilder monthlyPeriod(Constants.TRIGGER_MONTH_PERIOD monthlyPeriod) {
    this.monthlyPeriod = monthlyPeriod;
    return this;
  }

  /**
   * Set the trigger mis-fire policy.
   *
   * @param triggerMisfire - the mis-fire policy
   */
  public SchedulerTriggerBuilder misfirePolicy(Constants.TRIGGER_MISFIRE triggerMisfire) {
    this.triggerMisfire = triggerMisfire;
    return this;
  }

  public SchedulerTriggerBuilder withDelay(long numberOfSeconds) {
    this.delay = numberOfSeconds;
    return this;
  }

  /**
   * Build the new trigger.
   */
  public SchedulerWrappedTrigger build() {
    checkTriggerData();

    SchedulerWrappedTrigger swt = new SchedulerWrappedTrigger();
    if (StringUtils.isEmpty(triggerName)) {
      swt.setTriggerName("Trigger_" + UUID.randomUUID().toString());
    } else {
      swt.setTriggerName(triggerName);
    }
    if (StringUtils.isEmpty(triggerGroup)) {
      swt.setTriggerGroup("TriggerGroup_" + UUID.randomUUID().toString());
    } else {
      swt.setTriggerGroup(triggerGroup);
    }

    if (startOn == null) {
      swt.setStartOn(Calendar.getInstance().getTime());
    } else {
      swt.setStartOn(startOn);
    }
    if (endOn != null) {
      swt.setEndOn(endOn);
    }

    swt.setDelay(delay);

    if (!StringUtils.isEmpty(cronExpression)) {
      swt.setCronExpression(cronExpression);
    }
    swt.setInterval(intervalInSeconds, TimeUnit.SECONDS);
    swt.setTriggerType(triggerType);
    swt.setDailyTime(dailyTime);
    swt.setWeeklyDay(weeklyDay);
    swt.setMonthlyPeriod(monthlyPeriod);
    swt.setTriggerMisfire(triggerMisfire);

    return swt;
  }

  /**
   * Check that the trigger data passed, are valid.
   */
  private void checkTriggerData() {
    if (triggerType == null) {
      throw new IllegalArgumentException("Trigger does not have a type.");
    }
    switch (triggerType) {
      case DELAYED:
        if (startOn != null) {
          throw new IllegalArgumentException("A delayed trigger can not have a startOn date.");
        }
        if (endOn != null) {
          throw new IllegalArgumentException("A delayed trigger can not have an endOn date.");
        }
        if (delay < 0) {
          throw new IllegalArgumentException("Delay must be >= 0.");
        }
        break;
      case Interval:
        if (intervalInSeconds <= 0) {
          throw new IllegalArgumentException(
            "Trigger type 'Interval' requires the 'interval' attribute set to a positive integer.");
        }
        break;
      case Daily:
        if (StringUtils.isEmpty(dailyTime)) {
          throw new IllegalArgumentException(
            "Trigger type 'Daily' requires a 'dailyTime' attribute defined.");
        }
        if ((dailyTime.length() != 5) || (dailyTime.charAt(2) != ':')) {
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
      default:
        break;
    }
  }
}

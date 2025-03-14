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
package com.eurodyn.qlack2.fuse.scheduler.impl.utils;

import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerWrappedJob;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerWrappedTrigger;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author European Dynamics S.A.
 */
public class QuartzConverters {

  private QuartzConverters() {

  }

  /**
   * Get a Quartz job object that contains all the details passed from the Scheduler wrapped job.
   *
   * @return a JobDetail object containing the details passed
   */
  public static JobDetail getQuartzJob(SchedulerWrappedJob job) {
    JobDataMap jdm = new JobDataMap();
    if (job.getDataMap() != null) {
      jdm.putAll(job.getDataMap());
    }
    jdm.put(Constants.QSCH_JOB_QUALIFIER, job.getJobQualifier());

    return JobBuilder.newJob(ServiceInvokerJob.class)
      .withIdentity(job.getJobName(), job.getJobGroup())
      .usingJobData(jdm)
      .storeDurably(job.isDurable())
      .build();
  }

  /**
   * Get a Quartz trigger object that contains all the details passed from the Scheduler wrapped
   * trigger.
   *
   * @param trigger - the Scheduler wrapped trigger passed
   * @param jobName - the job's name
   * @param jobGroup - the job's group
   * @throws ParseException might be thrown during the parsing of Quartz CronExpression.
   */
  @SuppressWarnings("squid:S1301")
  public static Trigger getQuartzTrigger(SchedulerWrappedTrigger trigger, String jobName,
    String jobGroup) throws ParseException {
    TriggerBuilder<Trigger> tb = TriggerBuilder.newTrigger();
    tb.startAt(trigger.getStartOn());
    if (trigger.getEndOn() != null) {
      tb.endAt(trigger.getEndOn());
    }
    tb.withIdentity(trigger.getTriggerName(), trigger.getTriggerGroup());
    tb.forJob(jobName, jobGroup);

    CronExpression ce = null;
    switch (trigger.getTriggerType()) {
      case DELAYED:
        Date startEndDate = Date.from(Instant.now().plus(trigger.getDelay(), ChronoUnit.SECONDS));
        tb.startAt(startEndDate);
        tb.endAt(startEndDate);
        break;
      case Interval:
        tb.withSchedule(SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInSeconds((int) trigger.getInterval(
            TimeUnit.SECONDS)) // Casting to int is safe for intervals less than 68 years
          .repeatForever()
        );
        break;
      case Daily:
        ce = new CronExpression("0 " + trigger.getDailyTime().substring(3) + " "
          + trigger.getDailyTime().substring(0, 2) + " ? * *");
        break;
      case Weekly:
        ce = new CronExpression("0 " + trigger.getDailyTime().substring(3) + " "
          + trigger.getDailyTime().substring(0, 2) + " ? * " + trigger.getWeeklyDay().toString());
        break;
      case Monthly:
        String dom = "";
        switch (trigger.getMonthlyPeriod()) {
          case FIRST:
            dom = "1";
            break;
          case LAST:
            dom = "L";
            break;
        }
        ce = new CronExpression("0 " + trigger.getDailyTime().substring(3) + " "
          + trigger.getDailyTime().substring(0, 2) + " " + dom + " * ?");
        break;
      case Cron:
        ce = new CronExpression(trigger.getCronExpression());
        break;
      default:
        break;
    }

    if (ce != null) {
      CronTriggerImpl cti = new CronTriggerImpl();
      cti.setCronExpression(ce);
      cti.setMisfireInstruction(getMisfireInstruction(trigger));
      tb.withSchedule(cti.getScheduleBuilder());
    }

    /*
     * if trigger priority is not null set the given priority
     * else priority is set automatically to default value(5).
     */
    if (trigger.getPriority() != null) {
      tb.withPriority(trigger.getPriority());
    }

    return tb.build();
  }

  /**
   * Set misfire instruction for trigger.
   */
  private static int getMisfireInstruction(SchedulerWrappedTrigger trigger) {
    int misfire = CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY;

    if (trigger.getTriggerMisfire() != null) {
      switch (trigger.getTriggerMisfire()) {
        case MISFIRE_INSTRUCTION_DO_NOTHING:
          misfire = CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING;
          break;
        case MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
          misfire = CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW;
          break;
        case MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
          misfire = CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY;
          break;
      }
    }

    return misfire;
  }

}

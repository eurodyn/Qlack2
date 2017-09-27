package com.eurodyn.qlack2.fuse.scheduler.it.util;

import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerWrappedJob;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerWrappedTrigger;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;
import java.util.Date;

public class TestUtilities {

    public static SchedulerWrappedJob createSchedulerWrappedJob(){
        SchedulerWrappedJob schedulerWrappedJob = new SchedulerWrappedJob();
        schedulerWrappedJob.setJobName(TestConst.generateRandomString());
        schedulerWrappedJob.setJobGroup(TestConst.generateRandomString());
        schedulerWrappedJob.setJobQualifier(TestConst.generateRandomString());
        schedulerWrappedJob.setDurable(true);

        return schedulerWrappedJob;
    }

    public static SchedulerWrappedTrigger createSchedulerWrappedTrigger(){
        SchedulerWrappedTrigger schedulerWrappedTrigger = new SchedulerWrappedTrigger();
        schedulerWrappedTrigger.setCronExpression(TestConst.generateRandomString());
        schedulerWrappedTrigger.setDailyTime(new Date().toString());
        schedulerWrappedTrigger.setStartOn(new Date());
        schedulerWrappedTrigger.setEndOn(new Date());
        schedulerWrappedTrigger.setMonthlyPeriod(Constants.TRIGGER_MONTH_PERIOD.FIRST);
        schedulerWrappedTrigger.setTriggerGroup(TestConst.generateRandomString());
        schedulerWrappedTrigger.setTriggerType(Constants.TRIGGERS.ASAP);
        schedulerWrappedTrigger.setTriggerMisfire(Constants.TRIGGER_MISFIRE.MISFIRE_INSTRUCTION_DO_NOTHING);
        schedulerWrappedTrigger.setWeeklyDay(Constants.TRIGGER_DAYS.MON);
        schedulerWrappedTrigger.setTriggerName(TestConst.generateRandomString());

        return schedulerWrappedTrigger;
    }
}

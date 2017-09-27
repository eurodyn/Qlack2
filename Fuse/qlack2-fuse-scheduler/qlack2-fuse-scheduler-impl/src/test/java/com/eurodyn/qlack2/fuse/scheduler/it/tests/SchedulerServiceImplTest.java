package com.eurodyn.qlack2.fuse.scheduler.it.tests;

import com.eurodyn.qlack2.fuse.scheduler.api.SchedulerService;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerWrappedJob;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerWrappedTrigger;
import com.eurodyn.qlack2.fuse.scheduler.it.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.scheduler.it.util.TestUtilities;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SchedulerServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  SchedulerService schedulerService;

  @Test
  public void scheduleJob() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    Assert.assertNotNull(schedulerService
      .getJobDetails(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(), false));
  }

  @Test
  public void getSchedulerName() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    Assert.assertEquals("QuartzScheduler", schedulerService.getSchedulerName());
  }

  @Test
  public void standby() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService.standby();
    Assert.assertTrue(schedulerService.isInStandbyMode());
  }

  @Test
  public void rescheduleJob() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    Assert.assertNotNull(schedulerService
      .getJobDetails(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(), false));

    SchedulerWrappedTrigger schedulerNewWrappedTrigger = TestUtilities
      .createSchedulerWrappedTrigger();
    schedulerService
      .rescheduleJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(),
        schedulerNewWrappedTrigger);
    Assert.assertNotNull(schedulerService
      .getJobDetails(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(), false));
  }

  @Test
  public void deleteJob() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    Assert.assertNotNull(schedulerService
      .getJobDetails(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(), false));

    schedulerService.deleteJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());
    Assert.assertNull(schedulerService
      .getJobDetails(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(), false));
  }

  @Test
  public void deleteJobs() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    Assert.assertNotNull(schedulerService
      .getJobDetails(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(), false));

    Map<String, String> jobs = new HashMap<>();
    jobs.put(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());

    schedulerService.deleteJobs(jobs);
    Assert.assertNull(schedulerService
      .getJobDetails(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(), false));
  }

  @Test
  public void triggerJob() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();
    Date date = new Date();
    schedulerWrappedTrigger.setEndOn(date);
    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService
      .triggerJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());

    Assert.assertNotNull(schedulerService
      .getDateInfoForTrigger(schedulerWrappedTrigger.getTriggerName(),
        schedulerWrappedTrigger.getTriggerGroup()));
    Assert.assertEquals(date, schedulerService
      .getDateInfoForTrigger(schedulerWrappedTrigger.getTriggerName(),
        schedulerWrappedTrigger.getTriggerGroup()).getEndTime());
  }

  @Test
  public void pauseJob() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService
      .triggerJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());
    schedulerService.pauseJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());

    Assert.assertTrue(schedulerService.getCurrentlyExecutingJobsNames().isEmpty());
  }

  @Test
  public void pauseTrigger() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService
      .triggerJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());
    schedulerService.pauseTrigger(schedulerWrappedTrigger.getTriggerName(),
      schedulerWrappedTrigger.getTriggerGroup());

    Assert.assertTrue(schedulerService.getCurrentlyExecutingJobsNames().isEmpty());
  }

  @Test
  public void pauseAll() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService
      .triggerJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());
    schedulerService.pauseAll();

    Assert.assertTrue(schedulerService.getCurrentlyExecutingJobsNames().isEmpty());
  }

  @Test
  public void resumeAll() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService
      .triggerJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());
    schedulerService.pauseAll();
    schedulerService.resumeAll();

    Assert.assertNotNull(schedulerService.getCurrentlyExecutingJobsNames());
  }

  @Test
  public void clear() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService
      .triggerJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());
    schedulerService.clear();

    Assert.assertNull(schedulerService
      .getJobDetails(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup(), false));
  }

  @Test
  public void checkExistJob() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService
      .triggerJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());

    Assert.assertNotNull(schedulerService
      .checkExistJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup()));
  }

  @Test
  public void checkExistTrigger() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService
      .triggerJob(schedulerWrappedJob.getJobName(), schedulerWrappedJob.getJobGroup());

    Assert.assertNotNull(schedulerService
      .checkExistTrigger(schedulerWrappedTrigger.getTriggerName(),
        schedulerWrappedTrigger.getTriggerGroup()));
  }

  @Test
  public void zshutdown() {
    SchedulerWrappedJob schedulerWrappedJob = TestUtilities.createSchedulerWrappedJob();
    SchedulerWrappedTrigger schedulerWrappedTrigger = TestUtilities.createSchedulerWrappedTrigger();

    schedulerService.scheduleJob(schedulerWrappedJob, schedulerWrappedTrigger);
    schedulerService.start();
    schedulerService.shutdown();
    Assert.assertTrue(schedulerService.isShutdown());
  }
}



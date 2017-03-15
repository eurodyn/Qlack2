package com.eurodyn.qlack2.fuse.scheduler.impl;

import com.eurodyn.qlack2.fuse.scheduler.api.SchedulerService;
import com.eurodyn.qlack2.fuse.scheduler.api.builders.SchedulerJobBuilder;
import com.eurodyn.qlack2.fuse.scheduler.api.builders.SchedulerTriggerBuilder;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import javax.inject.Inject;
import java.io.File;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SchedulerServiceImplTest {
    @Inject
    SchedulerService schedulerService;

    @Configuration
    public Option[] config() {
        MavenArtifactUrlReference karafUrl = maven()
                .groupId("org.apache.karaf")
                .artifactId("apache-karaf")
                .version(karafVersion())
                .type("zip");

        MavenUrlReference karafStandardRepo = maven()
                .groupId("org.apache.karaf.features")
                .artifactId("standard")
                .version(karafVersion())
                .classifier("features")
                .type("xml");

        MavenUrlReference qlackFuseRepo = maven()
                .groupId("com.eurodyn.qlack2.fuse")
                .artifactId("qlack2-fuse-karaf-features")
                //TODO pass version similarly to karafVersion.
                .version("2.0.0")
                .classifier("features")
                .type("xml");

        return new Option[] {
                karafDistributionConfiguration()
                        .frameworkUrl(karafUrl)
                        .unpackDirectory(new File("target", "exam"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                copyCfg("etc/org.openengsb.labs.liquibase.cfg"),
                copyCfg("etc/org.ops4j.datasource-qlack.cfg"),
                logLevel(LogLevelOption.LogLevel.INFO),
                configureConsole().ignoreLocalConsole(),
                features(karafStandardRepo , "scr", "pax-jdbc-h2"),
                features(qlackFuseRepo, "qlack2-fuse-scheduler"),
                features(qlackFuseRepo, "qlack2-util-repack-liquibase")
        };
    }

    public static Option copyCfg(String path) {
        return replaceConfigurationFile(path, new File("src/test/resources/" + path));
    }

    public static String karafVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        String karafVersion = cm.getProperty("pax.exam.karaf.version");
        return karafVersion;
    }

    @Test
    public void getSchedulerName() throws Exception {
        assertEquals(schedulerService.getSchedulerName(), "QuartzScheduler");
    }

    @Test
    public void getSchedulerInstanceID() throws Exception {
        assertEquals(schedulerService.getSchedulerInstanceID(), "NON_CLUSTERED");
    }

    @Test
    public void start() throws Exception {
        // By default the scheduler is started.
        assertTrue(schedulerService.isStarted());
    }

    @Test
    public void shutdown() throws Exception {
        assertTrue(schedulerService.isStarted());
        assertFalse(schedulerService.isShutdown());
        schedulerService.shutdown();
        assertTrue(schedulerService.isShutdown());
    }

    @Test
    public void standby() throws Exception {
        assertFalse(schedulerService.isInStandbyMode());
        schedulerService.standby();
        assertTrue(schedulerService.isInStandbyMode());
    }

    @Test
    public void scheduleJob() throws Exception {
        // Schedule a new job.
        schedulerService.scheduleJob(
            SchedulerJobBuilder
                    .newJob()
                    .withIdentity("job1", "group1")
                    .withJobQualifier("someTestJob")
                    .build(),
            SchedulerTriggerBuilder
                    .newTrigger()
                    .withIdentity("trg1", "trg-group1")
                    .withTrigger(Constants.TRIGGERS.ASAP)
                    .build()
        );
    }
//
//    @Test
//    public void rescheduleJob() throws Exception {
//
//    }
//
//    @Test
//    public void deleteJob() throws Exception {
//
//    }
//
//    @Test
//    public void deleteJobs() throws Exception {
//
//    }
//
//    @Test
//    public void triggerJob() throws Exception {
//
//    }
//
//    @Test
//    public void triggerJob1() throws Exception {
//
//    }
//
//    @Test
//    public void pauseJob() throws Exception {
//
//    }
//
//    @Test
//    public void pauseTrigger() throws Exception {
//
//    }
//
//    @Test
//    public void pauseAll() throws Exception {
//
//    }
//
//    @Test
//    public void resumeAll() throws Exception {
//
//    }
//
//    @Test
//    public void clear() throws Exception {
//
//    }
//
//    @Test
//    public void getJobDetails() throws Exception {
//
//    }
//
//    @Test
//    public void getDateInfoForTrigger() throws Exception {
//
//    }
//
//    @Test
//    public void getNextFireTimeForTrigger() throws Exception {
//
//    }
}

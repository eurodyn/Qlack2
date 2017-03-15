package com.eurodyn.qlack2.fuse.scheduler.impl;

import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerJob;

import java.util.Map;

public class TestJob implements SchedulerJob {
    @Override
    public void execute(Map<String, Object> dataMap) {
        System.out.println("**********************************************");
        System.out.println("* JOB EXECUTED                               *");
        System.out.println("**********************************************");
    }
}

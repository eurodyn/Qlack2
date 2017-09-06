package com.eurodyn.qlack2.fuse.scheduler.impl.utils;

public class Constants {
  public static final String QUARTZ_JOBSTORE_CLASS = "org.quartz.impl.jdbcjobstore.JobStoreCMT";
  public static final String QUARTZ_DRIVERDELEGATE_CLASS = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate";
  public static final String QUARTZ_TABLE_PREFIX = "sch_";
  public static final int QUARTZ_STARTUP_DELAY_SEC = 10;
  public static final int QUARTZ_THREADS = 4;
}

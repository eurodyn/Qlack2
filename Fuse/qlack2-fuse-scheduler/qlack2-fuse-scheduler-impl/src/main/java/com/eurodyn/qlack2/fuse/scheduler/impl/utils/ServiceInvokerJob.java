package com.eurodyn.qlack2.fuse.scheduler.impl.utils;

import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerJob;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceInvokerJob implements Job {

  @SuppressWarnings("unused")
  private static final Logger LOGGER = Logger.getLogger(ServiceInvokerJob.class.getName());

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

    JobDataMap jobDataMap = context.getMergedJobDataMap();
    String jobQualifier = jobDataMap.getString(Constants.QSCH_JOB_QUALIFIER);

    BundleContext bundleContext = FrameworkUtil.getBundle(ServiceInvokerJob.class)
      .getBundleContext();

    ServiceReference<SchedulerJob> serviceReference = findServiceReference(bundleContext,
      jobQualifier);
    if (serviceReference == null) {
      throw new JobExecutionException("Cannot get OSGi service reference to job " + jobQualifier);
    }

    Object service = bundleContext.getService(serviceReference);
    if (service == null) {
      throw new JobExecutionException(
        "The OSGi service for job " + jobQualifier + " is not registered");
    }

    SchedulerJob job = (SchedulerJob) service;

    try {
      job.execute(jobDataMap);
    } catch (Exception e) {
      throw new JobExecutionException(e);
    } finally {
      bundleContext.ungetService(serviceReference);
    }
  }

  private static ServiceReference<SchedulerJob> findServiceReference(BundleContext bundleContext,
    String jobQualifier) {
    ServiceReference<SchedulerJob> retVal = null;

    try {
      Collection<ServiceReference<SchedulerJob>> serviceReferences = bundleContext
        .getServiceReferences(SchedulerJob.class, null);
      for (ServiceReference<SchedulerJob> serviceReference : serviceReferences) {
        String currentJobQualifier = (String) serviceReference
          .getProperty(Constants.QSCH_JOB_QUALIFIER);
        if (jobQualifier.equals(currentJobQualifier)) {
          retVal = serviceReference;
          break;
        }
      }
    } catch (InvalidSyntaxException e) {
      LOGGER.log(Level.SEVERE, "Could not find a reference for SchedulerJob.", e);
    }

    return retVal;
  }

}

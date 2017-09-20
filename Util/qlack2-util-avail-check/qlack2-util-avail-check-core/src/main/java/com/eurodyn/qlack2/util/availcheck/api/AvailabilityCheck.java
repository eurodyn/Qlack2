package com.eurodyn.qlack2.util.availcheck.api;

import java.util.Map;

/**
 * The API for all availability checks.
 */
public interface AvailabilityCheck {

  /**
   * Checks whether the specified resource is available can ready to be used.
   *
   * @param url The URL of the resource to be checked.
   * @param user The username to use while connecting to the resource.
   * @param password The password to use while connecting to the resource.
   * @param maxWait The maximum amount of time (in msec) to wait for the resource to become
   * available.
   * @param cycleWait The amount of time to wait between consecutive checks until the resource
   * becomes available.
   * @param params A map with custom params to be used by the specific checker.
   *
   * @return Returns true, if the resource has become available or false otherwise.
   */
  boolean isAvailable(String url, String user, String password, long maxWait, long cycleWait,
    Map<String, Object> params);
}

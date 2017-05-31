package com.eurodyn.qlack2.util.dbcheck.api;

public interface DBCheck {
  /** The number of ms to wait between successive tries */
  final long WAIT_CYCLE = 2000;

  boolean isDBAcceptingConnection(String url, String user, String pass, long maxWait)
    throws ClassNotFoundException, InterruptedException;

}

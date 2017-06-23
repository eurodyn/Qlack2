package com.eurodyn.qlack2.util.availcheck.mysql;

import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;
import java.sql.*;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AvailabilityCheckMySQL implements AvailabilityCheck {

  /** JUL reference */
  private final static Logger LOGGER = Logger.getLogger(AvailabilityCheckMySQL.class.getName());

  /** MySQL JDBC driver class to use */
  private final static String DRIVER_NAME = "com.mysql.jdbc.Driver";

  /** MySQL query to use while checking the DB is responding */
  private final static String DB_CHECK_QUERY = "SELECT NOW() FROM DUAL";

  @Override
  public boolean isAvailable(String url, String user, String password, long maxWait, long cycleWait, Map<String, Object> params){
      boolean retVal = false;

      try {
          Class.forName(DRIVER_NAME);
          long startTime = Instant.now().toEpochMilli();
          while (Instant.now().toEpochMilli() - startTime < maxWait && !retVal) {
              try (Connection connection = DriverManager.getConnection(url, user, password)){
                  try (Statement statement = connection.createStatement()) {
                      try (ResultSet rs = statement.executeQuery(DB_CHECK_QUERY)) {
                          rs.next();
                      }
                  }
                  retVal = true;
              } catch (SQLException ignore) {
              }
              Thread.sleep(cycleWait);
          }
      } catch (ClassNotFoundException | InterruptedException e) {
          LOGGER.log(Level.SEVERE, e.getMessage(), e);
      }

      return retVal;
  }

}
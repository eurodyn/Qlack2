package com.eurodyn.qlack2.util.availcheck.mariadb;

import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AvailabilityCheckMariaDB implements AvailabilityCheck {

  // JUL reference
  private final static Logger LOGGER = Logger.getLogger(AvailabilityCheckMariaDB.class.getName());

  // MariaDB JDBC driver class to use
  private final static String DRIVER_NAME = "org.mariadb.jdbc.Driver";

  // MariaDB query to use while checking the DB is responding
  private final static String DB_CHECK_QUERY = "SELECT 1";

  @Override
  public boolean isAvailable(String url, String user, String password, long maxWait, long cycleWait,
    Map<String, Object> params) {
    boolean retVal = false;

    LOGGER.log(Level.INFO, "Checking availability of MariaDB: url={0}, user={1}, password={2}, "
      + "maxWait={3}, cycleWait={4}", new Object[]{url, user, password,  maxWait,  cycleWait});

    try {
      Class.forName(DRIVER_NAME);
      long startTime = Instant.now().toEpochMilli();
      while (Instant.now().toEpochMilli() - startTime < maxWait && !retVal) {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
          try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(DB_CHECK_QUERY)) {
              rs.next();
            }
          }
          retVal = true;
        } catch (SQLException e) {
          LOGGER.log(Level.FINEST, e.getMessage(), e);
        }
        Thread.sleep(cycleWait);
      }
    } catch (ClassNotFoundException | InterruptedException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }

    return retVal;
  }

}
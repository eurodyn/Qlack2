package com.eurodyn.qlack2.util.dbcheck.impl.oracle;

import com.eurodyn.qlack2.util.dbcheck.api.DBCheck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

public class DBCheckImpl implements DBCheck {
  private final static String DRIVER_NAME = "oracle.jdbc.OracleDriver";

  @Override
  public boolean isDBAcceptingConnection(String url, String user, String pass, long maxWait)
    throws ClassNotFoundException, InterruptedException {
    Class.forName(DRIVER_NAME);

    long startTime = Instant.now().toEpochMilli();
    boolean isConnected = false;
    while (Instant.now().toEpochMilli() - startTime < maxWait && !isConnected) {
      try (Connection connection = DriverManager.getConnection(url, user, pass)){
        try (Statement statement = connection.createStatement()) {
          try (ResultSet rs = statement.executeQuery("SELECT SYSDATE FROM DUAL")) {
            rs.next();
          }
        }
        isConnected = true;
      } catch (SQLException ignore) {
      }
      Thread.sleep(WAIT_CYCLE);
    }

    return isConnected;
  }
}
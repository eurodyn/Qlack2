package com.eurodyn.qlack2.fuse.simm;

import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.replaceConfigurationFile;

public class TestUtil {

    public static String karafVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        return cm.getProperty("pax.exam.karaf.version");
    }

    public static String projectVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        return cm.getProperty("project.version");
    }

    public static Option copyITConf(String path) {
        return replaceConfigurationFile(path, new File("src/test/conf/it/" + path));
    }

    public static boolean isMySQLAcceptingConnection(String url, String user, String pass) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, pass);
            connection.close();
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException ignore) {
                }
        }
    }

}
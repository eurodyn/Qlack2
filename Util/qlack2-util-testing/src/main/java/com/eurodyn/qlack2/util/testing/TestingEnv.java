package com.eurodyn.qlack2.util.testing;

import com.eurodyn.qlack2.util.networking.NetworkingUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Properties;

/**
 * Encapsulates all the runtime parameters for the tests to be executed.
 */
public class TestingEnv {

  /**
   * The host of the Docker Engine to use
   */
  private String dockerEngineHost;

  /**
   * The host of the Docker Engine to use
   */
  private int dockerEnginePort;

  /**
   * The database Docker image
   */
  private String dbImage;

  /**
   * The port on the host to expose the DB container's port
   */
  private int dbPortHost;

  /**
   * The port on the container on which the DB is listening
   */
  private int dbPortContainer;

  /**
   * Custom container environmental options to pass to the container
   */
  private Properties containerEnvParams = new Properties();

  /**
   * The class to use to check whether the DB is available prior to execute tests
   */
  private String dbAvailabilityCheckClass;

  /**
   * The DB user to connect with to the DB container
   */
  private String dbUser;

  /**
   * The DB user password to connect with to the DB container
   */
  private String dbPassword;

  /**
   * The JDBC URL to connect with to the DB container
   */
  private String dbUrl;

  /**
   * The maximum amount of time (in msec) waiting for the container to become available
   */
  private int containerMaxWait;

  /**
   * The amount of time to wait between successive probes of container's availability
   */
  private int containerWaitCycle;

  public int getContainerMaxWait() {
    return containerMaxWait;
  }

  public void setContainerMaxWait(int containerMaxWait) {
    this.containerMaxWait = containerMaxWait;
  }

  public int getContainerWaitCycle() {
    return containerWaitCycle;
  }

  public void setContainerWaitCycle(int containerWaitCycle) {
    this.containerWaitCycle = containerWaitCycle;
  }

  public Properties getContainerEnvParams() {
    return containerEnvParams;
  }

  public void setContainerEnvParams(Properties containerEnvParams) {
    this.containerEnvParams = containerEnvParams;
  }

  public String getDockerEngineHost() {
    return dockerEngineHost;
  }

  public void setDockerEngineHost(String dockerEngineHost) {
    this.dockerEngineHost = dockerEngineHost;
  }

  public String getDbImage() {
    return dbImage;
  }

  public void setDbImage(String dbImage) {
    this.dbImage = dbImage;
  }

  public int getDbPortHost() {
    return dbPortHost;
  }

  public void setDbPortHost(int dbPortHost) {
    this.dbPortHost = dbPortHost;
  }

  public int getDbPortContainer() {
    return dbPortContainer;
  }

  public void setDbPortContainer(int dbPortContainer) {
    this.dbPortContainer = dbPortContainer;
  }

  public String getDbAvailabilityCheckClass() {
    return dbAvailabilityCheckClass;
  }

  public void setDbAvailabilityCheckClass(String dbAvailabilityCheckClass) {
    this.dbAvailabilityCheckClass = dbAvailabilityCheckClass;
  }

  public String getDbUser() {
    return dbUser;
  }

  public void setDbUser(String dbUser) {
    this.dbUser = dbUser;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public void setDbPassword(String dbPassword) {
    this.dbPassword = dbPassword;
  }

  public String getDbUrl() {
    return dbUrl;
  }

  public void setDbUrl(String dbUrl) {
    this.dbUrl = dbUrl;
  }

  public int getDockerEnginePort() {
    return dockerEnginePort;
  }

  public void setDockerEnginePort(int dockerEnginePort) {
    this.dockerEnginePort = dockerEnginePort;
  }

  /**
   * Checks system properties and environmental variables and prepares the parameters required to
   * execute Pax-Exam tests. This class effectively masks out environment-specific configurations
   * providing a seamless interface to the underlying testing infrastructure.
   */
  public static TestingEnv generate() {
    TestingEnv testingEnv = new TestingEnv();

    /** The Docker Engine host to use */
    testingEnv.setDockerEngineHost(StringUtils.defaultIfBlank(System.getProperty(
      TestingConstants.SYSENV_DOCKER_ENGINE_HOST), TestingConstants.DEFAULT_DOCKER_ENGINE_HOST));

    /** The Docker Engine port to use */
    if (StringUtils.isBlank(System.getProperty(TestingConstants.SYSENV_DOCKER_ENGINE_PORT))) {
      testingEnv.setDockerEnginePort(TestingConstants.DEFAULT_DOCKER_ENGINE_PORT);
    } else {
      testingEnv.setDockerEnginePort(Integer.parseInt(
        System.getProperty(TestingConstants.SYSENV_DOCKER_ENGINE_PORT)));
    }

    /** The Docker database image to use */
    testingEnv.setDbImage(StringUtils.defaultIfBlank(System.getProperty(
      TestingConstants.SYSENV_DB_IMAGE), TestingConstants.DEFAULT_DB_IMAGE));

    /** The host DB container port */
    testingEnv.setDbImage(StringUtils.defaultIfBlank(System.getProperty(
      TestingConstants.SYSENV_DB_IMAGE), TestingConstants.DEFAULT_DB_IMAGE));

    /** The port on which the DB container's listening port is exposed to the host */
    if (StringUtils.isBlank(System.getProperty(TestingConstants.SYSENV_DB_PORT_HOST))) {
      testingEnv.setDbPortHost(NetworkingUtils.getEphemeralFreePort());
    } else {
      testingEnv
        .setDbPortHost(Integer.parseInt(System.getProperty(TestingConstants.SYSENV_DB_PORT_HOST)));
    }

    /** The port on which the DB container listens to */
    if (StringUtils.isBlank(System.getProperty(TestingConstants.SYSENV_DB_PORT_CONTAINER))) {
      testingEnv.setDbPortContainer(TestingConstants.DEFAULT_DB_PORT_CONTAINER);
    } else {
      testingEnv
        .setDbPortContainer(
          Integer.parseInt(System.getProperty(TestingConstants.SYSENV_DB_PORT_CONTAINER)));
    }

    /** If container environmental options have been specified use these, otherwise fill-in some
     * default ones for MySQL which is the default DB container we use for testing if the user
     * has specified nothing different.
     */
    if (!System.getProperties().keySet().parallelStream()
      .anyMatch(o -> ((String) o).startsWith(TestingConstants.SYSENV_ENV_OPTIONS_PREFIX))) {
      testingEnv.getContainerEnvParams().put("MYSQL_ROOT_PASSWORD", "root");
    } else {
      final Properties properties = System.getProperties();
      for (Object o : properties.keySet()) {
        String propertyName = (String) o;
        if (propertyName.startsWith(TestingConstants.SYSENV_ENV_OPTIONS_PREFIX)) {
          testingEnv.getContainerEnvParams().put(
            propertyName.substring(TestingConstants.SYSENV_ENV_OPTIONS_PREFIX.length()),
            System.getProperty(TestingConstants.SYSENV_ENV_OPTIONS_PREFIX));
        }
      }
    }

    /** The class to use to check for DB availability before starting tests */
    testingEnv.setDbAvailabilityCheckClass(StringUtils.defaultIfBlank(System.getProperty(
      TestingConstants.SYSENV_DB_AVAILABILITY_CLASS),
      TestingConstants.DEFAULT_DB_AVAILABILITY_CLASS));

    /** The user to connect with to the DB container */
    testingEnv.setDbUser(StringUtils.defaultIfBlank(System.getProperty(
      TestingConstants.SYSENV_DB_USER), TestingConstants.DEFAULT_DB_USER));

    /** The password of the user to connect with to the DB container */
    testingEnv.setDbPassword(StringUtils.defaultIfBlank(System.getProperty(
      TestingConstants.SYSENV_DB_PASSWORD), TestingConstants.DEFAULT_DB_PASSWORD));

    /** The JDBC URL to connect with to the DB container. If the URL contains a placeholder
     * (e.g. ${hostPort}) replace it with the ephemeral port created for the host of the container */
    String dbUrl = StringUtils.defaultIfBlank(System.getProperty(TestingConstants.SYSENV_DB_URL),
      TestingConstants.DEFAULT_DB_URL);
    testingEnv.setDbUrl(StrSubstitutor
      .replace(dbUrl, ImmutableMap.of("hostPort", testingEnv.getDbPortHost(), "dockerEngineHost",
        testingEnv.getDockerEngineHost())));

    /** The maximum amount of time to wait for the container to become available */
    if (StringUtils.isBlank(System.getProperty(TestingConstants.SYSENV_CONTAINER_MAX_WAIT))) {
      testingEnv.setContainerMaxWait(TestingConstants.DEFAULT_CONTAINER_MAX_WAIT);
    } else {
      testingEnv.setContainerMaxWait(
        Integer.parseInt(System.getProperty(TestingConstants.SYSENV_CONTAINER_MAX_WAIT)));
    }

    /** The waiting time between successive attempts to probe the container */
    if (StringUtils.isBlank(System.getProperty(TestingConstants.SYSENV_CONTAINER_WAIT_CYCLE))) {
      testingEnv.setContainerWaitCycle(TestingConstants.DEFAULT_CONTAINER_WAIT_CYCLE);
    } else {
      testingEnv.setContainerWaitCycle(
        Integer.parseInt(System.getProperty(TestingConstants.SYSENV_CONTAINER_WAIT_CYCLE)));
    }

    return testingEnv;
  }

}

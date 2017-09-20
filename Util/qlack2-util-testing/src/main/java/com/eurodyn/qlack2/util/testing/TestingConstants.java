package com.eurodyn.qlack2.util.testing;

public class TestingConstants {

  /** The default Docker Engine host to use */
  public static final String DEFAULT_DOCKER_ENGINE_HOST = "localhost";

  /** The default Docker Engine port to use */
  public static final int DEFAULT_DOCKER_ENGINE_PORT = 2375;

  /** The default Docker DB Image to use */
  public static final String DEFAULT_DB_IMAGE = "mysql:5.7.16";

  /** The port on which the DB container listens to */
  public static final int DEFAULT_DB_PORT_CONTAINER = 3306;

  /** The default DB availabilty check class to use */
  public static final String DEFAULT_DB_AVAILABILITY_CLASS =
    "com.eurodyn.qlack2.util.availcheck.mysql.AvailabilityCheckMySQL";

  /** The default user to connect with to the DB container */
  public static final String DEFAULT_DB_USER = "root";

  /** The password of the default user to connect with to the DB container */
  public static final String DEFAULT_DB_PASSWORD = "root";

  /** The default JDBC URL */
  public static final String DEFAULT_DB_URL = "jdbc:mysql://${dockerEngineHost}:${hostPort}/sys?useSSL=false";

  /** The default time to wait for the container to become available - 5 mins */
  public static final int DEFAULT_CONTAINER_MAX_WAIT = 1000 * 60 * 5;

  /** The default waiting period before probing the container - 5 sec */
  public static final int DEFAULT_CONTAINER_WAIT_CYCLE = 5000;

  /** The system property to denote a custom Docker engine host to use */
  public static final String SYSENV_DOCKER_ENGINE_HOST = "TEST_DOCKER_ENGINE_HOST";

  /** The system property to denote a custom Docker engine port to use */
  public static final String SYSENV_DOCKER_ENGINE_PORT = "TEST_DOCKER_ENGINE_PORT";

  /** The system property to denote a custom Docker engine to use */
  public static final String SYSENV_DB_IMAGE = "TEST_DB_IMAGE";

  /** The port on the host on which to expose the DB container listening port */
  public static final String SYSENV_DB_PORT_HOST = "TEST_DB_PORT_HOST";

  /** The port on which the DB container listens to */
  public static final String SYSENV_DB_PORT_CONTAINER = "TEST_DB_PORT_CONTAINER";

  /** The prefix used to specify environment options to the Docker container,
   * e.g. -DTEST_ENV_MYSQL_ROOT_PASSWORD=root. Our underlying testing framework will strip the
   * common prefix (e.g. the TEST_ENV_ part) and will pass the remaining to the Docker container. */
  public static final String SYSENV_ENV_OPTIONS_PREFIX = "TEST_ENV_";

  /** The name of the class to use to check for DB availability before starting tests */
  public static final String SYSENV_DB_AVAILABILITY_CLASS = "TEST_DB_AVAILABILITY_CLASS";

  /** The user to connect with to the DB container */
  public static final String SYSENV_DB_USER = "TEST_DB_USER";

  /** The password of the user to connect with to the DB container */
  public static final String SYSENV_DB_PASSWORD = "TEST_DB_PASSWORD";

  /** The URL to be passed to the JDBC driver to connect to the DB container. Use a '${hostPort}'
   * for the port to have it automatically being replaced by the ephemeral port automatically
   * creted during tests execution. */
  public static final String SYSENV_DB_URL = "TEST_DB_URL";

  /** The total time to wait (in msec) for the container to become accessible */
  public static final String SYSENV_CONTAINER_MAX_WAIT = "TEST_CONTAINER_MAX_WAIT";

  /** The time to wait between successive container-ping attempts */
  public static final String SYSENV_CONTAINER_WAIT_CYCLE = "TEST_CONTAINER_WAIT_CYCLE";
}

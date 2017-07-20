package com.eurodyn.qlack2.fuse.chatim.it;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.eurodyn.qlack2.util.availcheck.api.AvailabilityCheck;
import com.eurodyn.qlack2.util.availcheck.mysql.AvailabilityCheckMySQL;
import com.eurodyn.qlack2.util.docker.DockerContainer;

/**
 * @author European Dynamics SA
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RoomServiceImplTest.class,
        MessageServiceImplTest.class,
        IMMessageServiceImplTest.class,
        ChatUserServiceImplTest.class
})
public class AllITTests{
    /** MySQL configuration */
    private static String containerId;
    private static String dockerEngine = "tcp://localhost:2375";
    private static final String user = "root";
    private static final String password = "root";
    private static final String DB_IMAGE = "mysql:5.7.16";
    private static String CONTAINER_PORT = "3306";
    private static String EXPOSED_PORT = "3307";
    private static String url = "jdbc:mysql://127.0.0.1:3307/sys?useSSL=false";
    private static long DB_MAX_WAITING_FOR_CONTAINER = 120000;
    private static long DB_MAX_WAITING_PER_CYCLE =  1000;

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        /** Create DB container and start */
        containerId = DockerContainer.builder()
        .withDockerEngine(dockerEngine)
        .withImage(DB_IMAGE)
        .withPort(EXPOSED_PORT, CONTAINER_PORT)
        .withName("TEST-" + UUID.randomUUID())
        .withEnv("MYSQL_ROOT_PASSWORD",password)
        .run();
        Assert.assertNotNull(containerId);

        System.out.println("Waiting for DB to become accessible...");
        AvailabilityCheck check = new AvailabilityCheckMySQL();
        if (!check.isAvailable(url, user ,password, DB_MAX_WAITING_FOR_CONTAINER, DB_MAX_WAITING_PER_CYCLE, null)) {
            System.out.println("Could not connect to the DB. Tests will be terminated.");
            System.exit(1);
        } else {
            System.out.println("DB is accessible.");
        }
    }

    @AfterClass
    public static void afterClass() {
        DockerContainer.builder().withId(containerId).clean();
    }

}
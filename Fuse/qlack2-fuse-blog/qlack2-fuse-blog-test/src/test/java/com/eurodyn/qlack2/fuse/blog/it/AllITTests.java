package com.eurodyn.qlack2.fuse.blog.it;

import com.eurodyn.qlack2.fuse.blog.TestUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.util.UUID;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BlogServiceImplTest.class,
        CategoryServiceImplTest.class,
        CommentServiceImplTest.class,
        LayoutServiceImplTest.class,
        PostServiceImplTest.class,
        TagServiceImplTest.class
})
public class AllITTests{
    private static DockerClient dockerClient;
    private static String containerID;
    private static final String DB_IMAGE = "mysql:5.7.16";
    private static final int DB_PORT = 3306;

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        System.out.println("Starting DB container.");
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375")
                .build();
        dockerClient = DockerClientBuilder.getInstance(config).build();
        dockerClient.pullImageCmd(DB_IMAGE).exec(new PullImageResultCallback()).awaitSuccess();
        Ports portBindings = new Ports();
        portBindings.bind(ExposedPort.tcp(DB_PORT), Ports.Binding.bindPort(DB_PORT + 1));
        String containerName = "TEST-" + UUID.randomUUID().toString();
        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(DB_IMAGE)
                .withExposedPorts(ExposedPort.tcp(DB_PORT + 1))
                .withPortBindings(portBindings)
                .withName(containerName)
                .withEnv("MYSQL_ROOT_PASSWORD=root")
                .exec();
        containerID = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerID).exec();
        System.out.println("DB container started.");
        System.out.println("Waiting for DB bootstrap.");
        while (!TestUtil.isMySQLAcceptingConnection(
                "jdbc:mysql://127.0.0.1:3307/sys?useSSL=false", "root", "root")) {
            try {
                System.out.println("waiting...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterClass
    public static void afterClass() {
        dockerClient.stopContainerCmd(containerID).exec();
        try {
            dockerClient.waitContainerCmd(containerID)
                    .exec(new WaitContainerResultCallback()).awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dockerClient.removeContainerCmd(containerID).exec();
    }

}
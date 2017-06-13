package com.eurodyn.qlack2.fuse.aaa.it;

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

/**
 * @author European Dynamics SA
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AdminServiceImplTest.class
})
public class AllITTests{
    private static DockerClient dockerClient;
    private static String containerID;
    private static final String ES_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:5.4.1";
    private static final int ES_PORT = 9200;

    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        System.out.println("Starting ES container.");
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:2375")
                .build();
        dockerClient = DockerClientBuilder.getInstance(config).build();
        dockerClient.pullImageCmd(ES_IMAGE).exec(new PullImageResultCallback()).awaitSuccess();
        Ports portBindings = new Ports();
        portBindings.bind(ExposedPort.tcp(ES_PORT), Ports.Binding.bindPort(ES_PORT + 1));
        String containerName = "TEST-" + UUID.randomUUID().toString();
        CreateContainerResponse createContainerResponse = dockerClient.createContainerCmd(ES_IMAGE)
                .withExposedPorts(ExposedPort.tcp(ES_PORT + 1))
                .withPortBindings(portBindings)
                .withName(containerName)
                .withEnv("http.host=0.0.0.0", "transport.host=127.0.0.1")
                .exec();
        containerID = createContainerResponse.getId();
        dockerClient.startContainerCmd(containerID).exec();
        System.out.println("ES container started.");
//        System.out.println("Waiting for DB bootstrap.");
//        while (!TestUtil.isMySQLAcceptingConnection(
//                "jdbc:mysql://127.0.0.1:3307/sys?useSSL=false", "root", "root")) {
//            try {
//                System.out.println("waiting...");
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

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
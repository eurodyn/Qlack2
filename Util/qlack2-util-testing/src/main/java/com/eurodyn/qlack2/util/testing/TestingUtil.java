package com.eurodyn.qlack2.util.testing;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.replaceConfigurationFile;

import com.eurodyn.qlack2.util.docker.DockerContainer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.ops4j.pax.exam.Option;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A helper class to perform Pax-Exam tests.
 */
public class TestingUtil {

  /**
   * JUL reference
   */
  private final static Logger LOGGER = Logger.getLogger(TestingUtil.class.getName());

  /**
   * Copies a .cfg file from your local sources to the Karaf instance in Pax-Exam, optionally
   * replacing variables in the .cfg file. Variable replacement is taking place using
   *
   * @see org.apache.commons.lang.text.StrSubstitutor using a ${var} pattern.
   */
  public static Option copyITConf(String path, Map replacements) throws IOException {
    if (replacements == null) {
      replacements = new HashMap<>();
    }
    String confContent = FileUtils.readFileToString(new File("src/test/conf/it/" + path), "UTF-8");
    confContent = StrSubstitutor.replace(confContent, replacements, "${", "}");
    File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".cfg");
    LOGGER.log(Level.FINE, "Creating temporary file {}.", tempFile.getAbsolutePath());
    tempFile.deleteOnExit();
    FileUtils.write(tempFile, confContent);

    return replaceConfigurationFile(path, tempFile);
  }

  /**
   * Copies a .cfg file from your local sources to the Karaf instance in Pax-Exam.
   */
  public static Option copyITConf(String path) throws IOException {
    return copyITConf(path, null);
  }

  public static String startContainer(TestingEnv testingParams) {
    /** Prepare the Docker container */
    final DockerContainer dockerContainer = DockerContainer.builder()
      .withDockerEngine(
        "tcp://" + testingParams.getDockerEngineHost() + ":" + testingParams.getDockerEnginePort())
      .withImage(testingParams.getDbImage())
      .withPort(String.valueOf(testingParams.getDbPortHost()),
        String.valueOf(testingParams.getDbPortContainer()))
      .withName("TEST-" + UUID.randomUUID());

    /** Fill-in environmental options to the container */
    testingParams.getContainerEnvParams().keySet().parallelStream().forEach(key -> {
      dockerContainer
        .withEnv((String) key, (String) testingParams.getContainerEnvParams().get(key));
    });

    return dockerContainer.run();
  }

}

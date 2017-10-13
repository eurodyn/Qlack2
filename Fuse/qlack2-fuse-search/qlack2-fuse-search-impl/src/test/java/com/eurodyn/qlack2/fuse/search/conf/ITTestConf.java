package com.eurodyn.qlack2.fuse.search.conf;

import static com.eurodyn.qlack2.util.testing.TestingUtil.copyITConf;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.url;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import com.eurodyn.qlack2.util.testing.TestingEnv;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ITTestConf {
  private final static Logger LOGGER = Logger.getLogger(ITTestConf.class.getName());
  protected static final String COVERAGE_COMMAND = "jcoverage.command";
  /**
   * The parameters defining the testing environment.
   */
  public static TestingEnv testingEnv = TestingEnv.generate();

  private static Option addCodeCoverageOption() {
    String coverageCommand = System.getenv(COVERAGE_COMMAND);
    if (coverageCommand != null) {
      LOGGER.log(Level.INFO, "Setting coverage command to: " + coverageCommand);
      return CoreOptions.vmOption(coverageCommand);
    }
    return null;
  }
  
  @Configuration
  public static Option[] config() throws IOException {

    MavenArtifactUrlReference karafUrl = maven()
      .groupId("org.apache.karaf")
      .artifactId("apache-karaf")
      .versionAsInProject()
      .type("zip");

    MavenUrlReference projectFeaturesRepo = maven()
      .groupId("com.eurodyn.qlack2.fuse")
      .artifactId("qlack2-fuse-karaf-features")
      .versionAsInProject()
      .classifier("features")
      .type("xml");

    MavenArtifactUrlReference karafStandardFeaturesUrl = maven()
      .groupId("org.apache.karaf.features")
      .artifactId("standard")
      .versionAsInProject()
      .classifier("features")
      .type("xml");

    MavenUrlReference projectFeaturesRepoUtil = maven()
        .groupId("com.eurodyn.qlack2.util")
        .artifactId("qlack2-util-karaf-features")
        .versionAsInProject()
        .classifier("features")
        .type("xml");

    String localRepository = System.getProperty("org.ops4j.pax.url.mvn.localRepository");

    return new Option[]{
      karafDistributionConfiguration()
        .frameworkUrl(karafUrl)
        .unpackDirectory(new File("target", "exam"))
        .useDeployFolder(false),
      keepRuntimeFolder(),
      copyITConf("etc/com.eurodyn.qlack2.fuse.search.cfg"),
      when(localRepository != null)
        .useOptions(editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg",
        "org.ops4j.pax.url.mvn.localRepository", localRepository)),
      logLevel(LogLevel.INFO),
      configureConsole().ignoreLocalConsole(),
      configureConsole().ignoreRemoteShell(),
      addCodeCoverageOption(),
      features(karafStandardFeaturesUrl, "wrap"),
      features(projectFeaturesRepo, "qlack2-fuse-search-deps"),
        features(projectFeaturesRepoUtil, "qlack2-common-util"),
      CoreOptions.wrappedBundle(CoreOptions.mavenBundle("com.eurodyn.qlack2.util", "qlack2-util-testing")),
      CoreOptions.wrappedBundle(CoreOptions.mavenBundle("com.eurodyn.qlack2.util", "qlack2-util-networking")),
      url("file:../../../../qlack2-fuse-search-api/target/qlack2-fuse-search-api-" +
        MavenUtils.getArtifactVersion("com.eurodyn.qlack2.fuse", "qlack2-fuse-search-api") + ".jar"),
      url("file:../../qlack2-fuse-search-impl-" +
        MavenUtils.getArtifactVersion("com.eurodyn.qlack2.fuse", "qlack2-fuse-search-impl") + ".jar"),
    };
  }

}
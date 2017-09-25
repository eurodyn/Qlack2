package com.eurodyn.qlack2.fuse.crypto.impl;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.url;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ITTestConf {
  private final static Logger LOGGER = Logger.getLogger(ITTestConf.class.getName());

  protected static final String COVERAGE_COMMAND = "jcoverage.command";

  private static Option addCodeCoverageOption() {
    String coverageCommand = System.getenv(COVERAGE_COMMAND);
    if (coverageCommand != null) {
      LOGGER.log(Level.INFO, "Setting coverage command to: " + coverageCommand);
      return CoreOptions.vmOption(coverageCommand);
    }
    return null;
  }

  @Configuration
  public static Option[] config() {

    MavenArtifactUrlReference karafUrl = maven()
      .groupId("org.apache.karaf")
      .artifactId("apache-karaf")
      .version(TestUtil.karafVersion())
      .type("zip");

    MavenUrlReference projectFeaturesRepo = maven()
      .groupId("com.eurodyn.qlack2.fuse")
      .artifactId("qlack2-fuse-karaf-features")
      .version(TestUtil.projectVersion())
      .classifier("features")
      .type("xml");

    MavenUrlReference projectFeaturesRepoUtil = maven()
      .groupId("com.eurodyn.qlack2.util")
      .artifactId("qlack2-util-karaf-features")
      .version(TestUtil.projectVersion())
      .classifier("features")
      .type("xml");

    MavenArtifactUrlReference karafStandardFeaturesUrl = maven()
      .groupId("org.apache.karaf.features")
      .artifactId("standard")
      .version(TestUtil.karafVersion())
      .classifier("features")
      .type("xml");

    String localRepository = System.getProperty("org.ops4j.pax.url.mvn.localRepository");

    return new Option[]{
      karafDistributionConfiguration()
        .frameworkUrl(karafUrl)
        .unpackDirectory(new File("target", "exam"))
        .useDeployFolder(false),
      keepRuntimeFolder(),
      when(localRepository != null)
        .useOptions(editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg",
        "org.ops4j.pax.url.mvn.localRepository", localRepository)),
      logLevel(LogLevelOption.LogLevel.INFO),
      configureConsole().ignoreLocalConsole(),
      configureConsole().ignoreRemoteShell(),
      addCodeCoverageOption(),
      features(karafStandardFeaturesUrl, "wrap"),
      features(projectFeaturesRepo, "qlack2-fuse-crypto-deps"),
      url("file:../../../../qlack2-fuse-crypto-api/target/qlack2-fuse-crypto-api-" + TestUtil.projectVersion() + ".jar"),
      url("file:../../qlack2-fuse-crypto-impl-" + TestUtil.projectVersion() + ".jar"),
    };
  }
}
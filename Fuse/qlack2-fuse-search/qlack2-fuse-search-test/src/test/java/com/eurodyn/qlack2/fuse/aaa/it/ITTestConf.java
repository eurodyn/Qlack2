package com.eurodyn.qlack2.fuse.aaa.it;

import static com.eurodyn.qlack2.fuse.aaa.TestUtil.copyITConf;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureConsole;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

import com.eurodyn.qlack2.fuse.aaa.TestUtil;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;

import java.io.File;

public abstract class ITTestConf {

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
      copyITConf("etc/com.eurodyn.qlack2.fuse.search.cfg"),
      when(localRepository != null)
        .useOptions(editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg",
        "org.ops4j.pax.url.mvn.localRepository", localRepository)),
      logLevel(LogLevel.INFO),
      configureConsole().ignoreLocalConsole(),
      configureConsole().ignoreRemoteShell(),
      features(karafStandardFeaturesUrl, "wrap"),
      features(projectFeaturesRepo, "qlack2-fuse-search")
    };

  }

}
package com.eurodyn.qlack2.fuse.aaa.it;

import com.eurodyn.qlack2.fuse.aaa.TestUtil;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import java.io.File;
import static com.eurodyn.qlack2.fuse.aaa.TestUtil.copyITConf;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;


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



        return new Option[]{
                //for debug
                //KarafDistributionOption.debugConfiguration("5005", true),
                karafDistributionConfiguration()
                        .frameworkUrl(karafUrl)
                        .unpackDirectory(new File("target", "exam"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                copyITConf("etc/com.eurodyn.qlack2.util.liquibase.cfg"),
                copyITConf("etc/org.ops4j.datasource-qlack2.cfg"),
                copyITConf("etc/org.ops4j.pax.url.mvn.cfg"),
                logLevel(LogLevelOption.LogLevel.INFO),
                configureConsole().ignoreLocalConsole(),
                configureConsole().ignoreRemoteShell(),
                features(karafStandardFeaturesUrl, "wrap"),
                features(projectFeaturesRepo, "pax-jdbc-mysql"),
                features(projectFeaturesRepoUtil, "qlack2-util-liquibase"),
                features(projectFeaturesRepo, "qlack2-fuse-AAA"),

        };

    }

}
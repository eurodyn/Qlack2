package com.eurodyn.qlack2.fuse.lexicon.tests;

import com.eurodyn.qlack2.fuse.lexicon.api.BundleUpdateService;
import com.eurodyn.qlack2.fuse.lexicon.conf.ITTestConf;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class BundleUpdateServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  BundleUpdateService bundleUpdateService;

  @Test
  public void processBundle() throws IOException {
    BundleContext ctx = FrameworkUtil.getBundle(BundleUpdateService.class).getBundleContext();
    Bundle bundle = ctx.getBundle();
    Yaml yaml = new Yaml();

    Configuration config = yaml
      .loadAs(this.getClass().getResourceAsStream("/custom-qlack-lexicon.yaml"), Configuration.class);
    bundleUpdateService.processBundle(bundle, config.toString());
  }

}



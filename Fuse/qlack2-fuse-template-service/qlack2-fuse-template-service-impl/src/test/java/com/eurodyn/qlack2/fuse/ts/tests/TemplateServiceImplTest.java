package com.eurodyn.qlack2.fuse.ts.tests;

import java.util.logging.Logger;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import com.eurodyn.qlack2.fuse.ts.api.TemplateService;
import com.eurodyn.qlack2.fuse.ts.conf.ITTestConf;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class TemplateServiceImplTest extends ITTestConf {

  private static final Logger LOGGER = Logger.getLogger(TemplateServiceImplTest.class.getName());


  @Inject
  @Filter(timeout = 1200000)
  TemplateService templateService;

  @Test
  public void replacePlaceholdersWordDoc(){
    
  }
}

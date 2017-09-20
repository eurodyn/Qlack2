package com.eurodyn.qlack2.fuse.aaa;

import com.eurodyn.qlack2.fuse.aaa.api.JSONConfigService;
import com.eurodyn.qlack2.fuse.aaa.conf.ITTestConf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Bundle;
import javax.inject.Inject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class JSONConfigServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    JSONConfigService jsonConfigService;

    @Test
    public void processBundle(){
        //returns bundle for the specific class
        BundleContext ctx = FrameworkUtil.getBundle(JSONConfigService.class).getBundleContext();
        Bundle bundle = ctx.getBundle();
        //check if has configuration file,then calls parseConfig()
        jsonConfigService.processBundle(bundle);
    }

}

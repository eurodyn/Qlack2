package com.eurodyn.qlack2.fuse.lexicon.it;

import com.eurodyn.qlack2.fuse.lexicon.api.BundleUpdateService;
import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.KeyService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BundleUpdateServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    BundleUpdateService bundleUpdateService;

    @Test
    public void processBundle(){
        BundleContext ctx = FrameworkUtil.getBundle(BundleUpdateService.class).getBundleContext();
        Bundle bundle = ctx.getBundle();
        Yaml yaml = new Yaml();

        try( InputStream in = Files.newInputStream( Paths.get("tmp/test.yaml") ) ) {
            Configuration config = yaml.loadAs( in, Configuration.class );
            System.out.println( config.toString() );
            bundleUpdateService.processBundle(bundle,config.toString());
        }catch(Exception e){

        }
    }

}



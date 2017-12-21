package com.eurodyn.qlack2.fuse.rules.tests;

import com.eurodyn.qlack2.fuse.rules.api.RulesRuntimeService;
import com.eurodyn.qlack2.fuse.rules.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.rules.util.FactDTO;
import com.eurodyn.qlack2.fuse.rules.util.TestUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class RulesRuntimeServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    RulesRuntimeService rulesRuntimeService;

    @Test
    public void statelessExecute(){

        List<String> warnings = new ArrayList<>();

        Map<String, Object> globals = new HashMap<>();
        globals.put("warnings", warnings);

        List<Object> facts = new ArrayList<>();
        facts.add(new FactDTO(20));

        List<String> rules = new ArrayList<>();
        rules.add(TestUtilities.testRule());

        rulesRuntimeService.statelessExecute(rules, facts, globals, this.getClass().getClassLoader());

        Assert.assertFalse("Test Rule not fired", ((List<String>) globals.get("warnings")).isEmpty());
    }

}
package com.eurodyn.qlack2.fuse.componentlibrary.tests;

import com.eurodyn.qlack2.fuse.componentlibrary.api.DirectoryService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.RegistrationService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.UserInteractionService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.componentlibrary.util.TestUtilities;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;
import java.util.*;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class DirectoryServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    DirectoryService directoryService;

    @Inject
    @Filter(timeout = 1200000)
    RegistrationService registrationService;

    @Inject
    @Filter(timeout = 1200000)
    UserInteractionService userInteractionService;

    @Test
    public void listGadgets(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));

        Assert.assertNotNull(directoryService.listGadgets(true));
        Assert.assertTrue(directoryService.listGadgets(true).size() != 0);
    }

    @Test
    public void searchGadgets(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));

        Assert.assertNotNull(directoryService.searchGadgets(componentDTO.getTitle(),true));
    }

    @Test
    public void getGadgetsForUserID(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        Assert.assertNotNull(directoryService.getGadgetsForUserID(componentDTO.getUserKey()));
        Assert.assertTrue(directoryService.getGadgetIDsForUserID(componentDTO.getUserKey()).size() != 0);
    }

    @Test
    public void getGadgetIDsForUserID(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        ArrayList userIds = new ArrayList();
        userIds.add(componentDTO.getId());

        Assert.assertNotNull(directoryService.getGadgetIDsForUserID(componentDTO.getUserKey()));
        Assert.assertTrue(directoryService.getGadgetIDsForUserID(componentDTO.getUserKey()).size() != 0);
        Assert.assertEquals(userIds,directoryService.getGadgetIDsForUserID(componentDTO.getUserKey()));
    }

}

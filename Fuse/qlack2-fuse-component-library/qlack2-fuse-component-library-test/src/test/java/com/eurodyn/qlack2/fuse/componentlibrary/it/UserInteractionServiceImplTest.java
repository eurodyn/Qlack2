package com.eurodyn.qlack2.fuse.componentlibrary.it;

import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.api.RegistrationService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.UserInteractionService;
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
public class UserInteractionServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    UserInteractionService userInteractionService;

    @Inject
    @Filter(timeout = 1200000)
    RegistrationService registrationService;

    @Test
    public void addGadgetToHomepage(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        Assert.assertNotNull(registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()));
    }
    
    @Test
    public void removeGadgetFromHomepage(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);
        Assert.assertNotNull(registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()));

        userInteractionService.removeGadgetFromHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        Assert.assertNotNull(registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()));
    }

    @Test
    public void addGadgetToGroupPage(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToGroupPage(componentDTO.getId(),componentDTO.getUserKey(),false);

        Assert.assertNotNull(registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()));
    }

    @Test
    public void removeGadgetFromGroupPage(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToGroupPage(componentDTO.getId(),componentDTO.getUserKey(),false);
        Assert.assertNotNull(registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()));

        userInteractionService.removeGadgetFromGroupPage(componentDTO.getId(),componentDTO.getUserKey(),false);
        Assert.assertNotNull(registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()));
    }

    @Test
    public void reorderGadgets(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        List<String> list = new ArrayList();
        userInteractionService.reorderGadgets(list,componentDTO.getUserKey());
    }

    @Test
    public void setState(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        List<String> list = new ArrayList();
        userInteractionService.setState(componentDTO.getId(),componentDTO.getState(),componentDTO.getUserKey());
    }
    
}

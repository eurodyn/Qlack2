package com.eurodyn.qlack2.fuse.componentlibrary.tests;

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

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class RegistrationServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    RegistrationService registrationService;

    @Inject
    @Filter(timeout = 1200000)
    UserInteractionService userInteractionService;

    @Test
    public void registerGadget(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
    }

    @Test
    public void unregisterGadget(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        registrationService.registerGadget(componentDTO);

        //expecting not null that gadget is disabled
        Assert.assertNotNull(registrationService.isGadgetEnabled(componentDTO.getId()));

        registrationService.unregisterGadget(componentDTO.getId());

        //check if privateKey is valid after the update,expect false
        Assert.assertFalse(registrationService.isValidSecretKey(componentDTO.getPrivateKey()));
    }

    @Test
    public void updateGadget(){
        //new gadget
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));

        //check if privateKey is valid before the update,expect true
        Assert.assertTrue(registrationService.isValidSecretKey(componentDTO.getPrivateKey()));

        //update DTO with non valid privateKey
        componentDTO.setPrivateKey("test");

        registrationService.updateGadget(componentDTO);

        //check if privateKey is valid after the update,expect false
        Assert.assertFalse(registrationService.isValidSecretKey(componentDTO.getPrivateKey()));
    }

    @Test
    public void enableGadget(){
        //new gadget
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));

        //enable gadget
        registrationService.enableGadget(componentDTO.getId());
        Assert.assertTrue(registrationService.isGadgetEnabled(componentDTO.getId()));
    }

    @Test
    public void disableGadget(){
        //new gadget
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));

        //enable gadget
        registrationService.disableGadget(componentDTO.getId());
        Assert.assertFalse(registrationService.isGadgetEnabled(componentDTO.getId()));
    }

    @Test
    public void getGadgetIDFromGadgetUserKey(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        Assert.assertNotNull(registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()));
        Assert.assertEquals("["+componentDTO.getId()+"]",registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()));
        Assert.assertTrue(registrationService.getGadgetIDFromGadgetUserKey(componentDTO.getUserKey()).contains(componentDTO.getId()));
    }

    @Test
    public void getUserIDFromGadgetUserKey(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        Assert.assertNotNull(registrationService.getUserIDFromGadgetUserKey(componentDTO.getUserKey()));
        Assert.assertEquals("["+componentDTO.getUserKey()+"]",registrationService.getUserIDFromGadgetUserKey(componentDTO.getUserKey()));
        Assert.assertTrue(registrationService.getUserIDFromGadgetUserKey(componentDTO.getUserKey()).contains(componentDTO.getUserKey()));
    }

    @Test
    public void isValidSecretKey(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        userInteractionService.addGadgetToHomepage(componentDTO.getId(),componentDTO.getUserKey(),false);

        Assert.assertNotNull(registrationService.isValidSecretKey(componentDTO.getPrivateKey()));
        Assert.assertTrue(registrationService.isValidSecretKey(componentDTO.getPrivateKey()));
    }

}

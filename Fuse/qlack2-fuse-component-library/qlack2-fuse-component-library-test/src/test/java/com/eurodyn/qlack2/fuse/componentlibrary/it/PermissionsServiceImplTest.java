package com.eurodyn.qlack2.fuse.componentlibrary.it;

import com.eurodyn.qlack2.fuse.componentlibrary.api.PermissionsService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.RegistrationService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.UserInteractionService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentPermissionDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class PermissionsServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    PermissionsService permissionsService;

    @Inject
    @Filter(timeout = 1200000)
    RegistrationService registrationService;

    @Test
    public void revokePermission(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        permissionsService.requestPermissions(TestConst.permission,componentDTO.getId(),componentDTO.getUserKey());

        ComponentPermissionDTO componentPermissionDTO = TestUtilities.createComponentPermissionDTO(componentDTO.getId(),componentDTO.getUserKey());
        permissionsService.revokePermission(componentPermissionDTO.getPermission(),componentDTO.getId(),componentDTO.getUserKey());
    }

    @Test
    public void getPendingPermissionRequests(){
        ComponentDTO componentDTO = TestUtilities.createComponentDTO();
        Assert.assertNotNull(registrationService.registerGadget(componentDTO));
        permissionsService.requestPermissions(TestConst.permission,componentDTO.getId(),componentDTO.getUserKey());
        Assert.assertNotNull(permissionsService.getPendingPermissionRequests(componentDTO.getUserKey()));
    }

}

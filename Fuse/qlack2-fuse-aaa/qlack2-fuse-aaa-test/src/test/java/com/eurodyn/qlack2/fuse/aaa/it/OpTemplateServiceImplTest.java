package com.eurodyn.qlack2.fuse.aaa.it;

import com.eurodyn.qlack2.fuse.aaa.api.OpTemplateService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OpTemplateServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    OpTemplateService opTemplateService;

    @Inject
    @Filter(timeout = 1200000)
    OperationService operationService;

    @Inject
    @Filter(timeout = 1200000)
    ResourceService resourceService;

    @Test
    public void createTemplate() throws Exception {
        //creates OpTemplateDTO
        OpTemplateDTO OpTemplateDTO = TestUtilities.createOpTemplateDTO();
        String OpTemplateID = opTemplateService.createTemplate(OpTemplateDTO);
        Assert.assertNotNull(OpTemplateID);
    }

    @Test
    public void deleteTemplateByID(){
        //creates OpTemplateDTO
        OpTemplateDTO OpTemplateDTO = TestUtilities.createOpTemplateDTO();
        String OpTemplateID = opTemplateService.createTemplate(OpTemplateDTO);
        Assert.assertNotNull(OpTemplateID);

        opTemplateService.deleteTemplateByID(OpTemplateID);
        Assert.assertNull(opTemplateService.getTemplateByID(OpTemplateID));
    }

    @Test
    public void deleteTemplateByName(){
        //creates OpTemplateDTO
        OpTemplateDTO OpTemplateDTO = TestUtilities.createOpTemplateDTO();
        String OpTemplateID = opTemplateService.createTemplate(OpTemplateDTO);
        Assert.assertNotNull(OpTemplateID);

        opTemplateService.deleteTemplateByName(OpTemplateDTO.getName());
        Assert.assertNull(opTemplateService.getTemplateByName(OpTemplateDTO.getName()));
    }

    @Test
    public void getTemplateByID(){
        //creates OpTemplateDTO
        OpTemplateDTO OpTemplateDTO = TestUtilities.createOpTemplateDTO();
        String OpTemplateID = opTemplateService.createTemplate(OpTemplateDTO);
        Assert.assertNotNull(OpTemplateID);

        Assert.assertNotNull(opTemplateService.getTemplateByID(OpTemplateID));
    }

    @Test
    public void getTemplateByName(){
        //creates OpTemplateDTO
        OpTemplateDTO OpTemplateDTO = TestUtilities.createOpTemplateDTO();
        String OpTemplateID = opTemplateService.createTemplate(OpTemplateDTO);
        Assert.assertNotNull(OpTemplateID);

        Assert.assertNotNull(opTemplateService.getTemplateByName(OpTemplateDTO.getName()));
    }

    @Test
    public void addOperation(){
        //creates OpTemplateDTO
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplAddOperID = opTemplateService.createTemplate(opTemplateDTO);
        Assert.assertNotNull(opTemplAddOperID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        opTemplateService.addOperation(opTemplAddOperID,operationDTO.getName(),false);
        Assert.assertNotNull(opTemplateService.getTemplateByID(opTemplAddOperID));
    }

    @Test
    public void addOperationArgs(){
        //creates OpTemplateDTO
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        Assert.assertNotNull(opTemplateID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        opTemplateService.addOperation(opTemplateID,operationDTO.getName(),resourceID,false);
        Assert.assertNotNull(opTemplateService.getTemplateByID(opTemplateID));
    }

    @Test
    public void removeOperation(){
        //creates OpTemplateDTO
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        Assert.assertNotNull(opTemplateID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        //add operation
        opTemplateService.addOperation(opTemplateID,operationDTO.getName(),false);
        Assert.assertNotNull(opTemplateService.getTemplateByID(opTemplateID));

        //remove operation
        opTemplateService.removeOperation(opTemplateID,operationDTO.getName());
        Assert.assertNull(opTemplateService.getOperationAccess(opTemplateID,operationDTO.getName()));
    }

    @Test
    public void removeOperationArgs(){
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        Assert.assertNotNull(opTemplateID);

        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        //adds operation
        opTemplateService.addOperation(opTemplateID,operationDTO.getName(),resourceID,false);
        Assert.assertNotNull(opTemplateService.getTemplateByID(opTemplateID));

        //removes operation
        opTemplateService.removeOperation(opTemplateID,operationDTO.getName(),resourceID);
        Assert.assertNull(opTemplateService.getOperationAccess(opTemplateID,operationDTO.getName(),resourceID));
    }

    @Test
    public void getOperationAccess(){
        //creates OpTemplateDTO
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        Assert.assertNotNull(opTemplateID);

        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        Assert.assertNull(opTemplateService.getOperationAccess(opTemplateID,operationDTO.getName()));
    }

    @Test
    public void getOperationAccessArgs(){
        //creates OpTemplateDTO
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        Assert.assertNotNull(opTemplateID);

        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        Assert.assertNull(opTemplateService.getOperationAccess(opTemplateID,operationDTO.getName(),resourceID));
    }

    @Test
    public void updateTemplate(){
        //creates OpTemplateDTO
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        Assert.assertNotNull(opTemplateID);

        Assert.assertNotNull(opTemplateService.updateTemplate(opTemplateDTO));
    }

}

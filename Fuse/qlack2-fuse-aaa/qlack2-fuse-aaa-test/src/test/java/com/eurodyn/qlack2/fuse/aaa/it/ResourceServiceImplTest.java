package com.eurodyn.qlack2.fuse.aaa.it;

import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.junit.Assert;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import javax.inject.Inject;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class ResourceServiceImplTest extends ITTestConf  {

    @Inject
    @Filter(timeout = 1200000)
    ResourceService resourceService;

    @Test
    public void createResource() {
        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);
    }

    @Test
    public void updateResource() {
        //creates Resource
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(UUID.randomUUID().toString());
        resourceDTO.setName("update");
        resourceDTO.setObjectID(UUID.randomUUID().toString());
        resourceDTO.setDescription("update-descr");
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        //finds and changes description and name
        ResourceDTO resourceUpdDTO = resourceService.getResourceById(resourceID);
        Assert.assertNotNull(resourceUpdDTO);

        resourceUpdDTO.setName("test-updated");
        resourceUpdDTO.setDescription("test-updated-descr");

        resourceService.updateResource(resourceUpdDTO);
        ResourceDTO resourceUpdID = resourceService.getResourceById(resourceID);
        Assert.assertNotNull(resourceUpdID);

        Assert.assertEquals("test-updated",resourceUpdID.getName());
    }

    @Test
    public void deleteResource(){
        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        resourceService.deleteResource(resourceID);
        Assert.assertNull(resourceService.getResourceById(resourceID));
    }

    @Test
    public void deleteResources(){
        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        List<String> list = new ArrayList();
        list.add(resourceID);

        resourceService.deleteResources(list);
        Assert.assertNull(resourceService.getResourceById(resourceID));
    }

    @Test
    public void deleteResourceByObjectId(){
        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        resourceService.deleteResourceByObjectId(resourceDTO.getObjectID());
        Assert.assertNull(resourceService.getResourceByObjectId(resourceDTO.getObjectID()));
    }

    @Test
    public void deleteResourcesByObjectIds(){
        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        List<String> list = new ArrayList();
        list.add(resourceDTO.getObjectID());

        resourceService.deleteResourcesByObjectIds(list);
        Assert.assertNull(resourceService.getResourceByObjectId(resourceDTO.getObjectID()));
    }

    @Test
    public void getResourceById(){
        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        Assert.assertNotNull(resourceService.getResourceById(resourceID));
    }

    @Test
    public void getResourceByObjectId(){
        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        Assert.assertNotNull(resourceService.getResourceByObjectId(resourceDTO.getObjectID()));
    }

}

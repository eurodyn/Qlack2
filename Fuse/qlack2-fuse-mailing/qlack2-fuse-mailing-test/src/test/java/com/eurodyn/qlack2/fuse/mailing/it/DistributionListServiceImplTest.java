package com.eurodyn.qlack2.fuse.mailing.it;

import com.eurodyn.qlack2.fuse.mailing.api.DistributionListService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.ContactDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.DistributionListDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class DistributionListServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    DistributionListService distributionListService;

    @Test
    public void createDistributionList(){
        DistributionListDTO distributionListDTO = TestUtilities.createDistributionList();
        distributionListDTO.setName("testName01");
        distributionListService.createDistributionList(distributionListDTO);

        List<DistributionListDTO> distributionListID = distributionListService.search("testName01");
        Assert.assertNotNull(distributionListID);
    }

    @Test
    public void editDistributionList(){
        DistributionListDTO distributionListDTO = TestUtilities.createDistributionList();
        distributionListDTO.setName("testName02");
        distributionListService.createDistributionList(distributionListDTO);

        List<DistributionListDTO> distributionListID = distributionListService.search("testName02");
        Assert.assertNotNull(distributionListID);

        distributionListDTO.setName("testName03");
        distributionListService.editDistributionList(distributionListDTO);

        List<DistributionListDTO> distributionListEditID = distributionListService.search("testName03");
        Assert.assertNotNull(distributionListEditID);
    }

    @Test
    public void deleteDistributionList(){
        DistributionListDTO distributionListDTO = TestUtilities.createDistributionList();
        distributionListDTO.setName("testName04");
        distributionListService.createDistributionList(distributionListDTO);

        List<DistributionListDTO> distributionListID = distributionListService.search("testName04");
        Assert.assertNotNull(distributionListID);

        Iterator iterator = distributionListID.iterator();
        while(iterator.hasNext()){
            DistributionListDTO element = (DistributionListDTO) iterator.next();
            String distrID = element.getId();
            distributionListService.deleteDistributionList(distrID);
        }

        Assert.assertTrue(distributionListService.search("testName04").isEmpty());
    }

    @Test
    public void createContact(){
        ContactDTO contactDTO = TestUtilities.createContactDTO();
        String contactID = distributionListService.createContact(contactDTO);
        Assert.assertNotNull(contactID);
    }

    @Test
    public void addContactToDistributionList(){
        DistributionListDTO distributionListDTO = TestUtilities.createDistributionList();
        distributionListDTO.setName("testName05");
        distributionListService.createDistributionList(distributionListDTO);

        List<DistributionListDTO> distributionListID = distributionListService.search("testName05");
        Assert.assertNotNull(distributionListID);

        ContactDTO contactDTO = TestUtilities.createContactDTO();
        String contactID = distributionListService.createContact(contactDTO);

        Iterator iterator = distributionListID.iterator();
        while(iterator.hasNext()){
            DistributionListDTO element = (DistributionListDTO) iterator.next();
            String distrID = element.getId();
            distributionListService.addContactToDistributionList(distrID,contactID);
        }
    }

    @Test
    public void removeContactFromDistributionList(){
        DistributionListDTO distributionListDTO = TestUtilities.createDistributionList();
        distributionListDTO.setName("testName06");
        distributionListService.createDistributionList(distributionListDTO);

        List<DistributionListDTO> distributionListID = distributionListService.search("testName06");
        Assert.assertNotNull(distributionListID);

        ContactDTO contactDTO = TestUtilities.createContactDTO();
        String contactID = distributionListService.createContact(contactDTO);

        Iterator iterator = distributionListID.iterator();
        while(iterator.hasNext()){
            DistributionListDTO element = (DistributionListDTO) iterator.next();
            String distrID = element.getId();
            distributionListService.addContactToDistributionList(distrID,contactID);
            distributionListService.removeContactFromDistributionList(distrID,contactID);
        }

        Iterator iteratorRem = distributionListID.iterator();
        while(iteratorRem.hasNext()){
            DistributionListDTO element = (DistributionListDTO) iteratorRem.next();
            Assert.assertNull(element.getContacts());
        }
    }

}



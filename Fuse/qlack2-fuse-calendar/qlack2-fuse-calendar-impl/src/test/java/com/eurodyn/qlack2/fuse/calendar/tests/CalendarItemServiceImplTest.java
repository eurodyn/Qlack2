package com.eurodyn.qlack2.fuse.calendar.tests;

import com.eurodyn.qlack2.fuse.calendar.api.CalendarItemService;
import com.eurodyn.qlack2.fuse.calendar.api.CalendarService;
import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarItemDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.ParticipantDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.SupportingObjectDTO;
import com.eurodyn.qlack2.fuse.calendar.api.exception.QParticipantNotExists;
import com.eurodyn.qlack2.fuse.calendar.api.exception.QSupportingObjectNotExists;
import com.eurodyn.qlack2.fuse.calendar.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.calendar.util.TestConst;
import com.eurodyn.qlack2.fuse.calendar.util.TestUtilities;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class CalendarItemServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    CalendarItemService calendarItemService;

    @Inject
    @Filter(timeout = 1200000)
    CalendarService calendarService;

    @Test
    public void createItem(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO = TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);
    }

    @Test
    public void updateItem(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        calendarItemDTO.setName("testName01");
        CalendarItemDTO blogTagCrt = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = blogTagCrt.getId();
        Assert.assertNotNull(calendarItemID);

        //search Item by id
        CalendarItemDTO calendarItemGetDTO = calendarItemService.getItem(calendarItemID);
        calendarItemGetDTO.setName("testName02");
        calendarItemService.updateItem(calendarItemGetDTO);

        //check if updated successfully
        Assert.assertEquals("testName02",calendarItemService.getItem(calendarItemID).getName());
    }

    @Test
    public void deleteItem(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        calendarItemService.deleteItem(calendarItemCrtDTO);

        //expected null
        Assert.assertNull(calendarItemService.getItem(calendarItemID));
    }

    @Test
    public void getItem(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        Assert.assertNotNull(calendarItemService.getItem(calendarItemID));
    }

    @Test
    public void getCalendarItems(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        String categoryId = calendarItemCrtDTO.getCategoryId();
        String categoryIds[] = categoryId.split(" ");

        Calendar startCal = new GregorianCalendar(2017,1,21);
        Date startDate = startCal.getTime();

        Calendar endCal = new GregorianCalendar(2017,1,21);
        Date endDate = endCal.getTime();

        Assert.assertNotNull(calendarItemService.getCalendarItems(calendarItemID,categoryIds,startDate,endDate));
    }

    @Test
    public void getItemsForUser(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        String categoryId = calendarItemCrtDTO.getCategoryId();
        String categoryIds[] = categoryId.split(" ");

        Calendar startCal = new GregorianCalendar(2017,1,21);
        Date startDate = startCal.getTime();

        Calendar endCal = new GregorianCalendar(2017,1,21);
        Date endDate = endCal.getTime();

        Assert.assertNotNull(calendarItemService.getItemsForUser(calendarItemCrtDTO.getSrcUserId(),categoryIds,startDate,endDate));
    }

    @Test
    public void getItemsForUserArgs(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        String categoryId = calendarItemCrtDTO.getCategoryId();
        String categoryIds[] = categoryId.split(" ");

        Calendar startCal = new GregorianCalendar(2017,1,21);
        Date startDate = startCal.getTime();

        Calendar endCal = new GregorianCalendar(2017,1,21);
        Date endDate = endCal.getTime();

        Assert.assertNotNull(calendarItemService.getItemsForUser(calendarItemCrtDTO.getSrcUserId(),calendarItemID,categoryIds,startDate,endDate));
    }

    @Test
    public void addItemSupportingObject(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        SupportingObjectDTO supportingObjectDTO = new SupportingObjectDTO();
        supportingObjectDTO.setCategoryId(calendarItemCrtDTO.getCategoryId());
        supportingObjectDTO.setLastModifiedOn(calendarItemDTO.getLastModifiedOn());
        supportingObjectDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        supportingObjectDTO.setId(UUID.randomUUID().toString());
        supportingObjectDTO.setCreatedOn(new Date());
        supportingObjectDTO.setItemId(calendarItemID);
        supportingObjectDTO.setCreatedBy(calendarItemDTO.getCreatedBy());
        supportingObjectDTO.setLastModifiedBy(calendarItemDTO.getLastModifiedBy());

        Assert.assertNotNull(calendarItemService.addItemSupportingObject(supportingObjectDTO,false));
    }

    @Test
    public void updateItemSupportingObject(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        SupportingObjectDTO supportingObjectDTO = new SupportingObjectDTO();
        supportingObjectDTO.setCategoryId(calendarItemCrtDTO.getCategoryId());
        supportingObjectDTO.setLastModifiedOn(calendarItemDTO.getLastModifiedOn());
        supportingObjectDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        supportingObjectDTO.setId(UUID.randomUUID().toString());
        supportingObjectDTO.setCreatedOn(new Date());
        supportingObjectDTO.setItemId(calendarItemID);
        supportingObjectDTO.setCreatedBy(calendarItemDTO.getCreatedBy());
        supportingObjectDTO.setLastModifiedBy(calendarItemDTO.getLastModifiedBy());

        SupportingObjectDTO supportingObjectGetDTO = calendarItemService.addItemSupportingObject(supportingObjectDTO,false);

        //update DTO
        supportingObjectDTO.setLastModifiedBy("testName03");

        calendarItemService.updateItemSupportingObject(supportingObjectDTO,true);

        Assert.assertEquals("testName03",calendarItemService.getItemSupportingObject(supportingObjectGetDTO.getId()).getLastModifiedBy());
    }

    @Test(expected=QSupportingObjectNotExists.class)
    public void removeItemSupportingObject(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        SupportingObjectDTO supportingObjectDTO = new SupportingObjectDTO();
        supportingObjectDTO.setCategoryId(calendarItemCrtDTO.getCategoryId());
        supportingObjectDTO.setLastModifiedOn(calendarItemDTO.getLastModifiedOn());
        supportingObjectDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        supportingObjectDTO.setId(UUID.randomUUID().toString());
        supportingObjectDTO.setCreatedOn(new Date());
        supportingObjectDTO.setItemId(calendarItemID);
        supportingObjectDTO.setCreatedBy(calendarItemDTO.getCreatedBy());
        supportingObjectDTO.setLastModifiedBy(calendarItemDTO.getLastModifiedBy());

        SupportingObjectDTO supportingObjectGetDTO = calendarItemService.addItemSupportingObject(supportingObjectDTO,false);
        Assert.assertNotNull(supportingObjectGetDTO);

        calendarItemService.removeItemSupportingObject(supportingObjectDTO,true);

        calendarItemService.getItemSupportingObject(supportingObjectGetDTO.getId());
    }

    @Test
    public void getItemSupportingObject(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        SupportingObjectDTO supportingObjectDTO = new SupportingObjectDTO();
        supportingObjectDTO.setCategoryId(calendarItemCrtDTO.getCategoryId());
        supportingObjectDTO.setLastModifiedOn(calendarItemDTO.getLastModifiedOn());
        supportingObjectDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        supportingObjectDTO.setId(UUID.randomUUID().toString());
        supportingObjectDTO.setCreatedOn(new Date());
        supportingObjectDTO.setItemId(calendarItemID);
        supportingObjectDTO.setCreatedBy(calendarItemDTO.getCreatedBy());
        supportingObjectDTO.setLastModifiedBy(calendarItemDTO.getLastModifiedBy());

        SupportingObjectDTO supportingObjectGetDTO = calendarItemService.addItemSupportingObject(supportingObjectDTO,false);
        Assert.assertNotNull(supportingObjectGetDTO);

        Assert.assertNotNull(calendarItemService.getItemSupportingObject(supportingObjectGetDTO.getId()));
    }

    @Test
    public void getItemSupportingObjects(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        SupportingObjectDTO supportingObjectDTO = new SupportingObjectDTO();
        supportingObjectDTO.setCategoryId(calendarItemCrtDTO.getCategoryId());
        supportingObjectDTO.setLastModifiedOn(calendarItemDTO.getLastModifiedOn());
        supportingObjectDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        supportingObjectDTO.setId(UUID.randomUUID().toString());
        supportingObjectDTO.setCreatedOn(new Date());
        supportingObjectDTO.setItemId(calendarItemID);
        supportingObjectDTO.setCreatedBy(calendarItemDTO.getCreatedBy());
        supportingObjectDTO.setLastModifiedBy(calendarItemDTO.getLastModifiedBy());

        SupportingObjectDTO supportingObjectGetDTO = calendarItemService.addItemSupportingObject(supportingObjectDTO,false);
        Assert.assertNotNull(supportingObjectGetDTO);

        String categoryId = calendarItemCrtDTO.getCategoryId();
        String categoryIds[] = categoryId.split(" ");

        Assert.assertNotNull(calendarItemService.getItemSupportingObjects(calendarItemID,categoryIds));
    }

    @Test
    public void addItemParticipant(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setParticipantId(UUID.randomUUID().toString());

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);
        Assert.assertNotNull(calendarItemService.getItemParticipant(itemParticipantId.getId()));
    }

    @Test
    public void updateItemParticipant(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setStatus(TestConst.status_prev);
        participantDTO.setParticipantId("testId04");

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);

        //update DTO
        itemParticipantId.setParticipantId("testId05");

        calendarItemService.updateItemParticipant(itemParticipantId,false);

        Assert.assertEquals("testId05",calendarItemService.getItemParticipant(itemParticipantId.getId()).getParticipantId());
    }

    @Test(expected=QParticipantNotExists.class)
    public void removeItemParticipant(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setStatus(TestConst.status_prev);
        participantDTO.setParticipantId(UUID.randomUUID().toString());

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);

        calendarItemService.removeItemParticipant(itemParticipantId,false);

        Assert.assertNull(calendarItemService.getItemParticipant(itemParticipantId.getId()));
    }

    @Test
    public void getItemParticipant(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setStatus(TestConst.status_prev);
        participantDTO.setParticipantId(UUID.randomUUID().toString());

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);

        Assert.assertNotNull(calendarItemService.getItemParticipant(itemParticipantId.getId()));
    }

    @Test
    public void getItemParticipants(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setStatus(TestConst.status_prev);
        participantDTO.setParticipantId(UUID.randomUUID().toString());

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);

        Assert.assertNotNull(calendarItemService.getItemParticipants(calendarItemID));
    }

    @Test
    public void getParticipantsForUser(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setStatus(TestConst.status_prev);
        participantDTO.setParticipantId(UUID.randomUUID().toString());

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);

        Assert.assertNotNull(calendarItemService.getParticipantsForUser(calendarItemCrtDTO.getSrcUserId()));
    }

    @Test
    public void getParticipantsForUserArgs(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setStatus(TestConst.status_prev);
        participantDTO.setParticipantId(UUID.randomUUID().toString());

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);

        Assert.assertNotNull(calendarItemService.getParticipantsForUser(calendarItemCrtDTO.getSrcUserId(),calendarItemID));
    }

    @Test
    public void getParticipantStatus(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setStatus(TestConst.status_prev);
        participantDTO.setParticipantId(UUID.randomUUID().toString());

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);

        Assert.assertNotNull(calendarItemService.getParticipantStatus(itemParticipantId.getId()));
    }

    @Test
    public void updateParticipantStatus(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO= TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(UUID.randomUUID().toString());
        participantDTO.setItemId(calendarItemID);
        participantDTO.setSrcUserId(calendarItemCrtDTO.getSrcUserId());
        participantDTO.setStatus(TestConst.status_prev);
        participantDTO.setParticipantId(UUID.randomUUID().toString());

        ParticipantDTO itemParticipantId = calendarItemService.addItemParticipant(participantDTO,false);

        calendarItemService.updateParticipantStatus(itemParticipantId.getId(),TestConst.status_new);

        Assert.assertEquals(TestConst.status_new,calendarItemService.getParticipantStatus(itemParticipantId.getId()));
    }

}

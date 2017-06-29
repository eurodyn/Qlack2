package com.eurodyn.qlack2.fuse.calendar.it;

import com.eurodyn.qlack2.fuse.calendar.api.CalendarService;
import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class CalendarServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    CalendarService calendarService;

    @Test
    public void createCalendar(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);
    }

    @Test
    public void updateCalendar(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        calendarDTO.setLastModifiedBy("testName06");
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);

        //find DTO by ID
        CalendarDTO calendarGetDTO = calendarService.getCalendar(calendarID);
        calendarGetDTO.setLastModifiedBy("testName07");

        calendarService.updateCalendar(calendarGetDTO);

        Assert.assertEquals("testName07",calendarService.getCalendar(calendarID).getLastModifiedBy());
    }

    @Test
    public void deleteCalendar(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);

        calendarService.deleteCalendar(calendarCrtDTO);

        Assert.assertNull(calendarService.getCalendar(calendarID));
    }

    @Test
    public void getCalendar(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        calendarDTO.setLastModifiedBy("testName08");
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);

        Assert.assertNotNull(calendarService.getCalendar(calendarID));
        Assert.assertEquals("testName08",calendarService.getCalendar(calendarID).getLastModifiedBy());
    }

    @Test
    public void getCalendars(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        calendarDTO.setLastModifiedBy("testName09");
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);

        Assert.assertNotNull(calendarService.getCalendars(true));
    }

    @Test
    public void getCalendarsArgs(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);

        Assert.assertNotNull(calendarService.getCalendars(calendarCrtDTO.getOwnerId()));
    }

    @Test
    public void activateCalendar(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        calendarDTO.setLastModifiedBy("testName10");
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);

        calendarService.activateCalendar(calendarID,calendarCrtDTO.getSrcUserId());

        //check if calendar is activated
        Assert.assertTrue(calendarService.isCalendarActive(calendarID));
    }

    @Test
    public void deactivateCalendar(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        calendarDTO.setLastModifiedBy("testName11");
        calendarDTO.setActive(true);
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);

        calendarService.deactivateCalendar(calendarID,calendarCrtDTO.getSrcUserId());

        //check if calendar is activated, expected false
        Assert.assertFalse(calendarService.isCalendarActive(calendarID));
    }

    @Test
    public void isCalendarActive(){
        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        calendarDTO.setLastModifiedBy("testName12");
        calendarDTO.setActive(true);
        CalendarDTO calendarCrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarCrtDTO.getId();
        Assert.assertNotNull(calendarID);

        //expected true
        Assert.assertTrue(calendarService.isCalendarActive(calendarID));
    }

}
package com.eurodyn.qlack2.fuse.calendar.it;

import com.eurodyn.qlack2.fuse.calendar.api.ImportExportService;
import com.eurodyn.qlack2.fuse.calendar.api.CalendarService;
import com.eurodyn.qlack2.fuse.calendar.api.CalendarItemService;
import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarItemDTO;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ImportExportServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    ImportExportService importExportService;

    @Inject
    @Filter(timeout = 1200000)
    CalendarService calendarService;

    @Inject
    @Filter(timeout = 1200000)
    CalendarItemService calendarItemService;

    @Test
    public void importCalendarItems(){
        Calendar crtCal = new GregorianCalendar(2017,5,21);
        Date crtDate = crtCal.getTime();

        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        calendarDTO.setCreatedOn(crtDate);
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO = TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        calendarItemDTO.setCreatedOn(crtDate);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        TestConst.importData(calendarItemID);
        importExportService.importCalendarItems(calendarID,TestConst.data,TestConst.case_num);
    }

    @Test
    public void exportCalendarItems(){
        Calendar strItemCal = new GregorianCalendar(2017,1,21);
        Date strItemDate = strItemCal.getTime();

        Calendar crtItemCal = new GregorianCalendar(2017,5,21);
        Date crtItemDate = crtItemCal.getTime();

        Calendar endItemCal = new GregorianCalendar(2017,9,21);
        Date endItemDate = endItemCal.getTime();

        CalendarDTO calendarDTO = TestUtilities.createCalendarDTO();
        calendarDTO.setCreatedOn(crtItemDate);
        CalendarDTO calendarGrtDTO = calendarService.createCalendar(calendarDTO);
        String calendarID = calendarGrtDTO.getId();
        Assert.assertNotNull(calendarID);

        CalendarItemDTO calendarItemDTO = TestUtilities.createCalendarItemDTO();
        calendarItemDTO.setCalendarId(calendarID);
        calendarItemDTO.setCreatedOn(crtItemDate);
        calendarItemDTO.setEndTime(endItemDate);
        calendarItemDTO.setStartTime(strItemDate);
        CalendarItemDTO calendarItemCrtDTO = calendarItemService.createItem(calendarItemDTO);
        String calendarItemID = calendarItemCrtDTO.getId();
        Assert.assertNotNull(calendarItemID);

        String categoryId = calendarItemCrtDTO.getCategoryId();
        String categoryIds[] = categoryId.split(" ");

        Calendar startCal = new GregorianCalendar(2017,1,21);
        Date startDate = startCal.getTime();

        Calendar endCal = new GregorianCalendar(2017,10,21);
        Date endDate = endCal.getTime();

        Assert.assertNotNull(importExportService.exportCalendarItems(calendarID,startDate,endDate,categoryIds,false));
    }

}

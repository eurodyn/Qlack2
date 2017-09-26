package com.eurodyn.qlack2.fuse.calendar.util;

import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarItemDTO;
import com.eurodyn.qlack2.fuse.calendar.api.dto.CalendarDTO;
import java.util.Date;
import java.util.UUID;

public class TestUtilities {

    public static CalendarItemDTO createCalendarItemDTO(){
        CalendarItemDTO calendarItemDTO = new CalendarItemDTO();
        calendarItemDTO.setCalendarId(UUID.randomUUID().toString());
        calendarItemDTO.setId(UUID.randomUUID().toString());
        calendarItemDTO.setName(TestConst.generateRandomString());
        calendarItemDTO.setCategoryId(UUID.randomUUID().toString());
        calendarItemDTO.setCreatedBy(TestConst.generateRandomString());
        calendarItemDTO.setContactId(TestConst.generateRandomString());
        calendarItemDTO.setCreatedOn(new Date());
        calendarItemDTO.setLastModifiedBy(TestConst.generateRandomString());
        calendarItemDTO.setLastModifiedOn(new Date());
        calendarItemDTO.setLocation(TestConst.generateRandomString());

        return calendarItemDTO;
    }

    public static CalendarDTO createCalendarDTO(){
        CalendarDTO calendarDTO = new CalendarDTO();
        calendarDTO.setLastModifiedBy(TestConst.generateRandomString());
        calendarDTO.setId(UUID.randomUUID().toString());
        calendarDTO.setCreatedOn(new Date());
        calendarDTO.setLastModifiedOn(new Date());
        calendarDTO.setOwnerId(UUID.randomUUID().toString());
        calendarDTO.setSrcUserId(UUID.randomUUID().toString());

        return calendarDTO;
    }

}

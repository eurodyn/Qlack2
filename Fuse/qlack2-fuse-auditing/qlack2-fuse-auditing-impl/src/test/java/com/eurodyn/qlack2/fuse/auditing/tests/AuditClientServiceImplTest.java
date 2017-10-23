package com.eurodyn.qlack2.fuse.auditing.tests;

import com.eurodyn.qlack2.fuse.auditing.api.AuditClientService;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLevelService;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLoggingService;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLevelDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.auditing.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.auditing.util.TestUtilities;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class AuditClientServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    AuditClientService auditClientService;

    @Inject
    @Filter(timeout = 1200000)
    AuditLoggingService auditLoggingService;

    @Inject
    @Filter(timeout = 1200000)
    AuditLevelService auditLevelService;

    @Test
    public void audit(){
        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = TestUtilities.createAuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        Assert.assertNotNull(auditClientService.audit(auditLogDTO));
    }

    @Test
    public void auditTrace(){
        //set Dates
        LocalDate nowDate = LocalDate.now();
        LocalDate nowMinusOneMonth = nowDate.minusMonths(1);
        LocalDate nowPlusOneMonth = nowDate.plusMonths(1);

        Date startDate = Date.from(nowMinusOneMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(nowPlusOneMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = TestUtilities.createAuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        //assigns argument to lists
        List<String> listLevel = new ArrayList();
        listLevel.add(auditLevelDTO.getName());

        List<String> referenceIds = new ArrayList();
        referenceIds.add(auditLogDTO.getReferenceId());

        List<String> groupNames = new ArrayList();
        groupNames.add(auditLogDTO.getGroupName());

        int auditsBefore = auditLoggingService.countAudits(listLevel, referenceIds, groupNames, startDate, endDate);
        auditClientService.audit(auditLogDTO.getLevel(),auditLogDTO.getEvent(),auditLogDTO.getGroupName(),auditLevelDTO.getDescription(),auditLogDTO.getPrinSessionId(),auditLogDTO.getTraceData());
        int auditsAfter = auditLoggingService.countAudits(listLevel, referenceIds, groupNames, startDate, endDate);
        Assert.assertTrue(auditsAfter > auditsBefore );
    }

    @Test
    public void auditRefer(){
        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = TestUtilities.createAuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        Assert.assertNotNull(auditClientService.audit(auditLogDTO.getLevel(),auditLogDTO.getEvent(),auditLogDTO.getGroupName(),auditLevelDTO.getDescription(),auditLogDTO.getPrinSessionId(),auditLogDTO.getTraceData(),auditLogDTO.getReferenceId()));
    }

    @Test
    public void auditTraceObj(){
        //set Dates
        LocalDate nowDate = LocalDate.now();
        LocalDate nowMinusOneMonth = nowDate.minusMonths(1);
        LocalDate nowPlusOneMonth = nowDate.plusMonths(1);

        Date startDate = Date.from(nowMinusOneMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(nowPlusOneMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = TestUtilities.createAuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        //assigns argument to lists
        List<String> listLevel = new ArrayList();
        listLevel.add(auditLevelDTO.getName());

        List<String> referenceIds = new ArrayList();
        referenceIds.add(auditLogDTO.getReferenceId());

        List<String> groupNames = new ArrayList();
        groupNames.add(auditLogDTO.getGroupName());

        Object traceData = auditLogDTO.getTraceData();

        int auditsBefore = auditLoggingService.countAudits(listLevel,referenceIds,groupNames,startDate,endDate);
        auditClientService.audit(auditLogDTO.getLevel(),auditLogDTO.getEvent(),auditLogDTO.getGroupName(),auditLevelDTO.getDescription(),auditLogDTO.getPrinSessionId(),traceData);
        int auditsAfter = auditLoggingService.countAudits(listLevel,referenceIds,groupNames,startDate,endDate);
        Assert.assertTrue(auditsAfter > auditsBefore );
    }

}

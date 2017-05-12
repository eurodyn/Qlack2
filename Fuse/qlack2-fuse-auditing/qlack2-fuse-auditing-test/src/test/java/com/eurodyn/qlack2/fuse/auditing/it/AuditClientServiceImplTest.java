package com.eurodyn.qlack2.fuse.auditing.it;

import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLevelService;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLoggingService;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLevelDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
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
        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = TestUtilities.createAuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        auditClientService.audit(auditLogDTO.getLevel(),auditLogDTO.getEvent(),auditLogDTO.getGroupName(),auditLevelDTO.getDescription(),auditLogDTO.getPrinSessionId(),auditLogDTO.getTraceData());
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

        auditClientService.audit(auditLogDTO.getLevel(),auditLogDTO.getEvent(),auditLogDTO.getGroupName(),auditLevelDTO.getDescription(),auditLogDTO.getPrinSessionId(),auditLogDTO.getTraceData(),auditLogDTO.getReferenceId());
    }

    @Test
    public void auditTraceObj(){
        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = TestUtilities.createAuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        Object traceData = auditLogDTO.getTraceData();

        auditClientService.audit(auditLogDTO.getLevel(),auditLogDTO.getEvent(),auditLogDTO.getGroupName(),auditLevelDTO.getDescription(),auditLogDTO.getPrinSessionId(),traceData);
    }

}

package com.eurodyn.qlack2.fuse.auditing.util;

import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLevelDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import java.util.Date;
import java.util.UUID;

public class TestUtilities {

    public static AuditLevelDTO createAuditLevelDTO(){
        AuditLevelDTO auditLevelDTO = new AuditLevelDTO();
        auditLevelDTO.setName(TestConst.generateRandomString());
        auditLevelDTO.setId(UUID.randomUUID().toString());
        auditLevelDTO.setCreatedOn(new Date());
        auditLevelDTO.setDescription("description for test");
        auditLevelDTO.setPrinSessionId(UUID.randomUUID().toString());

        return auditLevelDTO;
    }

    public static AuditLogDTO createAuditLogDTO(){
        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setId(UUID.randomUUID().toString());
        auditLogDTO.setCreatedOn(new Date());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setShortDescription("Description test");
        auditLogDTO.setEvent(UUID.randomUUID().toString());

        return auditLogDTO;
    }

}

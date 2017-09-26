package com.eurodyn.qlack2.fuse.auditing.tests;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLoggingService;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLevelService;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLevelDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SearchDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SortDTO;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SearchOperator;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SortOperator;
import com.eurodyn.qlack2.fuse.auditing.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.auditing.util.TestConst;
import com.eurodyn.qlack2.fuse.auditing.util.TestUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import com.eurodyn.qlack2.fuse.auditing.api.enums.AuditLogColumns;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class AuditLoggingServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    AuditLoggingService auditLoggingService;

    @Inject
    @Filter(timeout = 1200000)
    AuditLevelService auditLevelService;

    @Test
    public void logAudit(){
        //creates level
        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setId(UUID.randomUUID().toString());
        auditLogDTO.setCreatedOn(new Date());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setShortDescription("testDescr01");
        auditLogDTO.setEvent(UUID.randomUUID().toString());

        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);
    }

    @Test
    public void deleteAudit(){
        //creates level
        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setId(UUID.randomUUID().toString());
        auditLogDTO.setCreatedOn(new Date());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setShortDescription("testDescr02");
        auditLogDTO.setEvent(UUID.randomUUID().toString());

        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        //delete audit
        auditLoggingService.deleteAudit(auditLogID);

        //check if deleted
        Assert.assertNull(auditLoggingService.getAuditById(auditLogID));
    }

    @Test
    public void truncateAudits(){
        //create two Audits
        //Audit One - create level
        AuditLevelDTO auditLevelOneDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelOneID = auditLevelService.addLevel(auditLevelOneDTO);
        Assert.assertNotNull(auditLevelOneID);

        AuditLogDTO auditLogOneDTO = new AuditLogDTO();
        auditLogOneDTO.setLevel(auditLevelOneDTO.getName());
        auditLogOneDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogOneDTO.setId(UUID.randomUUID().toString());
        auditLogOneDTO.setCreatedOn(new Date());
        auditLogOneDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogOneDTO.setShortDescription("testDescr03");
        auditLogOneDTO.setEvent(UUID.randomUUID().toString());

        String auditLogOneID = auditLoggingService.logAudit(auditLogOneDTO);
        Assert.assertNotNull(auditLogOneID);

        //Audit Two - create level
        AuditLevelDTO auditLevelTwoDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelTwoID = auditLevelService.addLevel(auditLevelTwoDTO);
        Assert.assertNotNull(auditLevelTwoID);

        AuditLogDTO auditLogTwoDTO = new AuditLogDTO();
        auditLogTwoDTO.setLevel(auditLevelTwoDTO.getName());
        auditLogTwoDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogTwoDTO.setId(UUID.randomUUID().toString());
        auditLogTwoDTO.setCreatedOn(new Date());
        auditLogTwoDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogTwoDTO.setShortDescription("testDescr04");
        auditLogTwoDTO.setEvent(UUID.randomUUID().toString());

        String auditLogTwoID = auditLoggingService.logAudit(auditLogTwoDTO);
        Assert.assertNotNull(auditLogTwoID);

        //Delete all audit logs.
        auditLoggingService.truncateAudits();

        //check if Audit-One deleted, expected:null
        Assert.assertNull(auditLoggingService.getAuditById(auditLogOneID));
        //check if Audit-Two deleted, expected:null
        Assert.assertNull(auditLoggingService.getAuditById(auditLogTwoID));
    }

    @Test
    public void truncateAuditsDate(){
        long millisOne = System.currentTimeMillis();
        Date dateOne = new Date(millisOne);

        long specificMillis = System.currentTimeMillis()+1;
        Date SpecificDate = new Date(specificMillis);

        long millisTwo= System.currentTimeMillis()+2;
        Date dateTwo = new Date(millisTwo);

        //create two Audits
        //Audit One - create level
        AuditLevelDTO auditLevelOneDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelOneID = auditLevelService.addLevel(auditLevelOneDTO);
        Assert.assertNotNull(auditLevelOneID);

        AuditLogDTO auditLogOneDTO = new AuditLogDTO();
        auditLogOneDTO.setLevel(auditLevelOneDTO.getName());
        auditLogOneDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogOneDTO.setId(UUID.randomUUID().toString());
        auditLogOneDTO.setCreatedOn(dateOne);
        auditLogOneDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogOneDTO.setShortDescription("testDescr05");
        auditLogOneDTO.setEvent(UUID.randomUUID().toString());

        String auditLogOneID = auditLoggingService.logAudit(auditLogOneDTO);
        Assert.assertNotNull(auditLogOneID);

        //Audit Two - create level
        AuditLevelDTO auditLevelTwoDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelTwoID = auditLevelService.addLevel(auditLevelTwoDTO);
        Assert.assertNotNull(auditLevelTwoID);

        AuditLogDTO auditLogTwoDTO = new AuditLogDTO();
        auditLogTwoDTO.setLevel(auditLevelTwoDTO.getName());
        auditLogTwoDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogTwoDTO.setId(UUID.randomUUID().toString());
        auditLogTwoDTO.setCreatedOn(dateTwo);
        auditLogTwoDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogTwoDTO.setShortDescription("testDescr06");
        auditLogTwoDTO.setEvent(UUID.randomUUID().toString());

        String auditLogTwoID = auditLoggingService.logAudit(auditLogTwoDTO);
        Assert.assertNotNull(auditLogTwoID);

        //Delete all audit logs.
        auditLoggingService.truncateAudits(SpecificDate);

        //check if auditLogOneID has been deleted, expected: null
        Assert.assertNull(auditLoggingService.getAuditById(auditLogOneID));

        //check if auditLogTwoID has been deleted, expected: not null
        Assert.assertNotNull(auditLoggingService.getAuditById(auditLogTwoID));
    }

    @Test
    public void truncateAuditsPeriod(){
        long millisOne = System.currentTimeMillis();
        Date dateOne = new Date(millisOne);

        long specificMillis = System.currentTimeMillis()+1;
        Date retentionPeriod = new Date(specificMillis);

        long millisTwo= System.currentTimeMillis()+2;
        Date dateTwo = new Date(millisTwo);

        //create two Audits
        //Audit One - create level
        AuditLevelDTO auditLevelOneDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelOneID = auditLevelService.addLevel(auditLevelOneDTO);
        Assert.assertNotNull(auditLevelOneID);

        AuditLogDTO auditLogOneDTO = new AuditLogDTO();
        auditLogOneDTO.setLevel(auditLevelOneDTO.getName());
        auditLogOneDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogOneDTO.setId(UUID.randomUUID().toString());
        auditLogOneDTO.setCreatedOn(dateOne);
        auditLogOneDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogOneDTO.setShortDescription("testDescr07");
        auditLogOneDTO.setEvent(UUID.randomUUID().toString());

        String auditLogOneID = auditLoggingService.logAudit(auditLogOneDTO);
        Assert.assertNotNull(auditLogOneID);


        //Audit Two - create level
        AuditLevelDTO auditLevelTwoDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelTwoID = auditLevelService.addLevel(auditLevelTwoDTO);
        Assert.assertNotNull(auditLevelTwoID);

        AuditLogDTO auditLogTwoDTO = new AuditLogDTO();
        auditLogTwoDTO.setLevel(auditLevelTwoDTO.getName());
        auditLogTwoDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogTwoDTO.setId(UUID.randomUUID().toString());
        auditLogTwoDTO.setCreatedOn(dateTwo);
        auditLogTwoDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogTwoDTO.setShortDescription("testDescr08");
        auditLogTwoDTO.setEvent(UUID.randomUUID().toString());

        String auditLogTwoID = auditLoggingService.logAudit(auditLogTwoDTO);
        Assert.assertNotNull(auditLogTwoID);

        //Delete all audit logs older than a specific period of time
        auditLoggingService.truncateAudits(retentionPeriod);

        //check if auditLogOneID has been deleted, expected: null
        Assert.assertNull(auditLoggingService.getAuditById(auditLogOneID));

        //check if auditLogTwoID has been deleted, expected: not null
        Assert.assertNotNull(auditLoggingService.getAuditById(auditLogTwoID));
    }

    @Test
    public void getAuditById(){
        //create level
        AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelID);

        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setId(UUID.randomUUID().toString());
        auditLogDTO.setCreatedOn(new Date());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setShortDescription("testDescr09");
        auditLogDTO.setEvent(UUID.randomUUID().toString());

        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        Assert.assertNotNull(auditLoggingService.getAuditById(auditLogID));
    }

    @Test
    public void listAudits() {
        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        Calendar startCale = new GregorianCalendar(2017, 1, 21);
        Date startDate = startCale.getTime();

        Calendar crtCale = new GregorianCalendar(2017, 5, 22);
        Date crtDate = crtCale.getTime();

        Calendar endCale = new GregorianCalendar(2017, 9, 21);
        Date endDate = endCale.getTime();

        AuditLevelDTO auditLevelDTO = new AuditLevelDTO();
        auditLevelDTO.setId(UUID.randomUUID().toString());
        auditLevelDTO.setName("testName01");
        auditLevelDTO.setCreatedOn(crtDate);
        String auditLevelOneID = auditLevelService.addLevel(auditLevelDTO);
        Assert.assertNotNull(auditLevelOneID);

        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        auditLogDTO.setId(UUID.randomUUID().toString());
        auditLogDTO.setCreatedOn(crtDate);
        auditLogDTO.setShortDescription("testDescr16");
        auditLogDTO.setEvent(UUID.randomUUID().toString());
        auditLogDTO.setGroupName(TestConst.generateRandomString());
        auditLogDTO.setReferenceId(UUID.randomUUID().toString());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        List<String> levelNames = new ArrayList();
        levelNames.add(auditLevelDTO.getName());

        List<String> groupNames = new ArrayList();
        groupNames.add(auditLogDTO.getGroupName());

        List<String> referenceID = new ArrayList();
        referenceID.add(auditLogDTO.getReferenceId());

        Assert.assertNotNull(auditLoggingService.listAudits(levelNames, referenceID, groupNames, null, null, true, pagingParams));
        Assert.assertNotNull(auditLoggingService.listAudits(levelNames, referenceID, groupNames, startDate, endDate, true, pagingParams));
        Assert.assertNotNull(auditLoggingService.listAudits(levelNames, referenceID, groupNames, null, endDate, true, pagingParams));
        Assert.assertNotNull(auditLoggingService.listAudits(levelNames, referenceID, groupNames, startDate, null, true, pagingParams));
    }

    @Test
    public void countAudits(){
        //set Dates
        Calendar startCale = new GregorianCalendar(2017,1,21);
        Date startDate = startCale.getTime();

        Calendar crtCale = new GregorianCalendar(2017,5,21);
        Date crtDate = crtCale.getTime();

        Calendar endCale = new GregorianCalendar(2017,9,21);
        Date endDate = endCale.getTime();

        //creates level
        AuditLevelDTO auditLevelDTO = new AuditLevelDTO();
        auditLevelDTO.setName("testName02");
        auditLevelDTO.setId(UUID.randomUUID().toString());
        auditLevelDTO.setCreatedOn(crtDate);
        String auditLevelID = auditLevelService.addLevel(auditLevelDTO);

        //creates audit
        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setLevel(auditLevelDTO.getName());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setId(UUID.randomUUID().toString());
        auditLogDTO.setCreatedOn(crtDate);
        auditLogDTO.setReferenceId(UUID.randomUUID().toString());
        auditLogDTO.setPrinSessionId(UUID.randomUUID().toString());
        auditLogDTO.setShortDescription("testDescr17");
        auditLogDTO.setEvent(UUID.randomUUID().toString());
        auditLogDTO.setGroupName(TestConst.generateRandomString());
        String auditLogID = auditLoggingService.logAudit(auditLogDTO);
        Assert.assertNotNull(auditLogID);

        //assigns argument to lists
        List<String> listLevel = new ArrayList();
        listLevel.add(auditLevelDTO.getName());

        List<String> referenceIds = new ArrayList();
        referenceIds.add(auditLogDTO.getReferenceId());

        List<String> groupNames = new ArrayList();
        groupNames.add(auditLogDTO.getGroupName());

        Assert.assertNotNull(auditLoggingService.countAudits(listLevel,referenceIds,groupNames,startDate,endDate));
        Assert.assertTrue(auditLoggingService.countAudits(listLevel,referenceIds,groupNames,startDate,endDate) != 0);
        Assert.assertNotNull(auditLoggingService.countAudits(listLevel,referenceIds,groupNames,startDate,endDate));
        Assert.assertNotNull(auditLoggingService.countAudits(listLevel,referenceIds,groupNames,null,endDate));
        Assert.assertNotNull(auditLoggingService.countAudits(listLevel,referenceIds,groupNames,startDate,null));
        Assert.assertNotNull(auditLoggingService.countAudits(listLevel,referenceIds,groupNames,null,null));
    }

    @Test
    public void listAuditLogs(){
        //set params
        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        //creates start and end date to search
        Calendar startCale = new GregorianCalendar(2017,1,21);
        Date startDate = startCale.getTime();

        Calendar endCale = new GregorianCalendar(2017,9,21);
        Date endDate = endCale.getTime();

        AuditLogColumns auditLogColumns = AuditLogColumns.levelId;
        SearchOperator searchOperator = SearchOperator.EQUAL;
        SortOperator sortOperator = SortOperator.ASC;

        List valuelist = new ArrayList();
        valuelist.add("testValue01");

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setColumn(auditLogColumns);
        searchDTO.setOperator(searchOperator);
        searchDTO.setValue(valuelist);

        SortDTO sortDTO = new SortDTO();
        sortDTO.setColumn(auditLogColumns);
        sortDTO.setOperator(sortOperator);

        List searchList = new ArrayList();
        searchList.add(searchDTO);

        List sortList = new ArrayList();
        sortList.add(sortDTO);

        Assert.assertNotNull(auditLoggingService.listAuditLogs(searchList,startDate,endDate,sortList,pagingParams));
    }

    @Test
    public void countAuditLogs(){
        //creates start and end date to search
        Calendar startCale = new GregorianCalendar(2017,1,21);
        Date startDate = startCale.getTime();

        Calendar endCale = new GregorianCalendar(2017,9,21);
        Date endDate = endCale.getTime();

        AuditLogColumns auditLogColumns = AuditLogColumns.levelId;
        SearchOperator searchOperator = SearchOperator.EQUAL;

        List valuelist = new ArrayList();
        valuelist.add("testValue02");

        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setColumn(auditLogColumns);
        searchDTO.setOperator(searchOperator);
        searchDTO.setValue(valuelist);

        List searchList = new ArrayList();
        searchList.add(searchDTO);

        Assert.assertNotNull(auditLoggingService.countAuditLogs(searchList,endDate,startDate));
    }

}

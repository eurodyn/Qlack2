package com.eurodyn.qlack2.fuse.auditing.tests;

import com.eurodyn.qlack2.fuse.auditing.api.AuditLevelService;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLevelDTO;
import com.eurodyn.qlack2.fuse.auditing.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.auditing.util.TestUtilities;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class AuditLevelServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  AuditLevelService auditLevelService;

  @Test
  public void addLevel() {
    //adds new level
    AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
    String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
    Assert.assertNotNull(auditLevelID);
  }

  @Test
  public void addLevelIfNotExists() {
    // Adds a new level.
    AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
    String auditLevelID = auditLevelService.addLevelIfNotExists(auditLevelDTO);
    Assert.assertNotNull(auditLevelID);

    // Adds a new level without an id.
    auditLevelDTO = TestUtilities.createAuditLevelDTO();
    auditLevelDTO.setId(null);
    auditLevelID = auditLevelService.addLevelIfNotExists(auditLevelDTO);
    Assert.assertNotNull(auditLevelID);

    // Try to re-add the same level.
    auditLevelID = auditLevelService.addLevelIfNotExists(auditLevelDTO);
    Assert.assertNull(auditLevelID);
  }

  @Test
  public void deleteLevelById() {
    //adds new level
    AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
    auditLevelDTO.setName("testName11"); //set name=test to use it in getAuditLevelByName()
    String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
    Assert.assertNotNull(auditLevelID);

    //check if auditLevelID exists, expected: not null
    Assert.assertNotNull(auditLevelService.getAuditLevelByName("testName11"));

    //delete level
    auditLevelService.deleteLevelById(auditLevelID);

    //check if level deleted successfully, expected:null
    Assert.assertNull(auditLevelService.getAuditLevelByName("testName11"));
  }

  @Test
  public void deleteLevelByName() {
    //add new level
    AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
    auditLevelDTO.setName("testName12"); //set name=test to use it in getAuditLevelByName()
    String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
    Assert.assertNotNull(auditLevelID);

    //check if auditLevelID exists expected: not null
    Assert.assertNotNull(auditLevelService.getAuditLevelByName("testName12"));

    //delete level
    auditLevelService.deleteLevelByName(auditLevelDTO.getName());

    //check if level deleted successfully, expected:null
    Assert.assertNull(auditLevelService.getAuditLevelByName("testName12"));
  }

  @Test
  public void updateLevel() {
    //adds new level
    AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
    auditLevelDTO.setName("testName13"); //set name=test to use it in getAuditLevelByName()
    String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
    Assert.assertNotNull(auditLevelID);

    AuditLevelDTO auditLevelUpdDTO = auditLevelService.getAuditLevelByName("testName13");
    auditLevelUpdDTO.setDescription("testDescr13");

    //call updateLevel with updated DTO
    auditLevelService.updateLevel(auditLevelUpdDTO);

    //check if Description is updated
    Assert.assertEquals("testDescr13",
      auditLevelService.getAuditLevelByName("testName13").getDescription());
  }

  @Test
  public void getAuditLevelByName() {
    //add new level
    AuditLevelDTO auditLevelDTO = TestUtilities.createAuditLevelDTO();
    auditLevelDTO.setName("testName14"); //set name=test to use it in getAuditLevelByName()
    String auditLevelID = auditLevelService.addLevel(auditLevelDTO);
    Assert.assertNotNull(auditLevelID);

    Assert
      .assertEquals("testName14", auditLevelService.getAuditLevelByName("testName14").getName());
  }

  @Test
  public void listAuditLevels() {
    //add two levels
    //add level-1
    AuditLevelDTO auditLevelOneDTO = TestUtilities.createAuditLevelDTO();
    String auditLevelOneID = auditLevelService.addLevel(auditLevelOneDTO);
    Assert.assertNotNull(auditLevelOneID);

    //add level-2
    AuditLevelDTO auditLevelTwoDTO = TestUtilities.createAuditLevelDTO();
    String auditLevelTwoID = auditLevelService.addLevel(auditLevelTwoDTO);
    Assert.assertNotNull(auditLevelTwoID);

    Assert.assertNotNull(auditLevelService.listAuditLevels());
    Assert.assertTrue(auditLevelService.listAuditLevels().size() != 0);
  }

}

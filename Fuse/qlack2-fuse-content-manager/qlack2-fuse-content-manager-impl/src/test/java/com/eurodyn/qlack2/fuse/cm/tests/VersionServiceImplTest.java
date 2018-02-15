package com.eurodyn.qlack2.fuse.cm.tests;

import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.dto.FileDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.fuse.cm.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.cm.util.TestConst;
import com.eurodyn.qlack2.fuse.cm.util.TestUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.Map;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class VersionServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  VersionService versionService;

  @Inject
  @Filter(timeout = 1200000)
  DocumentService documentService;

  @Test
  public void createVersion() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename01",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);
  }

  @Test
  public void getFileVersions() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename02",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Assert.assertNotNull(versionService.getFileVersions(fileID));
  }

  @Test
  public void getFileLatestVersion() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename03",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Assert.assertNotNull(versionService.getFileLatestVersion(fileID));
  }

  @Test
  public void getBinContent() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename04",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Assert.assertNotNull(versionService.getBinContent(fileID));
  }

  @Test
  public void getBinContentArgs() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename05",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Assert.assertNotNull(versionService.getBinContent(fileID, versionDTO.getName()));
  }

  @Test
  public void getFileAsZip() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename06",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Assert.assertNotNull(versionService.getFileAsZip(fileID, false));
  }

  @Test
  public void getFileAsZipArgs() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename07",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Assert.assertNotNull(versionService.getFileAsZip(fileID, versionDTO.getName(), false));
  }

  @Test
  public void setBinChunk() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename08",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    versionService.setBinChunk(versionID, TestConst.content, 1);
    Assert.assertNotNull(versionService.getBinContent(fileID, versionDTO.getName()));
    Assert.assertNotNull(versionService.getBinChunk(versionID, 1));
  }

  @Test
  public void getBinChunk() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename09",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    versionService.setBinChunk(versionID, TestConst.content, 1);
    Assert.assertNotNull(versionService.getBinContent(fileID, versionDTO.getName()));
    Assert.assertNotNull(versionService.getBinChunk(versionID, 1));
  }

  @Test
  public void updateAttribute() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename10",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Map<String, String> getAtr = versionDTO.getAttributes();
    String attr = getAtr.toString();

    versionService.updateAttribute(fileID, attr, "attr01", TestConst.userID, fileDTO.getId());

    Assert.assertNotNull(versionService.getFileLatestVersion(fileID).getAttributes());
  }

  @Test
  public void updateAttributeOne() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename11",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Map<String, String> getAtr = versionDTO.getAttributes();
    String attr = getAtr.toString();

    versionService.updateAttribute(fileID, versionDTO.getName(), versionDTO.getName(), "attr02",
        TestConst.userID, fileDTO.getId());

    Assert.assertNotNull(versionService.getFileLatestVersion(fileID).getAttributes());
  }

  @Test
  public void updateAttributeTwo() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename12",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    Map<String, String> getAtr = versionDTO.getAttributes();
    String attr = getAtr.toString();

    versionService.updateAttribute(fileID, "filename12", attr, TestConst.userID, fileDTO.getId());

    Assert.assertNotNull(versionService.getFileLatestVersion(fileID).getAttributes());
  }

  @Test
  public void updateVersion() {

    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename15",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    versionDTO.setFilename("filename15");
    versionService.updateVersion(fileID, versionDTO, TestConst.content, TestConst.userID, false,
        null);

    VersionDTO persistedVersion = versionService.getFileLatestVersion(fileID);
    Assert.assertNotEquals(versionDTO.getLastModifiedOn(), persistedVersion.getLastModifiedOn());

  }

  @Test
  public void deleteAttribute() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename13",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);


    versionService.deleteAttribute(fileID, versionDTO.getName(), "LAST MODIFIED ON", null, null);
    Assert.assertNull(
        versionService.getFileLatestVersion(fileID).getAttributes().get("LAST MODIFIED ON"));
  }

  @Test
  public void deleteAttributeArgs() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename14",
        TestConst.content, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(versionID);

    versionService.deleteAttribute(fileID, "LAST MODIFIED ON", null, null);

    Assert.assertNull(
        versionService.getFileLatestVersion(fileID).getAttributes().get("LAST MODIFIED ON"));
  }

  @Test
  public void getVersionById() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename01",
        TestConst.content, TestConst.userID, fileDTO.getId());
    VersionDTO persistedVersionDTO = versionService.getVersionById(versionID);
    Assert.assertNotNull(persistedVersionDTO);
  }

  @Test
  public void deleteVersionById() {
    FileDTO fileDTO = TestUtilities.createFileDTO();
    String fileID = documentService.createFile(fileDTO, TestConst.userID, fileDTO.getId());
    Assert.assertNotNull(fileID);

    VersionDTO versionDTO = TestUtilities.createVersionDTO();
    String versionID = versionService.createVersion(fileID, versionDTO, "filename01",
        TestConst.content, TestConst.userID, fileDTO.getId());
    versionService.deleteVersion(versionID, null);

    VersionDTO persistedVersionDTO = versionService.getVersionById(versionID);
    Assert.assertNull(persistedVersionDTO);
  }

}

package com.eurodyn.qlack2.fuse.cm.tests;

import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.dto.FolderDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.FileDTO;
import com.eurodyn.qlack2.fuse.cm.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.cm.util.TestConst;
import com.eurodyn.qlack2.fuse.cm.util.TestUtilities;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;
import java.util.*;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class DocumentServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    DocumentService documentService;

    @Inject
    @Filter(timeout = 1200000)
    VersionService versionService;

    @Test
    public void createFolder(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO, TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);
    }

    @Test
    public void deleteFolder(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        documentService.deleteFolder(folderID,"lock06");
        Assert.assertNull(documentService.getFolderByID(folderID,false,false));
    }

    @Test
    public void renameFolder(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        documentService.renameFolder(folderID,TestConst.newName,TestConst.userID,"lock07");
        Assert.assertEquals(TestConst.newName,documentService.getFolderByID(folderID,false,false).getName());
    }

    @Test
    public void getFolderByID(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        Assert.assertNotNull(documentService.getFolderByID(folderID,false,false));
    }

    @Test
    public void getFolderAsZip(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        Assert.assertNotNull(documentService.getFolderAsZip(folderID,true,false));
    }

    @Test
    public void createFile(){
        FileDTO fileDTO = TestUtilities.createFileDTO();
        String fileID = documentService.createFile(fileDTO,TestConst.userID,fileDTO.getId());
        Assert.assertNotNull(fileID);
    }

    @Test
    public void deleteFile(){
        FileDTO fileDTO = TestUtilities.createFileDTO();
        String fileID = documentService.createFile(fileDTO,TestConst.userID,fileDTO.getId());
        Assert.assertNotNull(fileID);

        documentService.deleteFile(fileID,fileDTO.getId());
        Assert.assertNull(documentService.getFileByID(fileID,false,false));
    }

    @Test
    public void renameFile(){
        FileDTO fileDTO = TestUtilities.createFileDTO();
        String fileID = documentService.createFile(fileDTO,TestConst.userID,fileDTO.getId());
        Assert.assertNotNull(fileID);

        documentService.renameFile(fileID,TestConst.newFileName,TestConst.userID,fileDTO.getId());
        Assert.assertEquals(TestConst.newFileName,documentService.getFileByID(fileID,false,false).getName());
    }

    @Test
    public void getFileByID(){
        FileDTO fileDTO = TestUtilities.createFileDTO();
        String fileID = documentService.createFile(fileDTO,TestConst.userID,fileDTO.getId());
        Assert.assertNotNull(fileID);

        Assert.assertNotNull(documentService.getFileByID(fileID,false,false));
    }

    @Test
    public void getNodeByID(){
        FileDTO fileDTO = TestUtilities.createFileDTO();
        String fileID = documentService.createFile(fileDTO,TestConst.userID,fileDTO.getId());
        Assert.assertNotNull(fileID);

        NodeDTO nodeDTO = TestUtilities.createNodeDTO();
        Assert.assertNotNull(nodeDTO);

        Assert.assertNotNull(documentService.getNodeByID(fileID));
    }
    
    @Test
    public  void getNodeByAttributes() {
      FolderDTO folderDTO = TestUtilities.createFolderDTO();
      String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
      Assert.assertNotNull(folderID);
      Assert.assertNotNull(documentService.getNodeByAttributes(folderID,null));
    }

    @Test
    public void getParent(){
        NodeDTO nodeDTO = TestUtilities.createNodeDTO();
        Assert.assertNotNull(nodeDTO);

        FolderDTO parentDTO = TestUtilities.createFolderDTO();
        String parentID = documentService.createFolder(parentDTO,TestConst.userID,parentDTO.getId());
        Assert.assertNotNull(parentID);

        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        String nodeID = documentService.copy(folderID,parentID,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(documentService.getParent(nodeID,false));
    }

    @Test
    public void getAncestors(){
        FolderDTO parentDTO = TestUtilities.createFolderDTO();
        String parentID = documentService.createFolder(parentDTO,TestConst.userID,parentDTO.getId());
        Assert.assertNotNull(parentID);

        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        String nodeID = documentService.copy(folderID,parentID,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(documentService.getAncestors(nodeID));
    }

    @Test
    public void createAttribute(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        Assert.assertNotNull(documentService.createAttribute(folderID,"attrName05","attr05",TestConst.userID,folderDTO.getId()));
    }

    @Test
    public void updateAttribute(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        //create Attribute
        Assert.assertNotNull(documentService.createAttribute(folderID,"attrName06","attr06",TestConst.userID,folderDTO.getId()));

        //update Attribute with new value
        documentService.updateAttribute(folderID,"attrName06","attr07",TestConst.userID,folderDTO.getId());

        NodeDTO nodeDTO = documentService.getNodeByID(folderID);
        Assert.assertEquals("attr07",nodeDTO.getAttributes().get("attrName06"));
    }

    @Test
    public void updateAttributeArgs(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        //create Attribute
        Assert.assertNotNull(documentService.createAttribute(folderID,"attrName08","attr08",TestConst.userID,folderDTO.getId()));

        //new map to hold the attributes
        Map<String,String> attr = new HashMap<String, String>();
        attr.put("attrName08","attr09");

        //update Attribute with new value
        documentService.updateAttributes(folderID,attr,TestConst.userID,folderDTO.getId());

        NodeDTO nodeDTO = documentService.getNodeByID(folderID);
        Assert.assertEquals("attr09",nodeDTO.getAttributes().get("attrName08"));
    }

    @Test
    public void deleteAttribute(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        //create Attribute
        Assert.assertNotNull(documentService.createAttribute(folderID,"attrName10","attr10",TestConst.userID,folderDTO.getId()));

        //new map to hold the attributes
        Map<String,String> attr = new HashMap<String, String>();
        attr.put("attrName10","attr11");

        //update Attribute with new value
        documentService.deleteAttribute(folderID,"attrName10",TestConst.userID,folderDTO.getId());

        NodeDTO nodeDTO = documentService.getNodeByID(folderID);

        //expect that attributes is null
        Assert.assertNull(nodeDTO.getAttributes().get("attrName10"));
    }

    @Test
    public void copy(){
        FolderDTO parentDTO = TestUtilities.createFolderDTO();
        String parentID = documentService.createFolder(parentDTO,TestConst.userID,parentDTO.getId());
        Assert.assertNotNull(parentID);

        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        String nodeID = documentService.copy(folderID,parentID,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(nodeID);
    }

    @Test
    public void move() {
        FolderDTO parentDTO = TestUtilities.createFolderDTO();
        String parentID = documentService.createFolder(parentDTO, TestConst.userID, parentDTO.getId());
        Assert.assertNotNull(parentID);

        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO, TestConst.userID, folderDTO.getId());
        Assert.assertNotNull(folderID);

        documentService.move(folderID, parentID, TestConst.userID, folderDTO.getId());
        Assert.assertEquals(parentID,documentService.getParent(folderID,false).getId());
    }

    @Test
    public void isFileNameUnique() {
        FolderDTO parentDTO = TestUtilities.createFolderDTO();
        String parentID = documentService.createFolder(parentDTO, TestConst.userID, parentDTO.getId());
        Assert.assertNotNull(parentID);

        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO, TestConst.userID, folderDTO.getId());
        Assert.assertNotNull(folderID);

        documentService.copy(folderID, parentID, TestConst.userID, folderDTO.getId());
        Assert.assertFalse(documentService.isFileNameUnique(folderDTO.getName(),parentID));
    }

    @Test
    public void duplicateFileNamesInDirectory(){
        FolderDTO parentDTO = TestUtilities.createFolderDTO();
        String parentID = documentService.createFolder(parentDTO, TestConst.userID, parentDTO.getId());
        Assert.assertNotNull(parentID);

        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO, TestConst.userID, folderDTO.getId());
        Assert.assertNotNull(folderID);

        List<String> fileNames = new ArrayList<>();
        fileNames.add(folderDTO.getName());

        documentService.copy(folderID, parentID, TestConst.userID, folderDTO.getId());

        //expect that duplicate filename exist
        Assert.assertNotNull(documentService.duplicateFileNamesInDirectory(fileNames,parentID));
    }

    @Test
    public void createFileAndVersion(){
        FileDTO fileDTO = TestUtilities.createFileDTO();

        VersionDTO versionDTO = TestUtilities.createVersionDTO();

        Assert.assertNotNull(documentService.createFileAndVersion(fileDTO,versionDTO,TestConst.content,TestConst.userID,fileDTO.getId()));
    }

}

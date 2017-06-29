package com.eurodyn.qlack2.fuse.cm.it;

import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.ConcurrencyControlService;
import com.eurodyn.qlack2.fuse.cm.api.dto.FolderDTO;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class ConcurrencyControlServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    DocumentService documentService;

    @Inject
    @Filter(timeout = 1200000)
    ConcurrencyControlService concurrencyControlService;

    @Test
    public void lock(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        concurrencyControlService.lock(folderID,"locked01",false,TestConst.userID);
        Assert.assertNotNull(concurrencyControlService.getSelectedNodeWithLockConflict(folderID,"unlocked01"));
    }

    @Test
    public void unlock(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        concurrencyControlService.lock(folderID,folderDTO.getLockedBy(),false,TestConst.userID);
        concurrencyControlService.unlock(folderID,folderDTO.getLockedBy(),false,TestConst.userID);

        //expect null
        Assert.assertNull(concurrencyControlService.getSelectedNodeWithLockConflict(folderID,"unlocked02"));
    }

    @Test
    public void getSelectedNodeWithLockConflict(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        concurrencyControlService.lock(folderID,"locked03",false,TestConst.userID);

        Assert.assertNotNull(concurrencyControlService.getSelectedNodeWithLockConflict(folderID,"unlocked03"));
    }

    @Test
    public void getAncestorFolderWithLockConflict(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        concurrencyControlService.lock(folderID,"locked04",false,TestConst.userID);

        Assert.assertNotNull(concurrencyControlService.getAncestorFolderWithLockConflict(folderID,"unlocked04"));
    }

    @Test
    public void getDescendantNodeWithLockConflict(){
        FolderDTO folderDTO = TestUtilities.createFolderDTO();
        String folderID = documentService.createFolder(folderDTO,TestConst.userID,folderDTO.getId());
        Assert.assertNotNull(folderID);

        concurrencyControlService.lock(folderID,"locked05",false,TestConst.userID);

        //Descendant node isnt locked, expcted null
        Assert.assertNull(concurrencyControlService.getDescendantNodeWithLockConflict(folderID,"unlocked05"));
    }

}

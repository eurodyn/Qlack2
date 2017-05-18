package com.eurodyn.qlack2.fuse.clipboard.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.clipboard.api.ClipboardService;
import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardEntryDTO;
import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardMetaDTO;
import com.eurodyn.qlack2.fuse.clipboard.api.exception.QClipboardException;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;
import java.util.*;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ClipboardServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    ClipboardService clipboardService;

    @Test
    public void createEntry(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);
    }

    @Test
    public void updateEntry(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        clipboardEntryDTO.setDescription("test");
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);

        clipboardEntryDTO.setDescription("updated");
        clipboardService.updateEntry(clipboardEntryDTO);
        ClipboardEntryDTO clipboardEntryUpd = clipboardService.getEntry(clipboardEntryID.getId());

        Assert.assertEquals("updated",clipboardEntryUpd.getDescription());
    }

    @Test(expected=QClipboardException.class)
    public void deleteEntry(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        clipboardEntryDTO.setDescription("test");
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);

        clipboardService.deleteEntry(clipboardEntryDTO);

        //expected that clipboardEntry doesn't exist (QClipboardException)
        Assert.assertNull(clipboardService.getEntry(clipboardEntryID.getId()));
    }

    @Test(expected=QClipboardException.class)
    public void deleteEntries(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);

        List listIds = new ArrayList();
        listIds.add(clipboardEntryID.getId());

        clipboardService.deleteEntry(clipboardEntryDTO);

        //expected that clipboardEntry doesn't exist (QClipboardException)
        Assert.assertNull(clipboardService.getEntry(clipboardEntryID.getId()));
    }

    @Test
    public void getEntries(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);

        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        Assert.assertNotNull(clipboardService.getEntries(clipboardEntryDTO.getOwnerId(),pagingParams));
    }

    @Test
    public void getEntriesArgs(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);

        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        Assert.assertNotNull(clipboardService.getEntries(clipboardEntryDTO.getOwnerId(),clipboardEntryDTO.getTypeId(),pagingParams));
    }

    @Test
    public void addEntryMeta(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);

        ClipboardMetaDTO clipboardMetaDTO = TestUtilities.createClipboardMetaDTO();
        Assert.assertNotNull(clipboardService.addEntryMeta(clipboardEntryID.getId(),clipboardMetaDTO));
    }

    @Test
    public void updateEntryMeta(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);

        ClipboardMetaDTO clipboardMetaDTO = TestUtilities.createClipboardMetaDTO();
        clipboardService.addEntryMeta(clipboardEntryID.getId(),clipboardMetaDTO);

        //update clipboardEntryDTO
        clipboardMetaDTO.setValue("updated");
        clipboardService.updateEntryMeta(clipboardMetaDTO);
    }

    @Test
    public void removeEntryMeta(){
        ClipboardEntryDTO clipboardEntryDTO = TestUtilities.createClipboardEntryDTO();
        ClipboardEntryDTO clipboardEntryID = clipboardService.createEntry(clipboardEntryDTO);
        Assert.assertNotNull(clipboardEntryID);

        ClipboardMetaDTO clipboardMetaDTO = TestUtilities.createClipboardMetaDTO();

        clipboardService.addEntryMeta(clipboardEntryID.getId(),clipboardMetaDTO);
        clipboardService.removeEntryMeta(clipboardMetaDTO);
    }

}

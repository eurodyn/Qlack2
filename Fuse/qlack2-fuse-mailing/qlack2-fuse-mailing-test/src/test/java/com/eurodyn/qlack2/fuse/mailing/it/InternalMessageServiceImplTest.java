package com.eurodyn.qlack2.fuse.mailing.it;

import com.eurodyn.qlack2.fuse.mailing.api.InternalMessageService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.InternalAttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.InternalMessagesDTO;
import com.eurodyn.qlack2.fuse.mailing.impl.util.MaiConstants;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import java.util.Iterator;
import java.util.List;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class InternalMessageServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  InternalMessageService internalMessageService;

  @Test
  public void sendInternalMail() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);
  }

  @Test
  public void getInternalInboxFolder() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    Assert.assertNotNull(
      internalMessageService.getInternalInboxFolder(internalMessagesID.getSrcUserId()));
  }

  @Test
  public void getInternalSentFolder() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    Assert.assertNotNull(
      internalMessageService.getInternalSentFolder(internalMessagesID.getSrcUserId()));
  }

  @Test
  public void markMessageAsRead() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    internalMessageService.markMessageAsRead(internalMessagesID.getId());
    Assert.assertEquals(MaiConstants.MARK_READ,
      internalMessageService.getInternalMessage(internalMessagesID.getId()).getStatus());
  }

  @Test
  public void markMessageAsReplied() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    internalMessageService.markMessageAsReplied(internalMessagesID.getId());
    Assert.assertEquals(MaiConstants.MARK_REPLIED,
      internalMessageService.getInternalMessage(internalMessagesID.getId()).getStatus());
  }

  @Test
  public void markMessageAsUnread() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    internalMessageService.markMessageAsUnread(internalMessagesID.getId());
    Assert.assertEquals(MaiConstants.MARK_UNREAD,
      internalMessageService.getInternalMessage(internalMessagesID.getId()).getStatus());
  }

  @Test
  public void deleteMessage() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    internalMessageService.deleteMessage(internalMessagesID.getId(), "SENT");
    Assert.assertEquals("S",
      internalMessageService.getInternalMessage(internalMessagesID.getId()).getDeleteType());
    internalMessageService.deleteMessage(internalMessagesID.getId(), "INBOX");
    Assert.assertNull(internalMessageService.getInternalMessage(internalMessagesID.getId()));
  }

  @Test
  public void getInternalMessage() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    Assert.assertNotNull(internalMessageService.getInternalMessage(internalMessagesID.getId()));
  }

  @Test
  public void getInternalMessageAttachments() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    Assert.assertNotNull(
      internalMessageService.getInternalMessageAttachments(internalMessagesID.getId()));
  }

  @Test
  public void getInternalAttachment() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);
    List<InternalAttachmentDTO> attachmentID = internalMessagesID.getAttachments();

    Iterator iterator = attachmentID.iterator();
    while (iterator.hasNext()) {
      InternalAttachmentDTO element = (InternalAttachmentDTO) iterator.next();
      String distrID = element.getId();
      Assert.assertNotNull(internalMessageService.getInternalAttachment(distrID));
    }
  }

  @Test
  public void getMailCount() {
    InternalMessagesDTO internalMessagesDTO = TestUtilities.createInternalMessagesDTO();
    InternalMessagesDTO internalMessagesID = internalMessageService
      .sendInternalMail(internalMessagesDTO);
    Assert.assertNotNull(internalMessagesID);

    Assert.assertNotNull(internalMessageService
      .getMailCount(internalMessagesID.getSrcUserId(), internalMessagesID.getStatus()));
    Assert.assertTrue(internalMessageService
      .getMailCount(internalMessagesID.getSrcUserId(), internalMessagesID.getStatus()) != 0);
  }

}



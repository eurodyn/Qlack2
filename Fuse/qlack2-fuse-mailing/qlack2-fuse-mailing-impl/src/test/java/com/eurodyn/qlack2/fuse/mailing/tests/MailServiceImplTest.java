package com.eurodyn.qlack2.fuse.mailing.tests;

import static com.eurodyn.qlack2.fuse.mailing.api.MailService.EMAIL_STATUS.SENT;

import com.eurodyn.qlack2.fuse.mailing.api.MailService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;
import com.eurodyn.qlack2.fuse.mailing.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.mailing.util.TestUtilities;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class MailServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  MailService mailService;

  @Test
  public void queueEmail() {
    EmailDTO emailDTO = TestUtilities.createEmailDTO();
    String emailID = mailService.queueEmail(emailDTO);
    Assert.assertNotNull(emailID);
  }

  @Test
  public void queueEmails() {
    EmailDTO emailDTO = TestUtilities.createEmailDTO();
    List<EmailDTO> emails = new ArrayList<>();
    emails.add(emailDTO);

    List<String> ids = mailService.queueEmails(emails);
    Assert.assertNotNull(ids);
  }

  @Test
  public void updateStatus() {
    EmailDTO emailDTO = TestUtilities.createEmailDTO();
    String emailID = mailService.queueEmail(emailDTO);
    Assert.assertNotNull(emailID);

    mailService.updateStatus(emailID, SENT);
    String status = mailService.getMail(emailID).getStatus();
    Assert.assertEquals(status, "SENT");
  }

  @Test
  public void deleteFromQueue() {
    EmailDTO emailDTO = TestUtilities.createEmailDTO();
    String emailID = mailService.queueEmail(emailDTO);
    Assert.assertNotNull(emailID);

    mailService.deleteFromQueue(emailID);
    //expect that the id doesnt exist
    Assert.assertNull(mailService.getMail(emailID));
  }

  @Test
  public void cleanup() {
    EmailDTO emailDTO = TestUtilities.createEmailDTO();
    emailDTO.setStatus("SENT");
    emailDTO.setDateSent(new Date().getTime());
    String emailID = mailService.queueEmail(emailDTO);
    Assert.assertNotNull(emailID);

    MailService.EMAIL_STATUS[] status = MailService.EMAIL_STATUS.values();
    mailService.cleanup(new Date().getTime(), status);

    //expect that the id doesnt exist
    Assert.assertNull(mailService.getMail(emailID));
  }

}



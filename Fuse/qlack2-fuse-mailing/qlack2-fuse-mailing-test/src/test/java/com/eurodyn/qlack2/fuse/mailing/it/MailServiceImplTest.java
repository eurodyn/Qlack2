package com.eurodyn.qlack2.fuse.mailing.it;

import com.eurodyn.qlack2.fuse.mailing.api.MailService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import static com.eurodyn.qlack2.fuse.mailing.api.MailService.EMAIL_STATUS.QUEUED;
import static com.eurodyn.qlack2.fuse.mailing.api.MailService.EMAIL_STATUS.SENT;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class MailServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    MailService mailService;

    @Test
    public void queueEmail(){
        EmailDTO emailDTO = TestUtilities.createEmailDTO();
        String emailID = mailService.queueEmail(emailDTO);
        Assert.assertNotNull(emailID);
    }

    @Test
    public void queueEmails(){
        EmailDTO emailDTO = TestUtilities.createEmailDTO();
        List<EmailDTO> emails = new ArrayList<>();
        emails.add(emailDTO);

        List<String> ids = mailService.queueEmails(emails);
        Assert.assertNotNull(ids);
    }

    @Test
    public void updateStatus(){
        EmailDTO emailDTO = TestUtilities.createEmailDTO();
        String emailID = mailService.queueEmail(emailDTO);
        Assert.assertNotNull(emailID);

        mailService.updateStatus(emailID,SENT);
        String status = mailService.getStatus(emailID);
        Assert.assertEquals(status,"SENT");
    }

    @Test
    public void deleteFromQueue(){
        EmailDTO emailDTO = TestUtilities.createEmailDTO();
        String emailID = mailService.queueEmail(emailDTO);
        Assert.assertNotNull(emailID);

        mailService.deleteFromQueue(emailID);
        Assert.assertNull(mailService.getMailId(emailID));
    }

    @Test
    public void cleanup(){
        EmailDTO emailDTO = TestUtilities.createEmailDTO();
        emailDTO.setStatus("SENT");
        emailDTO.setDateSent(new Date().getTime());
        String emailID = mailService.queueEmail(emailDTO);
        Assert.assertNotNull(emailID);

        MailService.EMAIL_STATUS[] status = MailService.EMAIL_STATUS.values();
        mailService.cleanup(new Date().getTime(),status);
        Assert.assertNull(mailService.getMailId(emailID));
    }

}



/*
 * Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
 *
 * Licensed under the EUPL, Version 1.1 only (the "License").
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package com.eurodyn.qlack2.fuse.mailing.impl.commands;

import com.eurodyn.qlack2.fuse.mailing.api.MailService;
import com.eurodyn.qlack2.fuse.mailing.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO;
import com.eurodyn.qlack2.fuse.mailing.api.dto.EmailDTO.EMAIL_TYPE;
import org.apache.commons.codec.binary.Base64;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.io.IOException;
import java.time.Instant;


@Command(scope = "qlack", name = "mail-test-email",
  description = "Sends test emails. Note that all test emails are registered in the database as "
    + "regular emails.")
@Service
public final class TestEmailCmd implements Action {

  @Argument(index = 0, name = "from", description = "The sender of the email.", required = true, multiValued = false)
  private String sender;

  @Argument(index = 1, name = "recipient", description = "The recipient of the email.", required = true, multiValued = false)
  private String recipient;

  @Argument(index = 2, name = "type", description =
    "Choose the type/format of test email to generate:\n"
      + "\t0. Plaintext\n"
      + "\t1. Plaintext UTF-8\n"
      + "\t2. Plaintext with attachments\n"
      + "\t3. Plaintext UTF-8 with attachments\n"
      + "\t4. HTML UTF-8\n"
      + "\t5. HTML UTF-8 with attachments\n"

    , required = true, multiValued = false)
  private int emailType;

  @Reference
  private MailService mailService;

  private EmailDTO typePlainText(boolean utf8) {
    EmailDTO emailDTO = new EmailDTO();
    emailDTO.setToContact(recipient);
    emailDTO.setSubject("Test email - PlainText" + (utf8 ? " - UTF8: Ελλάδα" : ""));
    emailDTO.setFrom(sender);
    emailDTO.setEmailType(EMAIL_TYPE.TEXT);
    emailDTO.setDateSent(Instant.now().toEpochMilli());
    emailDTO.setBody("Test email - PlainText" + (utf8 ? " - UTF8: Ελλάδα" : ""));

    return emailDTO;
  }

  private EmailDTO typePlainTextA(boolean utf8) throws IOException {
    EmailDTO emailDTO = typePlainText(utf8);
    Base64 base64 = new Base64();
    AttachmentDTO attachmentDTO = new AttachmentDTO();
    attachmentDTO.setContentType("image/png");
    attachmentDTO.setData(base64.decode(TestEmailSupport.javaLogo));
    attachmentDTO.setFilename("java.png");
    emailDTO.addAttachment(attachmentDTO);

    return emailDTO;
  }

  private EmailDTO typeHtml(boolean utf8) {
    EmailDTO emailDTO = new EmailDTO();
    emailDTO.setToContact(recipient);
    emailDTO.setSubject("Test email - HTML - UTF8: Ελλάδα");
    emailDTO.setFrom(sender);
    emailDTO.setEmailType(EMAIL_TYPE.HTML);
    emailDTO.setDateSent(Instant.now().toEpochMilli());
    emailDTO.setBody(TestEmailSupport.htmlBody);

    return emailDTO;
  }

  private EmailDTO typeHtmlA(boolean utf8) throws IOException {
    EmailDTO emailDTO = typeHtml(utf8);
    Base64 base64 = new Base64();
    AttachmentDTO attachmentDTO = new AttachmentDTO();
    attachmentDTO.setContentType("image/png");
    attachmentDTO.setData(base64.decode(TestEmailSupport.javaLogo));
    attachmentDTO.setFilename("java.png");
    emailDTO.addAttachment(attachmentDTO);

    return emailDTO;
  }

  @Override
  public Object execute() throws IOException {
    switch (emailType) {
      case 0:
        mailService.sendOne(mailService.queueEmail(typePlainText(false)));
        break;
      case 1:
        mailService.sendOne(mailService.queueEmail(typePlainText(true)));
        break;
      case 2:
        mailService.sendOne(mailService.queueEmail(typePlainTextA(false)));
        break;
      case 3:
        mailService.sendOne(mailService.queueEmail(typePlainTextA(true)));
        break;
      case 4:
        mailService.sendOne(mailService.queueEmail(typeHtml(true)));
        break;
      case 5:
        mailService.sendOne(mailService.queueEmail(typeHtmlA(true)));
        break;
    }

    return null;
  }

}

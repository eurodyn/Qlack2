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
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;


@Command(scope = "qlack", name = "mail-send-now",
  description = "Sends a specific email now irrespectively of its status.")
@Service
public final class SendNowCmd implements Action {

  @Argument(index = 0, name = "emailId", description = "The id of the email to send.", required = true, multiValued = false)
  private String emailId;

  @Reference
  private MailService mailService;

  @Override
  public Object execute() {
    mailService.sendOne(emailId);

    return null;
  }

}

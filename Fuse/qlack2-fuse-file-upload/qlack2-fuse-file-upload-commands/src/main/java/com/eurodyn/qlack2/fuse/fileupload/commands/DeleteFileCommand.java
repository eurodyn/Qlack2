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
package com.eurodyn.qlack2.fuse.fileupload.commands;

import java.io.IOException;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
//import com.eurodyn.qlack2.fuse.fileupload.api.request.FileDeleteRequest;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileDeleteResponse;

@Command(scope = "qlack", name = "fileupload-delete-file",
description = "Deletes all chunks for a file.")
@Service
public final class DeleteFileCommand implements Action {
	@Argument(index=0, name="fileID", description = "The ID of the file to delete.",
			required = true, multiValued = false)
	private String fileID;

	@Reference
	private FileUpload fileUploadService;

	@Override
	public Object execute() throws IOException {
		System.out.println("Executing delete-file command.");
		long starTime = System.currentTimeMillis();

		FileDeleteResponse res = fileUploadService.deleteByIDForConsole(fileID);

		System.out.println("Deleted chunks: " + res.getDeletedChunks());

		System.out.println("*** Executed in: " + (System.currentTimeMillis() - starTime) + "msec.");
		return null;
	}

}

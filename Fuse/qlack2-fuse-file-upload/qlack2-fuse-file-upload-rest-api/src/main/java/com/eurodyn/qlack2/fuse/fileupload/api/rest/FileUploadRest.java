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
package com.eurodyn.qlack2.fuse.fileupload.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.eurodyn.qlack2.fuse.fileupload.api.response.CheckChunkResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileDeleteResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.rest.rdto.FileDeleteRDTO;

@Path("/file-upload")
public interface FileUploadRest {

	@GET
	@Path("/upload")
	@Produces(MediaType.APPLICATION_JSON)
	Response checkChunk(
			@QueryParam("flowChunkNumber") long chunkNumber,
			@QueryParam("flowCurrentChunkSize") long chunkSize,
			@QueryParam("flowTotalSize") long totalSize,
			@QueryParam("flowIdentifier") String alias,
			@QueryParam("flowFilename") String filename,
			@QueryParam("flowTotalChunks") long totalChunks,
			@Context HttpHeaders headers, @Context MessageContext msgContext);

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	/**
	 * Uploads a chunk of the to be uploaded file.
	 * @param body
	 * @param headers
	 * @return
	 */
	String upload(MultipartBody body, @Context HttpHeaders headers);

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	FileDeleteResponse deleteByID(FileDeleteRDTO req,
			@Context HttpHeaders headers);

}

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
package com.eurodyn.qlack2.fuse.fileupload.impl.rest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;

import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.request.CheckChunkRequest;
import com.eurodyn.qlack2.fuse.fileupload.api.request.FileUploadRequest;
import com.eurodyn.qlack2.fuse.fileupload.api.response.CheckChunkResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileDeleteResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.rest.FileUploadRest;
import com.eurodyn.qlack2.fuse.fileupload.api.rest.rdto.FileDeleteRDTO;


@OsgiServiceProvider(classes = { FileUploadRest.class })
@Properties({
		@Property(name = "ticketHeaderName", value = "${ticket.header.name}")
})
@Singleton
public class FileUploadRestImpl implements FileUploadRest {
	private static final Logger LOGGER = Logger.getLogger(FileUploadRestImpl.class.getName());
	@OsgiService
	@Inject
	private FileUpload fileUpload;
	private String ticketHeaderName;

	public void setTicketHeaderName(String ticketHeaderName) {
		this.ticketHeaderName = ticketHeaderName;
	}

	private byte[] getBin(String fieldName, MultipartBody body)
			throws IOException {
		Attachment attachment = body.getAttachment(fieldName);
		if (attachment != null) {
			return IOUtils.readBytesFromStream(attachment.getDataHandler().getInputStream());	
		} else {
			return null;
		}
	}

	private String getString(String fieldName, MultipartBody body)
			throws IOException {
		Attachment attachment = body.getAttachment(fieldName);
		if (attachment != null) {
			return IOUtils.toString(attachment.getDataHandler()
					.getInputStream());
		} else {
			return null;
		}
	}

	private Long getLong(String fieldName, MultipartBody body)
			throws IOException {
		Attachment attachment = body.getAttachment(fieldName);
		if (attachment != null) {
			return Long.valueOf(IOUtils.toString(attachment.getDataHandler().getInputStream())); 	
		} else {
			return null;
		}
	}

	@Override
	public Response checkChunk(long chunkNumber, long chunkSize,
			long totalSize, String alias, String filename, long totalChunks,
			HttpHeaders headers, MessageContext msgContext) {
		CheckChunkRequest req = new CheckChunkRequest();
		req.setChunkNumber(chunkNumber);
		req.setFileAlias(alias);
		CheckChunkResponse res = fileUpload.checkChunk(req);
		
		if (!res.isChunkExists()) {
			return Response.status(Status.NO_CONTENT).build();
		} else {
			return Response.ok().build();
		}
	}

	@Override
	public String upload(MultipartBody body, HttpHeaders headers) {
		try {
			FileUploadRequest fur = new FileUploadRequest();
			fur.setAlias(getString("flowIdentifier", body));
			fur.setAutoDelete(true);
			if (body.getAttachment("flowChunkNumber") != null) {
				fur.setChunkNumber(getLong("flowChunkNumber", body).longValue());
			} else {
				// support for older browsers, where there is always one chunk
				fur.setChunkNumber(1);
			}
			if (body.getAttachment("flowChunkSize") != null) {
				fur.setChunkSize(getLong("flowChunkSize", body).longValue());
			}
			fur.setFilename(getString("flowFilename", body));
			if (body.getAttachment("flowTotalChunks") != null) {
				fur.setTotalChunks(getLong("flowTotalChunks", body).longValue());
			} else {
				// support for older browsers, where there is always one chunk
				fur.setTotalChunks(1);
			}
			if (body.getAttachment("flowTotalSize") != null) {
				fur.setTotalSize(getLong("flowTotalSize", body).longValue());
			}
			
			fur.setData(getBin("file", body));

			fileUpload.upload(fur);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not process file upload.", e);

		}
		return "";
	}

	@Override
	public FileDeleteResponse deleteByID(FileDeleteRDTO req, HttpHeaders headers) {
		return fileUpload.deleteByID(req.getId());
	}

}

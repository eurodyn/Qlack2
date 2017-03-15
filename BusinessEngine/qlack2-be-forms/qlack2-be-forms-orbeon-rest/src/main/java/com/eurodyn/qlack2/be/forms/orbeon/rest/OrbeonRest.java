package com.eurodyn.qlack2.be.forms.orbeon.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import com.eurodyn.qlack2.be.forms.api.OrbeonService;
import com.eurodyn.qlack2.be.forms.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.RetrieveAttachmentRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.RetrieveDocumentRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.RetrieveFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.StoreAttachmentRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.StoreDocumentRequest;
import com.eurodyn.qlack2.be.forms.orbeon.util.Utils;

@Path("/orbeon")
public class OrbeonRest {
	private static final String FORM_XHTML = "form.xhtml";

	private static final String DATA_XML = "data.xml";

	@Context
	private HttpHeaders headers;

	private OrbeonService orbeonService;

	/**
	 * Retrieve the orbeon form content to be displayed in the orbeon form
	 * builder. This method implements the HTTP GET of the orbeon persistent API
	 * for the form builder:
	 * /crud/orbeon/builder/data/[FORM_DATA_ID]/[ATTACHMENT_NAME]. When the
	 * parameter attachmentName is "data.xml" then the form definition is
	 * retrieved, otherwise the attachment with the given name.
	 *
	 * @param documentId
	 * @param attachmentName
	 * @return
	 */
	@GET
	@Path("/crud/orbeon/builder/data/{documentId}/{attachmentName}")
	public Response retrieveDocument(
			@PathParam("documentId") String documentId,
			@PathParam("attachmentName") String attachmentName) {
		Response response = null;

		if (DATA_XML.equals(attachmentName)) {
			RetrieveDocumentRequest req = new RetrieveDocumentRequest();
			req.setDocumentId(documentId);

			Utils.sign(req, headers);

			String content = orbeonService.retrieveDocument(req);

			response = Response.ok(content, MediaType.APPLICATION_XHTML_XML)
					.build();
		} else {
			RetrieveAttachmentRequest req = new RetrieveAttachmentRequest();
			req.setDocumentId(documentId);
			req.setAttachmentName(attachmentName);

			Utils.sign(req, headers);

			AttachmentDTO attachmentDTO = orbeonService.retrieveAttachment(req);
			response = Response.ok(attachmentDTO.getFileContent(),
					attachmentDTO.getContentType()).build();
		}
		return response;
	}

	/**
	 * Implements the orbeon persistent api for the HTTP PUT method. In case the
	 * app parameter is orbeon and the form is builder the method saves the form
	 * definition and attachment.
	 *
	 * @param app
	 * @param form
	 * @param documentId
	 * @param attachmentName
	 * @param httpRequest
	 * @throws IOException
	 */
	@PUT
	@Path("/crud/{app}/{form}/data/{documentId}/{attachmentName}")
	public void storeDocument(@PathParam("app") String app,
			@PathParam("form") String form,
			@PathParam("documentId") String documentId,
			@PathParam("attachmentName") String attachmentName,
			@Context final HttpServletRequest httpRequest) throws IOException {

		InputStream inputStream = httpRequest.getInputStream();

		if ("orbeon".equals(app) && "builder".equals(form)) {
			if (DATA_XML.equals(attachmentName)) {
				StringWriter writer = new StringWriter();
				IOUtils.copy(inputStream, writer, "UTF-8");
				String content = writer.toString();

				StoreDocumentRequest req = new StoreDocumentRequest();
				req.setDocumentId(documentId);
				req.setContent(content);

				Utils.sign(req, headers);

				orbeonService.storeDocument(req);
			} else {
				byte[] content = IOUtils.toByteArray(inputStream);

				StoreAttachmentRequest req = new StoreAttachmentRequest();
				req.setDocumentId(documentId);
				req.setAttachmentName(attachmentName);
				req.setContent(content);
				req.setContentType(httpRequest.getContentType());

				Utils.sign(req, headers);

				orbeonService.storeAttachment(req);
			}
		}
	}

	/**
	 * Retrieve the orbeon form content for the orbeon form runner. This method
	 * implements the HTTP GET of the orbeon persistent API for the form
	 * builder: /crud/[APPLICATION_NAME]/[FORM_NAME]/form/[ATTACHMENT_NAME].
	 * When the parameter attachmentName is "form.xhtml" then the form
	 * definition is retrieved, otherwise the attachment with the given name.
	 *
	 * @param app
	 * @param form
	 * @param attachmentName
	 * @return
	 */
	@GET
	@Path("/crud/{app}/{form}/form/{attachmentName}")
	public Response retrieveForm(@PathParam("app") String app,
			@PathParam("form") String form,
			@PathParam("attachmentName") String attachmentName) {
		Response response = null;

		if (FORM_XHTML.equals(attachmentName)) {
			RetrieveFormRequest req = new RetrieveFormRequest();
			req.setFormVersionRequestId(form);

			Utils.sign(req, headers);

			String content = orbeonService.retrieveForm(req);

			response = Response.ok(content, MediaType.APPLICATION_XHTML_XML)
					.build();
		} else {
			RetrieveAttachmentRequest req = new RetrieveAttachmentRequest();
			req.setDocumentId(form);
			req.setAttachmentName(attachmentName);

			Utils.sign(req, headers);

			AttachmentDTO attachmentDTO = orbeonService.retrieveAttachment(req);
			response = Response.ok(attachmentDTO.getFileContent(),
					attachmentDTO.getContentType()).build();
		}
		return response;
	}

	public void setOrbeonService(OrbeonService orbeonService) {
		this.orbeonService = orbeonService;
	}

}

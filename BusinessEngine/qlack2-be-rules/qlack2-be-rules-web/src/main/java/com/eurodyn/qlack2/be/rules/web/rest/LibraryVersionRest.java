package com.eurodyn.qlack2.be.rules.web.rest;

import com.eurodyn.qlack2.be.rules.api.LibraryVersionService;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteLibraryVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingLibraryResult;
import com.eurodyn.qlack2.be.rules.api.request.library.version.*;
import com.eurodyn.qlack2.be.rules.web.dto.LibraryVersionUpdateRestDTO;
import com.eurodyn.qlack2.be.rules.web.util.Utils;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.dto.DBFileDTO;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationAttribute;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrorType;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationFieldErrors;
import com.eurodyn.qlack2.util.validator.util.exception.QValidationException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/library-versions")
public class LibraryVersionRest {

	@Context
	private HttpHeaders headers;

	private LibraryVersionService libraryVersionService;
	private FileUpload fileUploadService;

	public void setLibraryVersionService(LibraryVersionService libraryVersionService) {
		this.libraryVersionService = libraryVersionService;
	}

	public void setFileUploadService(FileUpload fileUploadService) {
		this.fileUploadService = fileUploadService;
	}

	@GET
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public LibraryVersionDTO getLibraryVersion(@PathParam("versionId") String versionId) {

		GetLibraryVersionRequest req = new GetLibraryVersionRequest();
		req.setVersionId(versionId);

		return libraryVersionService.getLibraryVersion(Utils.sign(req, headers));
	}

	@PUT
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void updateLibraryVersion(@PathParam("versionId") String versionId, LibraryVersionUpdateRestDTO versionRestDto) {
		FileGetResponse fileResponse = fileUploadService.getByID(versionRestDto.getContentJAR());
		DBFileDTO fileDto = fileResponse.getFile();

		checkFileExtensionIsJar(fileDto);

		checkMaxJarFileSize(fileDto);

		UpdateLibraryVersionRequest request = new UpdateLibraryVersionRequest();
		request.setId(versionId);
		request.setContentJar(fileDto.getFileData());
		Utils.sign(request, headers);

		libraryVersionService.updateLibraryVersion(request);
	}

	private void checkFileExtensionIsJar(DBFileDTO fileDto) throws QValidationException {
		String filename = fileDto.getFilename();

		String[] tokens = filename.split("\\.(?=[^\\.]+$)");
		if (!tokens[1].equalsIgnoreCase("jar")) {
			throw new QValidationException(createFileExtensionIsNotJarError(filename));
		}
	}

	private static ValidationErrors createFileExtensionIsNotJarError(String filename) {
		ValidationErrors ve = new ValidationErrors();

		ValidationFieldErrors vfe = new ValidationFieldErrors("contentJar");
		ValidationErrorType vet = new ValidationErrorType("qlack.validation.JarFile");
		vet.putAttribute(ValidationAttribute.Message, "qlack.validation.JarFile");
		vet.putAttribute(ValidationAttribute.InvalidValue, filename);
		vfe.addError(vet);
		ve.addValidationError(vfe);

		return ve;
	}

	private void checkMaxJarFileSize(DBFileDTO fileDto) throws QValidationException {
		int maxFileSize = libraryVersionService.getMaxFileSize();
		byte[] data = fileDto.getFileData();

		if (data.length > maxFileSize) {
			throw new QValidationException(createMaxFileSizeError("contentJar"));
		}
	}

	private static ValidationErrors createMaxFileSizeError(String field) {
		ValidationErrors ve = new ValidationErrors();

		ValidationFieldErrors vfe = new ValidationFieldErrors(field);
		ValidationErrorType vet = new ValidationErrorType("qlack.validation.MaxFileSize");
		vet.putAttribute(ValidationAttribute.Message, "qlack.validation.MaxFileSize");
		vfe.addError(vet);
		ve.addValidationError(vfe);

		return ve;
	}

	@GET
	@Path("{versionId}/canDelete")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDeleteLibraryVersionResult canDeleteLibraryVersion(@PathParam("versionId") String versionId) {

		DeleteLibraryVersionRequest request = new DeleteLibraryVersionRequest();
		request.setId(versionId);

		return libraryVersionService.canDeleteLibraryVersion(Utils.sign(request, headers));
	}

	@DELETE
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteLibraryVersion(@PathParam("versionId") String versionId) {

		DeleteLibraryVersionRequest req = new DeleteLibraryVersionRequest();
		req.setId(versionId);

		libraryVersionService.deleteLibraryVersion(Utils.sign(req, headers));
	}

	@PUT
	@Path("{versionId}/lock")
	@Produces(MediaType.APPLICATION_JSON)
	public void lockLibraryVersion(@PathParam("versionId") String versionId) {

		LockLibraryVersionRequest req = new LockLibraryVersionRequest();
		req.setId(versionId);

		libraryVersionService.lockLibraryVersion(Utils.sign(req, headers));
	}

	@PUT
	@Path("{versionId}/unlock")
	@Produces(MediaType.APPLICATION_JSON)
	public void unlockLibraryVersion(@PathParam("versionId") String versionId) {

		UnlockLibraryVersionRequest req = new UnlockLibraryVersionRequest();
		req.setId(versionId);

		libraryVersionService.unlockLibraryVersion(Utils.sign(req, headers));
	}

	@GET
	@Path("{versionId}/canDisableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDisableTestingLibraryResult canDisableTestingLibraryVersion(@PathParam("versionId") String versionId) {

		EnableTestingLibraryVersionRequest request = new EnableTestingLibraryVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return libraryVersionService.canDisableTestingLibraryVersion(request);
	}

	@PUT
	@Path("{versionId}/enableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void enableTestingLibraryVersion(@PathParam("versionId") String versionId) {

		EnableTestingLibraryVersionRequest request = new EnableTestingLibraryVersionRequest();
		request.setId(versionId);
		request.setEnableTesting(true);
		Utils.sign(request, headers);

		libraryVersionService.enableTestingLibraryVersion(request);
	}

	@PUT
	@Path("{versionId}/disableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void disableTestingLibraryVersion(@PathParam("versionId") String versionId) {

		EnableTestingLibraryVersionRequest request = new EnableTestingLibraryVersionRequest();
		request.setId(versionId);
		request.setEnableTesting(false);
		Utils.sign(request, headers);

		libraryVersionService.enableTestingLibraryVersion(request);
	}

	@PUT
	@Path("{versionId}/finalize")
	@Produces(MediaType.APPLICATION_JSON)
	public void finaliseLibraryVersion(@PathParam("versionId") String versionId) {

		FinaliseLibraryVersionRequest req = new FinaliseLibraryVersionRequest();
		req.setId(versionId);
		Utils.sign(req, headers);

		libraryVersionService.finaliseLibraryVersion(req);
	}

	@GET
	@Path("{versionId}/export")
	@Produces(MediaType.APPLICATION_XML)
	public Response exportLibraryVersion(@PathParam("versionId") String versionId, @QueryParam("ticket") String ticket) {

		ExportLibraryVersionRequest request = new ExportLibraryVersionRequest();
		request.setId(versionId);
		request.setSignedTicket(SignedTicket.fromVal(ticket));

		byte[] xml = libraryVersionService.exportLibraryVersion(request);

		return Response.ok(xml)
				.header("Content-Disposition", "attachment; filename=library-version.xml")
				.build();
	}

}

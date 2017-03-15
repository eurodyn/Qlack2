package com.eurodyn.qlack2.be.rules.web.rest;

import com.eurodyn.qlack2.be.rules.api.LibraryService;
import com.eurodyn.qlack2.be.rules.api.LibraryVersionService;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteLibraryResult;
import com.eurodyn.qlack2.be.rules.api.request.library.*;
import com.eurodyn.qlack2.be.rules.api.request.library.version.CreateLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.GetLibraryVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.GetLibraryVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.ImportLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.web.dto.LibraryRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.LibraryVersionCreateRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.LibraryVersionImportRestDTO;
import com.eurodyn.qlack2.be.rules.web.util.RestConverterUtil;
import com.eurodyn.qlack2.be.rules.web.util.Utils;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.dto.DBFileDTO;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
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
import java.util.List;

@Path("/libraries")
public class LibraryRest {

	@Context
	private HttpHeaders headers;

	private LibraryService libraryService;
	private LibraryVersionService libraryVersionService;
	private FileUpload fileUploadService;

	private RestConverterUtil mapper = RestConverterUtil.INSTANCE;

	public void setLibraryService(LibraryService libraryService) {
		this.libraryService = libraryService;
	}

	public void setLibraryVersionService(LibraryVersionService libraryVersionService) {
		this.libraryVersionService = libraryVersionService;
	}

	public void setFileUploadService(FileUpload fileUploadService) {
		this.fileUploadService = fileUploadService;
	}

	@GET
	@Path("{libraryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public LibraryDTO getLibrary(@PathParam("libraryId") String libraryId) {

		GetLibraryRequest req = new GetLibraryRequest();
		req.setLibraryId(libraryId);

		return libraryService.getLibrary(Utils.sign(req, headers));
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument
	public String createLibrary(LibraryRestDTO libraryRestDto) {

		checkUniqueLibraryNameOnCreate(libraryRestDto.getProjectId(), libraryRestDto.getName());

		CreateLibraryRequest req = mapper.mapCreateLibrary(libraryRestDto);

		return libraryService.createLibrary(Utils.sign(req, headers));
	}

	@PUT
	@Path("{libraryId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void updateLibrary(@PathParam("libraryId") String libraryId, LibraryRestDTO libraryRestDto) {

		checkUniqueLibraryNameOnUpdate(libraryId, libraryRestDto.getProjectId(), libraryRestDto.getName());

		UpdateLibraryRequest req = mapper.mapUpdateLibrary(libraryRestDto);
		req.setId(libraryId);

		libraryService.updateLibrary(Utils.sign(req, headers));
	}

	@GET
	@Path("{libraryId}/canDelete")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDeleteLibraryResult canDelete(@PathParam("libraryId") String libraryId) {

		DeleteLibraryRequest request = new DeleteLibraryRequest();
		request.setId(libraryId);

		return libraryService.canDeleteLibrary(Utils.sign(request, headers));
	}

	@DELETE
	@Path("{libraryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteLibrary(@PathParam("libraryId") String libraryId) {

		DeleteLibraryRequest req = new DeleteLibraryRequest();
		req.setId(libraryId);

		libraryService.deleteLibrary(Utils.sign(req, headers));
	}

	@GET
	@Path("{libraryId}/versions/countLockedByOtherUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Long countLibraryVersionsLockedByOtherUser(@PathParam("libraryId") String libraryId) {
		CountLibraryVersionsLockedByOtherUserRequest req = new CountLibraryVersionsLockedByOtherUserRequest();
		req.setLibraryId(libraryId);

		return libraryVersionService.countLibraryVersionsLockedByOtherUser(Utils.sign(req, headers));
	}

	@GET
	@Path("{libraryId}/versions")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LibraryVersionDTO> getLibraryVersions(@PathParam("libraryId") String libraryId) {

		GetLibraryVersionsRequest request = new GetLibraryVersionsRequest();
		request.setId(libraryId);

		return libraryVersionService.getLibraryVersions(Utils.sign(request, headers));
	}

	@POST
	@Path("{libraryId}/versions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public String createLibraryVersion(@PathParam("libraryId") String libraryId, LibraryVersionCreateRestDTO versionRestDto) {

		checkUniqueLibraryVersionNameOnCreate(libraryId, versionRestDto.getName());

		FileGetResponse fileResponse = fileUploadService.getByID(versionRestDto.getContentJAR());
		DBFileDTO fileDto = fileResponse.getFile();

		checkFileExtensionIsJar(fileDto);

		checkMaxJarFileSize(fileDto);

		CreateLibraryVersionRequest req = new CreateLibraryVersionRequest();
		req.setLibraryId(libraryId);
		req.setName(versionRestDto.getName());
		req.setDescription(versionRestDto.getDescription());
		req.setContentJar(fileDto.getFileData());
		Utils.sign(req, headers);

		return libraryVersionService.createLibraryVersion(req);
	}

	@PUT
	@Path("{libraryId}/import-version")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public String importLibraryVersion(@PathParam("libraryId") String libraryId, LibraryVersionImportRestDTO versionRestDto) {
		FileGetResponse fileResponse = fileUploadService.getByID(versionRestDto.getFile());
		DBFileDTO fileDto = fileResponse.getFile();

		checkFileExtensionIsXml(fileDto);

		ImportLibraryVersionRequest request = new ImportLibraryVersionRequest();
		request.setLibraryId(libraryId);
		request.setXml(fileDto.getFileData());
		Utils.sign(request, headers);

		return libraryVersionService.importLibraryVersion(request);
	}

	// -- Helpers

	private void checkUniqueLibraryNameOnCreate(String projectId, String name) throws QValidationException {
		GetLibraryByProjectAndNameRequest req = new GetLibraryByProjectAndNameRequest();
		req.setProjectId(projectId);
		req.setName(name);
		Utils.sign(req, headers);

		LibraryDTO existingLibrary = libraryService.getLibraryByProjectAndName(req);
		if (existingLibrary == null) {
			return;
		}

		throw new QValidationException(createUniqueLibraryNameError(name));
	}

	private void checkUniqueLibraryNameOnUpdate(String id, String projectId, String name) throws QValidationException {
		GetLibraryByProjectAndNameRequest req = new GetLibraryByProjectAndNameRequest();
		req.setProjectId(projectId);
		req.setName(name);
		Utils.sign(req, headers);

		LibraryDTO existingLibrary = libraryService.getLibraryByProjectAndName(req);
		if (existingLibrary == null) {
			return;
		}

		if (id.equals(existingLibrary.getId())) {
			return;
		}

		throw new QValidationException(createUniqueLibraryNameError(name));
	}

	private void checkUniqueLibraryVersionNameOnCreate(String libraryId, String versionName) throws QValidationException {
		GetLibraryVersionIdByNameRequest request = new GetLibraryVersionIdByNameRequest();
		request.setLibraryId(libraryId);
		request.setLibraryVersionName(versionName);
		Utils.sign(request, headers);

		String existingLibraryVersionId = libraryVersionService.getLibraryVersionIdByName(request);
		if (existingLibraryVersionId != null) {
			throw new QValidationException(createUniqueLibraryVersionNameError(versionName));
		}

	}

	private static ValidationErrors createUniqueLibraryNameError(String name) {
		ValidationErrors ve = new ValidationErrors();
		ValidationFieldErrors vfe = new ValidationFieldErrors("name");
		ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
		vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
		vet.putAttribute(ValidationAttribute.InvalidValue, name);
		vfe.addError(vet);
		ve.addValidationError(vfe);
		return ve;
	}

	private static ValidationErrors createUniqueLibraryVersionNameError(String versionName) {
		ValidationErrors ve = new ValidationErrors();
		ValidationFieldErrors vfe = new ValidationFieldErrors("name");
		ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
		vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
		vet.putAttribute(ValidationAttribute.InvalidValue, versionName);
		vfe.addError(vet);
		ve.addValidationError(vfe);
		return ve;
	}

	private void checkFileExtensionIsJar(DBFileDTO fileDto) throws QValidationException {
		String filename = fileDto.getFilename();

		String[] tokens = filename.split("\\.(?=[^\\.]+$)");
		if (!tokens[1].equalsIgnoreCase("jar")) {
			throw new QValidationException(createFileExtensionIsNotJarError(filename));
		}
	}

	private void checkFileExtensionIsXml(DBFileDTO fileDto) throws QValidationException {
		String filename = fileDto.getFilename();

		String[] tokens = filename.split("\\.(?=[^\\.]+$)");
		if (!tokens[1].equalsIgnoreCase("xml")) {
			throw new QValidationException(createFileExtensionIsNotXmlError(filename));
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

	private static ValidationErrors createFileExtensionIsNotXmlError(String filename) {
		ValidationErrors ve = new ValidationErrors();

		ValidationFieldErrors vfe = new ValidationFieldErrors("file");
		ValidationErrorType vet = new ValidationErrorType("qlack.validation.XmlFile");
		vet.putAttribute(ValidationAttribute.Message, "qlack.validation.XmlFile");
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

}

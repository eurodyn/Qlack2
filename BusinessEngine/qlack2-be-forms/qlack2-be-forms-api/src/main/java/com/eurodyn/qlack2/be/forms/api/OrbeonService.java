package com.eurodyn.qlack2.be.forms.api;

import java.util.List;

import com.eurodyn.qlack2.be.forms.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.be.forms.api.dto.ContentDTO;
import com.eurodyn.qlack2.be.forms.api.exception.QFormsRuntimeException;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidFormVersionStateException;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidOperationException;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidPreconditionsException;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.GetOrbeonFormContentRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.RetrieveAttachmentRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.RetrieveDocumentRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.RetrieveFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.StoreAttachmentRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.StoreDocumentRequest;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.SubmitOrbeonFormContentRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface OrbeonService {

	String retrieveDocument(RetrieveDocumentRequest req)
			throws QInvalidTicketException, QAuthorisationException;

	void storeDocument(StoreDocumentRequest req)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	AttachmentDTO retrieveAttachment(RetrieveAttachmentRequest req)
			throws QInvalidTicketException, QAuthorisationException;

	void storeAttachment(StoreAttachmentRequest req)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	String retrieveForm(RetrieveFormRequest req)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidFormVersionStateException, QFormsRuntimeException;

	ContentDTO getOrbeonFormContent(GetOrbeonFormContentRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidFormVersionStateException, QInvalidPreconditionsException,
			QFormsRuntimeException;

	List<byte[]> submitOrbeonFormContent(SubmitOrbeonFormContentRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidFormVersionStateException, QFormsRuntimeException;
}

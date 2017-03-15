package com.eurodyn.qlack2.be.forms.api;

import com.eurodyn.qlack2.be.forms.api.dto.FormDTO;
import com.eurodyn.qlack2.be.forms.api.request.form.CreateFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.DeleteFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.GetFormIdByNameRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.GetFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.UpdateFormRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface FormsService {

	/**
	 * Retrieves a form.
	 *
	 * @param request
	 * @return
	 */
	FormDTO getForm(GetFormRequest request) throws QInvalidTicketException,
			QAuthorisationException;

	/**
	 * Retrieve a form id by its name
	 *
	 * @param request
	 * @return
	 */
	String getFormIdByName(GetFormIdByNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Creates a new form.
	 *
	 * @param request
	 * @return
	 */
	String createForm(CreateFormRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Updates an existing form. In case the request includes a form version,
	 * then it is also update along with its conditions and translations. The
	 * form version conditions are a special case where each condition should
	 * have a not null unique id. This is necessary to allow referencing among
	 * the condition - follows after (parent) relationships.
	 *
	 *
	 * @param request
	 */
	void updateForm(UpdateFormRequest request) throws QInvalidTicketException,
			QAuthorisationException;

	/**
	 * Deletes a form.
	 *
	 * @param request
	 */
	void deleteForm(DeleteFormRequest request) throws QInvalidTicketException,
			QAuthorisationException;
}

package com.eurodyn.qlack2.be.rules.api;

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.be.rules.api.exception.QRuntimeManagementException;
import com.eurodyn.qlack2.be.rules.api.request.runtime.CreateKnowledgeBaseRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.CreateKnowledgeSessionRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.DeleteFactRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.DestroyKnowledgeSessionRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.FireRulesRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.GetFactRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.GetFactsRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.GetGlobalRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.GetQueryResultsRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.InsertFactRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.InsertFactsRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.SetGlobalRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.StatelessExecuteRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.StatelessMultiExecuteRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.fuse.rules.api.StatelessExecutionResults;

public interface RulesRuntimeManagementService {
	
	String createKnowledgeBase(CreateKnowledgeBaseRequest request);

	StatelessExecutionResults statelessExecute(StatelessExecuteRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	StatelessExecutionResults statelessMultiExecute(StatelessMultiExecuteRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	String createKnowledgeSession(CreateKnowledgeSessionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	void destroyKnowledgeSession(DestroyKnowledgeSessionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	void setGlobal(SetGlobalRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	byte[] getGlobal(GetGlobalRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	String insertFact(InsertFactRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	List<String> insertFacts(InsertFactsRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	byte[] getFact(GetFactRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	List<byte[]> getFacts(GetFactsRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	void deleteFact(DeleteFactRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	void fireRules(FireRulesRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;

	List<Map<String, byte[]>> getQueryResults(GetQueryResultsRequest request)
			throws QInvalidTicketException, QAuthorisationException, QRuntimeManagementException;
}

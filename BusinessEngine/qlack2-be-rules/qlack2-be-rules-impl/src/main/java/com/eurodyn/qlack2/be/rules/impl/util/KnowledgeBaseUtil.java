package com.eurodyn.qlack2.be.rules.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuntimeBaseDTO;
import com.eurodyn.qlack2.be.rules.impl.model.LibraryVersion;
import com.eurodyn.qlack2.be.rules.impl.model.RuleVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersionKnowledgeBase;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.rules.api.RulesRuntimeService;

public class KnowledgeBaseUtil {
	private static final Logger LOGGER = Logger.getLogger(KnowledgeBaseUtil.class.getName());

	private AuditClientService audit;

	private EntityManager em;

	private DataModelsJarUtil dataModelsJarUtil;

	private RulesRuntimeService rulesRuntimeService;

	public void setAudit(AuditClientService audit) {
		this.audit = audit;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setDataModelsJarUtil(DataModelsJarUtil dataModelsJarUtil) {
		this.dataModelsJarUtil = dataModelsJarUtil;
	}

	public void setRulesRuntimeService(RulesRuntimeService rulesRuntimeService) {
		this.rulesRuntimeService = rulesRuntimeService;
	}

	public WorkingSetVersionKnowledgeBase createKnowledgeBase(SignedTicket ticket, WorkingSetVersion workingSetVersion) {
		String workingSetVersionId = workingSetVersion.getId();

		LOGGER.log(Level.FINE, "Create knowledge base for working set version {0}.", workingSetVersionId);

		List<RuleVersion> ruleVersions = RuleVersion.findByWorkingSetVersionId(em, workingSetVersionId);
		List<LibraryVersion> libraryVersions = LibraryVersion.findByWorkingSetVersionId(em, workingSetVersionId);

		List<String> rules = new ArrayList<>();
		for (RuleVersion version : ruleVersions) {
			rules.add(version.getContent());
		}

		List<byte[]> libraries = new ArrayList<>();
		for (LibraryVersion version : libraryVersions) {
			libraries.add(version.getContentJar());
		}

		byte[] models = workingSetVersion.getDataModelsJar();
		if (models == null) {
			dataModelsJarUtil.createDataModelsJar(workingSetVersion);
			models = workingSetVersion.getDataModelsJar();
			libraries.add(models);
		}

		String knowledgeBaseId = rulesRuntimeService.createKnowledgeBase(libraries, rules);

		WorkingSetVersionKnowledgeBase knowledgeBase = new WorkingSetVersionKnowledgeBase();
		knowledgeBase.setWorkingSetVersionId(workingSetVersionId);
		knowledgeBase.setWorkingSetVersion(workingSetVersion);
		knowledgeBase.setKnowledgeBaseId(knowledgeBaseId);

		workingSetVersion.setKnowledgeBase(knowledgeBase);

		em.persist(knowledgeBase);

		AuditRuntimeBaseDTO auditDto = new AuditRuntimeBaseDTO();
		auditDto.setWorkingSetVersionId(workingSetVersionId);
		auditDto.setKnowledgeBaseId(knowledgeBaseId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.RUNTIME_KBASE.toString(),
					null, ticket.getUserID(), auditDto);

		return knowledgeBase;
	}

	public void destroyKnowledgeBase(SignedTicket ticket, WorkingSetVersion workingSetVersion) {

		LOGGER.log(Level.FINE, "Destroy knowledge base for working set version {0}.", workingSetVersion.getId());

		// XXX what if the knowledge base is in use by the runtime ?
		WorkingSetVersionKnowledgeBase knowledgeBase = workingSetVersion.getKnowledgeBase();
		if (knowledgeBase == null) {
			return;
		}

		String workingSetVersionId = knowledgeBase.getWorkingSetVersionId();
		String knowledgeBaseId = knowledgeBase.getKnowledgeBaseId();

		em.remove(knowledgeBase);

		rulesRuntimeService.destroyKnowledgeBase(knowledgeBaseId);

		AuditRuntimeBaseDTO auditDto = new AuditRuntimeBaseDTO();
		auditDto.setWorkingSetVersionId(workingSetVersionId);
		auditDto.setKnowledgeBaseId(knowledgeBaseId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RUNTIME_KBASE.toString(),
					null, ticket.getUserID(), auditDto);
	}

}

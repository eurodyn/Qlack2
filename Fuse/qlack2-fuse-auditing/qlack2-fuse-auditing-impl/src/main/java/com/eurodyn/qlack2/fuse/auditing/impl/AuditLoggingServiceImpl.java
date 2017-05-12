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
package com.eurodyn.qlack2.fuse.auditing.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.auditing.api.AuditLoggingService;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SearchDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SortDTO;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SearchOperator;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SortOperator;
import com.eurodyn.qlack2.fuse.auditing.impl.model.Audit;
import com.eurodyn.qlack2.fuse.auditing.impl.model.AuditLevel;
import com.eurodyn.qlack2.fuse.auditing.impl.util.ConverterUtil;

@Transactional
public class AuditLoggingServiceImpl implements
		AuditLoggingService {
	private static final Logger LOGGER = Logger
			.getLogger(AuditLoggingServiceImpl.class.getSimpleName());
	@PersistenceContext(unitName = "fuse-audit")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param audit
	 *            {@inheritDoc}
	 *             {@inheritDoc}
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String logAudit(AuditLogDTO audit) {
		LOGGER.log(Level.FINER, "Adding audit ''{0}''.", audit);
		if (audit.getCreatedOn() == null) {
			audit.setCreatedOn(new Date());
		}
		Audit alAudit = ConverterUtil.convertToAuditLogModel(audit);
		alAudit.setLevelId(AuditLevel.findByName(em, audit.getLevel()));
		if (null != alAudit.getTraceId()) {
			em.persist(alAudit.getTraceId());
		}
		em.persist(alAudit);
		return alAudit.getId();
	}

	private <T> CriteriaQuery<T> addPredicate(CriteriaQuery<T> query,
			CriteriaBuilder cb, Predicate pr) {

		CriteriaQuery<T> cq = query;
		if (cq.getRestriction() != null) {
			cq = cq.where(cb.and(cq.getRestriction(), pr));
		} else {
			cq = cq.where(pr);
		}
		return cq;
	}

	private <T> CriteriaQuery<T> applySearchCriteria(CriteriaBuilder cb,
			CriteriaQuery<T> query, Root<Audit> root,
			List<String> referenceIds, List<String> levelNames,
			List<String> groupNames, Date startDate, Date endDate) {
		CriteriaQuery<T> cq = query;

		if (referenceIds != null) {
			Predicate pr = root.get("referenceId").in(referenceIds);
			cq = addPredicate(cq, cb, pr);
		}
		if (levelNames != null) {
			Predicate pr = root.get("levelId").get("name").in(levelNames);
			cq = addPredicate(cq, cb, pr);
		}
		if (groupNames != null) {
			Predicate pr = root.get("groupName").in(groupNames);
			cq = addPredicate(cq, cb, pr);
		}

    if (startDate != null) {
      Expression expression = root.get("createdOn");
      cq.where(cb.greaterThanOrEqualTo(expression,startDate.getTime()));
    }
    if (endDate != null) {
      Expression expression = root.get("createdOn");
      if (startDate == null) {
        cq.where(cb.lessThanOrEqualTo(expression, endDate.getTime()));
      } else {
        cq.where(cb.and(cq.getRestriction(), cb.lessThanOrEqualTo(expression, endDate.getTime())));
      }
    }

		return cq;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param id
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteAudit(String id) {
		LOGGER.log(Level.FINER, "Deleting audit ''{0}''.", id);
		em.remove(em.find(Audit.class, id));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void truncateAudits() {
		LOGGER.log(Level.FINER, "Clearing all audit log data.");
		Query query = em.createQuery("DELETE FROM Audit a");
		query.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param createdOn
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void truncateAudits(Date createdOn) {
		LOGGER.log(Level.FINER, "Clearing audit log data before {0}",
				createdOn.toString());
		Query query = em
				.createQuery("DELETE FROM Audit a WHERE a.createdOn < :createdOn");
		query.setParameter("createdOn", createdOn.getTime());
		query.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param retentionPeriod
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void truncateAudits(long retentionPeriod) {
		LOGGER.log(Level.FINER, "Clearing audit log data older than {0}",
				String.valueOf(retentionPeriod));
		Query query = em
				.createQuery("DELETE FROM Audit a WHERE a.createdOn < :createdOn");
		query.setParameter("createdOn", Calendar.getInstance()
				.getTimeInMillis() - retentionPeriod);
		query.executeUpdate();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param levelNames
	 *            {@inheritDoc}
	 * @param referenceIds
	 *            {@inheritDoc}
	 * @param groupNames
	 *            {@inheritDoc}
	 * @param startDate
	 *            {@inheritDoc}
	 * @param endDate
	 *            {@inheritDoc}
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public int countAudits(List<String> levelNames, List<String> referenceIds,
			List<String> groupNames, Date startDate, Date endDate) {
		LOGGER.log(
				Level.FINER,
				"Counting audits, levelNames count = {0}, referenceId count = {1}, groupNames count {2}, "
						+ "startDate = {3} and endDate = {4}",
				new String[] {
						(levelNames != null) ? String.valueOf(levelNames.size())
								: "0",
						(referenceIds != null) ? String.valueOf(referenceIds
								.size()) : "0",
						(groupNames != null) ? String.valueOf(groupNames.size())
								: "0",
						(startDate != null) ? startDate.toString() : "NONE",
						(endDate != null) ? endDate.toString() : "NONE" });

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Audit> root = cq.from(Audit.class);
		cq = cq.select(cb.count(root));

		cq = applySearchCriteria(cb, cq, root, referenceIds, levelNames,
				groupNames, startDate, endDate);

		TypedQuery<Long> query = em.createQuery(cq);
		return query.getSingleResult().intValue();
	}
	
	@Override
	@Transactional(TxType.REQUIRED)
	public AuditLogDTO getAuditById(String auditId){
		Audit log = em.find(Audit.class, auditId);
		
		return ConverterUtil.convertToAuditLogDTO(log);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param levelNames
	 *            {@inheritDoc}
	 * @param referenceIds
	 *            {@inheritDoc}
	 * @param groupNames
	 *            {@inheritDoc}
	 * @param startDate
	 *            {@inheritDoc}
	 * @param endDate
	 *            {@inheritDoc}
	 * @param isAscending
	 *            {@inheritDoc}
	 * @param pagingParams
	 *            {@inheritDoc}
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<AuditLogDTO> listAudits(List<String> levelNames,
			List<String> referenceIds, List<String> groupNames, Date startDate,
			Date endDate, boolean isAscending, PagingParams pagingParams) {
		LOGGER.log(
				Level.FINER,
				"Listing audits audits, levelNames count = {0}, referenceId count = {1}, groupNames count {2}, "
						+ "startDate = {3} and endDate = {4}",
				new String[] {
						(levelNames != null) ? String.valueOf(levelNames.size())
								: "0",
						(referenceIds != null) ? String.valueOf(referenceIds
								.size()) : "0",
						(groupNames != null) ? String.valueOf(groupNames.size())
								: "0",
						(startDate != null) ? startDate.toString() : "NONE",
						(endDate != null) ? endDate.toString() : "NONE" });

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Audit> cq = cb.createQuery(Audit.class);
		Root<Audit> root = cq.from(Audit.class);

		cq = applySearchCriteria(cb, cq, root, referenceIds, levelNames,
				groupNames, startDate, endDate);

		if (isAscending) {
			cq.orderBy(cb.asc(root.get("createdOn")));
		} else {
			cq.orderBy(cb.desc(root.get("createdOn")));
		}

		TypedQuery<Audit> query = em.createQuery(cq);
		if (pagingParams != null && pagingParams.getCurrentPage() > -1) {
			query.setFirstResult((pagingParams.getCurrentPage() - 1)
					* pagingParams.getPageSize());
			query.setMaxResults(pagingParams.getPageSize());
		}

		return ConverterUtil.convertToAuditLogList(query.getResultList());
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<AuditLogDTO> listAuditLogs(List<SearchDTO> searchList,
			Date startDate, Date endDate, List<SortDTO> sortList,
			PagingParams pagingParams) {
		LOGGER.log(
				Level.FINER,
				"listAuditLogs, searchList count = {0}, sortList count = {1}, startDate = {2} and endDate = {3}",
				new String[] {
						(searchList != null) ? String.valueOf(searchList.size())
								: "0",
						(sortList != null) ? String.valueOf(sortList.size())
								: "0",
						(startDate != null) ? startDate.toString() : "NONE",
						(endDate != null) ? endDate.toString() : "NONE" });

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Audit> cq = cb.createQuery(Audit.class);
		Root<Audit> root = cq.from(Audit.class);

		cq = applySearchCriteria(cb, cq, root, searchList, startDate, endDate);

		cq = applySortCriteria(cb, cq, root, sortList);

		TypedQuery<Audit> query = em.createQuery(cq);
		if (pagingParams != null && pagingParams.getCurrentPage() > -1) {
			query.setFirstResult((pagingParams.getCurrentPage() - 1)
					* pagingParams.getPageSize());
			query.setMaxResults(pagingParams.getPageSize());
		}

		return ConverterUtil.convertToAuditLogList(query.getResultList());
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public int countAuditLogs(List<SearchDTO> searchList, Date startDate,
			Date endDate) {
		LOGGER.log(
				Level.FINER,
				"countAuditLogs, searchList count = {0}, startDate = {1} and endDate = {2}",
				new String[] {
						(searchList != null) ? String.valueOf(searchList.size())
								: "0",
						(startDate != null) ? startDate.toString() : "NONE",
						(endDate != null) ? endDate.toString() : "NONE" });

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Audit> root = cq.from(Audit.class);
		cq = cq.select(cb.count(root));
		cq = applySearchCriteria(cb, cq, root, searchList, startDate, endDate);

		TypedQuery<Long> query = em.createQuery(cq);

		return query.getSingleResult().intValue();
	}

	//TODO this will never scale, especially in an Audit table.
	private <T> CriteriaQuery<T> applySearchCriteria(CriteriaBuilder cb,
			CriteriaQuery<T> query, Root<Audit> root,
			List<SearchDTO> searchList, Date startDate, Date endDate) {
		CriteriaQuery<T> cq = query;

		if (searchList != null) {
			for (SearchDTO searchDTO : searchList) {
				if (SearchOperator.EQUAL == searchDTO.getOperator()) {
					if ("levelId".equals(searchDTO.getColumn().name())) {
						Predicate pr = root.get("levelId").get("name").in(searchDTO.getValue());
						cq = addPredicate(cq, cb, pr);
					} else {
						Predicate pr = root.get(searchDTO.getColumn().name()).in(
								searchDTO.getValue());
						cq = addPredicate(cq, cb, pr);
					}
				} else if (SearchOperator.LIKE == searchDTO.getOperator()) {
					if ("levelId".equals(searchDTO.getColumn().name())) {
						Expression expression = root.get(searchDTO.getColumn()
								.name()).get("name");
						Predicate pr = cb.like(expression, "%"
								+ searchDTO.getValue().get(0) + "%");

						cq = addPredicate(cq, cb, pr);
					} else {
						Expression expression = root.get(searchDTO.getColumn()
								.name());
						Predicate pr = cb.like(expression, "%"
								+ searchDTO.getValue().get(0) + "%");

						cq = addPredicate(cq, cb, pr);
					}
				}
			}
		}

		if (startDate != null) {
			Expression expression = root.get("createdOn");
			Predicate pr = cb.greaterThanOrEqualTo(expression,
					startDate.getTime());

			cq = addPredicate(cq, cb, pr);

		}
		if (endDate != null) {
			Expression expression = root.get("createdOn");
			Predicate pr = cb.lessThanOrEqualTo(expression, endDate.getTime());

			cq = addPredicate(cq, cb, pr);
		}

		return cq;
	}

	private <T> CriteriaQuery<T> applySortCriteria(CriteriaBuilder cb,
			CriteriaQuery<T> query, Root<Audit> root, List<SortDTO> sortList) {
		CriteriaQuery<T> cq = query;
		List<Order> orders = new ArrayList<>();

		if (sortList != null) {
			for (SortDTO sortDTO : sortList) {
				if (SortOperator.ASC == sortDTO.getOperator()) {
					orders.add(cb.asc(root.get(sortDTO.getColumn().name())));
				} else {
					orders.add(cb.desc(root.get(sortDTO.getColumn().name())));
				}
			}
			cq = cq.orderBy(orders);
		}

		return cq;
	}

}
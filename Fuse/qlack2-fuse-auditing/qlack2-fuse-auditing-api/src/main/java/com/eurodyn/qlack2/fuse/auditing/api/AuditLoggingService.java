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
package com.eurodyn.qlack2.fuse.auditing.api;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SearchDTO;
import com.eurodyn.qlack2.fuse.auditing.api.dto.SortDTO;

import java.util.Date;
import java.util.List;

/**
 * To manage audit logging functionality.
 */
public interface AuditLoggingService {

  /**
   * To log audit. Throws QlackFuseALException in case of unexpected
   * condition.
   */
  String logAudit(AuditLogDTO audit);

  /**
   * Logs a list of audits and correlate them if an correlationId is passed.
   *
   * @param auditList the audits
   * @param correlationId the correlation id
   * @return the uuid's of the created audit entries
   */
  List<String> logAudits(List<AuditLogDTO> auditList, String correlationId);

  /**
   * To delete specific audit log.
   */
  void deleteAudit(String auditLogId);

  /**
   * Delete all audit logs.
   */
  void truncateAudits();

  /**
   * Delete all audit logs created before a specific date
   *
   * @param createdOn The date before which to clear the audit logs (exclusive).
   */
  void truncateAudits(Date createdOn);

  /**
   * Delete all audit logs older than a specific period of time. The logs
   * which will be deleted are those whose creation date is less than (now -
   * retentionPeriod).
   *
   * @param retentionPeriod The period for which to retain audit logs, in milliseconds
   */
  void truncateAudits(long retentionPeriod);

  /**
   * To find an audit log by id.
   */
  AuditLogDTO getAuditById(String auditId);

  /**
   * Returns a list with AuditLogDTO for the given constraints.
   */
  List<AuditLogDTO> listAudits(List<String> levelNames,
    List<String> referenceIds, List<String> groupNames, Date startDate,
    Date endDate, boolean isAscending, PagingParams pagingParams);

  /**
   * Returns the number of entities that the given query will return. Used for
   * pagination.
   */
  int countAudits(List<String> levelNames, List<String> referenceIds,
    List<String> groupNames, Date startDate, Date endDate);

  /**
   * Returns a list of AuditLogDTO's taking account the given search, sort and
   * paging criteria.
   *
   * @param searchList list with search criteria
   * @param startDate from date to search
   * @param endDate to date to search
   * @param sortList list with sort criteria
   * @param pagingParams pagination parameters
   * @return list of AuditLogDTO's
   */
  List<AuditLogDTO> listAuditLogs(List<SearchDTO> searchList,
    Date startDate, Date endDate, List<SortDTO> sortList,
    PagingParams pagingParams, boolean fetchTraceData);

  /**
   * Convenience method for the {@link #listAuditLogs(List, Date, Date, List, PagingParams,
   * boolean)}, with {@code fetchTraceData} set to true.
   */
  List<AuditLogDTO> listAuditLogs(List<SearchDTO> searchList,
    Date startDate, Date endDate, List<SortDTO> sortList,
    PagingParams pagingParams);

  /**
   * Returns the number of entities that the given query will return. Used for
   * pagination.
   */
  int countAuditLogs(List<SearchDTO> searchList, Date startDate,
    Date endDate);

  /**
   * Returns the number of rows of entities for the spedific column
   * that the given query will return.
   * Used for pagination.
   */
  List<String> getAuditLogsColumn(List<SearchDTO> searchList, Date startDate,
    Date endDate, String column);

	/**
	 * Flushes and then clears the first level entity cache. Useful for memory management inside large transactions.
	 */
	void flushAndClearCache();

}

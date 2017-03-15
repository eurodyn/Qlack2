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
package com.eurodyn.qlack2.fuse.lexicon.api.criteria;

import com.eurodyn.qlack2.common.util.search.PagingParams;

public class KeySearchCriteria {
	public enum SortType {
		ASCENDING,
		DESCENDING
	}

	private String groupId;
	private String keyName;
    private boolean ascending = true;
    private PagingParams paging;

	public static class KeySearchCriteriaBuilder {
		private KeySearchCriteria criteria;
		private PagingParams paging;

		private KeySearchCriteriaBuilder() {
			criteria = new KeySearchCriteria();
		}

		public static KeySearchCriteriaBuilder createCriteria() {
			return new KeySearchCriteriaBuilder();
		}

		public KeySearchCriteria build() {
			criteria.setPaging(paging);
			return criteria;
		}

		public KeySearchCriteriaBuilder withNameLike(String name) {
			criteria.setKeyName(name);
			return this;
		}

		public KeySearchCriteriaBuilder inGroup(String groupId) {
			criteria.setGroupId(groupId);
			return this;
		}

		public KeySearchCriteriaBuilder withPageSize(int pageSize) {
			if (paging == null) {
				paging = new PagingParams();
			}
			paging.setPageSize(pageSize);
			return this;
		}

		public KeySearchCriteriaBuilder getPage(int page) {
			if (paging == null) {
				paging = new PagingParams();
			}
			paging.setCurrentPage(page);
			return this;
		}
	}

	public String getGroupId() {
		return groupId;
	}

	private void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getKeyName() {
		return keyName;
	}

	private void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public boolean isAscending() {
		return ascending;
	}

	private void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public PagingParams getPaging() {
		return paging;
	}

	private void setPaging(PagingParams paging) {
		this.paging = paging;
	}
}

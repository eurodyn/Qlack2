package com.eurodyn.qlack2.be.explorer.api.criteria;

import com.eurodyn.qlack2.common.util.search.PagingParams;

public class ProjectListCriteria {
	public enum SortColumn {
		NAME,
        CREATED_ON
    }
	public enum SortType {
		ASCENDING,
		DESCENDING
	}

	private String sortColumn;
	private SortType sortType;
	private boolean ascending;
	private PagingParams paging;
	private Boolean active;
	private Boolean rules;
	private Boolean workflows;
	private Boolean forms;

	public static class ProjectListCriteriaBuilder {
		private ProjectListCriteria criteria;
		private PagingParams paging;

		private ProjectListCriteriaBuilder() {
			criteria = new ProjectListCriteria();
		}

		public static ProjectListCriteriaBuilder createCriteria() {
			return new ProjectListCriteriaBuilder();
		}

		public ProjectListCriteria build() {
			// Default sorting if none was specified
			if (criteria.getSortColumn() == null) {
				criteria.setSortColumn("name");
			}
			criteria.setPaging(paging);
			return criteria;
		}

		public ProjectListCriteriaBuilder withActive(boolean active) {
			criteria.setActive(active);
			return this;
		}

		public ProjectListCriteriaBuilder withRules(boolean rules) {
			criteria.setRules(rules);
			return this;
		}

		public ProjectListCriteriaBuilder withWorkflows(boolean workflows) {
			criteria.setWorkflows(workflows);
			return this;
		}

		public ProjectListCriteriaBuilder withForms(boolean forms) {
			criteria.setForms(forms);
			return this;
		}

		public ProjectListCriteriaBuilder sortByColumn(SortColumn column, SortType type) {
			switch(column) {
			case NAME:
				criteria.setSortColumn("name");
				break;
			case CREATED_ON:
				criteria.setSortColumn("createdOn");
				break;
			}
			switch(type) {
			case ASCENDING:
				criteria.setAscending(true);
				break;
			case DESCENDING:
				criteria.setAscending(false);
				break;
			}
			return this;
		}

		public ProjectListCriteriaBuilder withPageSize(int pageSize) {
			if (paging == null) {
				paging = new PagingParams();
			}
			paging.setPageSize(pageSize);
			return this;
		}

		public ProjectListCriteriaBuilder getPage(int page) {
			if (paging == null) {
				paging = new PagingParams();
			}
			paging.setCurrentPage(page);
			return this;
		}
	}

	private ProjectListCriteria() {}

	public String getSortColumn() {
		return sortColumn;
	}

	private void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	public SortType getSortType() {
		return sortType;
	}

	@SuppressWarnings("unused")
	private void setSortType(SortType sortType) {
		this.sortType = sortType;
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

	public Boolean getActive() {
		return active;
	}

	private void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getRules() {
		return rules;
	}

	private void setRules(Boolean rules) {
		this.rules = rules;
	}

	public Boolean getWorkflows() {
		return workflows;
	}

	private void setWorkflows(Boolean workflows) {
		this.workflows = workflows;
	}

	public Boolean getForms() {
		return forms;
	}

	private void setForms(Boolean forms) {
		this.forms = forms;
	}


}

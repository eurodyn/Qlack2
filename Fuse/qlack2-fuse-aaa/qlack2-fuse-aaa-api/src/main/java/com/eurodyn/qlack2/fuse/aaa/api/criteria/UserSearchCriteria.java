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
package com.eurodyn.qlack2.fuse.aaa.api.criteria;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria.UserAttributeCriteria.Type;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;

import java.util.Arrays;
import java.util.Collection;

public class UserSearchCriteria {
	public enum SortColumn {
        USERNAME,
        STATUS,
        ATTRIBUTE
    }
	public enum SortType {
		ASCENDING,
		DESCENDING
	}

	private Collection<String> includeIds;
	private Collection<String> excludeIds;
	private Collection<String> includeGroupIds;
	private Collection<String> excludeGroupIds;
	private Collection<Byte> includeStatuses;
	private Collection<Byte> excludeStatuses;
	private String username;
	private UserAttributeCriteria attributeCriteria;
	private Boolean superadmin;
    private String sortColumn;
    private String sortAttribute;
    private boolean ascending = true;
    private PagingParams paging;

	public static class UserSearchCriteriaBuilder {
		private UserSearchCriteria criteria;
		private PagingParams paging;

		private UserSearchCriteriaBuilder() {
			criteria = new UserSearchCriteria();
		}

		public static UserSearchCriteriaBuilder createCriteria() {
			return new UserSearchCriteriaBuilder();
		}

		public UserSearchCriteria build() {
			// Default sorting if none was specified
			if ((criteria.getSortColumn() == null) && (criteria.getSortAttribute() == null)) {
				criteria.setSortColumn("username");
			}
			criteria.setPaging(paging);
			return criteria;
		}

		/**
		 * Specify a collection of IDs in which the IDs of the retrieved users should be contained.
		 * @param ids
		 * @return
		 */
		public UserSearchCriteriaBuilder withIdIn(Collection<String> ids) {
			criteria.setIncludeIds(ids);
			return this;
		}

		/**
		 * Specify a collection of IDs in which the IDs of the retrieved users should not be contained.
		 * @param ids
		 * @return
		 */
		public UserSearchCriteriaBuilder withIdNotIn(Collection<String> ids) {
			criteria.setExcludeIds(ids);
			return this;
		}

		/**
		 * Specify a collection of IDs in which the IDs of the retrieved users groups should be contained.
		 * @param ids
		 * @return
		 */
		public UserSearchCriteriaBuilder withGroupIdIn(Collection<String> ids) {
			criteria.setIncludeGroupIds(ids);
			return this;
		}

		/**
		 * Specify a collection of IDs in which the IDs of the retrieved users groups should not be contained.
		 * @param ids
		 * @return
		 */
		public UserSearchCriteriaBuilder withGroupIdNotIn(Collection<String> ids) {
			criteria.setExcludeGroupIds(ids);
			return this;
		}

		/**
		 * Specify a collection of statuses in which the status of the retrieved users should be contained.
		 * @param statuses The list of statuses.
		 * @return
		 */
		public UserSearchCriteriaBuilder withStatusIn(Collection<Byte> statuses) {
			criteria.setIncludeStatuses(statuses);
			return this;
		}

		/**
		 * Specify a collection of statuses in which the status of the retrieved users should not be contained.
		 * @param statuses The list of statuses.
		 * @return
		 */
		public UserSearchCriteriaBuilder withStatusNotIn(Collection<Byte> statuses) {
			criteria.setExcludeStatuses(statuses);
			return this;
		}

		/**
		 * Spacify a username for which to check
		 * @param username
		 * @return
		 */
		public UserSearchCriteriaBuilder withUsernameLike(String username) {
			criteria.setUsername(username);
			return this;
		}

		/**
		 * Specify the attributes the retrieved users should have. This method is intended to be
		 * called in conjuction with the and and or static methods which are used to specify
		 * the relationship between the different criteria, for example:
		 * UserSearchCriteriaBuilder.createCriteria().withAttributes(and(and(att1, att2, att3), or (att4, att5), or(att6, att7)))
		 * @param attCriteria
		 * @return
		 */
		public UserSearchCriteriaBuilder withAttributes(UserAttributeCriteria attCriteria) {
			criteria.setAttributeCriteria(attCriteria);
			return this;
		}

		public UserSearchCriteriaBuilder withSuperadmin(boolean superadmin) {
			criteria.setSuperadmin(superadmin);
			return this;
		}

		public static UserAttributeCriteria and(UserAttributeDTO... attributes) {
			UserAttributeCriteria retVal = new UserAttributeCriteria();
			retVal.setAttributes(attributes);
			retVal.setType(Type.AND);
			return retVal;
		}

		public static UserAttributeCriteria and(Collection<UserAttributeDTO> attributes) {
			UserAttributeCriteria retVal = new UserAttributeCriteria();
			retVal.setAttributes(attributes);
			retVal.setType(Type.AND);
			return retVal;
		}

		public UserAttributeCriteria and(UserAttributeCriteria... attCriteria) {
			UserAttributeCriteria retVal = new UserAttributeCriteria();
			retVal.setAttCriteria(attCriteria);
			retVal.setType(Type.AND);
			return retVal;
		}

		public static UserAttributeCriteria or(UserAttributeDTO... attributes) {
			UserAttributeCriteria retVal = new UserAttributeCriteria();
			retVal.setAttributes(attributes);
			retVal.setType(Type.OR);
			return retVal;
		}

		public static UserAttributeCriteria or(Collection<UserAttributeDTO> attributes) {
			UserAttributeCriteria retVal = new UserAttributeCriteria();
			retVal.setAttributes(attributes);
			retVal.setType(Type.OR);
			return retVal;
		}

		public UserAttributeCriteria or(UserAttributeCriteria... attCriteria) {
			UserAttributeCriteria retVal = new UserAttributeCriteria();
			retVal.setAttCriteria(attCriteria);
			retVal.setType(Type.OR);
			return retVal;
		}

		public UserSearchCriteriaBuilder sortByColumn(SortColumn column, SortType type) {
			switch(column) {
			case USERNAME:
				criteria.setSortColumn("username");
				break;
			case STATUS:
				criteria.setSortColumn("status");
				break;
			case ATTRIBUTE:
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

		public UserSearchCriteriaBuilder sortByAttribute(String attributeName, SortType type) {
			criteria.setSortAttribute(attributeName);
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

		public UserSearchCriteriaBuilder withPageSize(int pageSize) {
			if (paging == null) {
				paging = new PagingParams();
			}
			paging.setPageSize(pageSize);
			return this;
		}

		public UserSearchCriteriaBuilder getPage(int page) {
			if (paging == null) {
				paging = new PagingParams();
			}
			paging.setCurrentPage(page);
			return this;
		}
	}

	public static class UserAttributeCriteria {
		public enum Type {
			AND, OR
		}
		private Type type;
		private Collection<UserAttributeDTO> attributes;
		private Collection<UserAttributeCriteria> attCriteria;
		private boolean useLike;

	

    private UserAttributeCriteria(){}

		public Type getType() {
			return type;
		}

		private void setType(Type type) {
			this.type = type;
		}

		public Collection<UserAttributeDTO> getAttributes() {
			return attributes;
		}

		private void setAttributes(Collection<UserAttributeDTO> attributes) {
			this.attributes = attributes;
		}

		private void setAttributes(UserAttributeDTO[] attributes) {
			this.attributes = Arrays.asList(attributes);
		}

		public Collection<UserAttributeCriteria> getAttCriteria() {
			return attCriteria;
		}

		private void setAttCriteria(UserAttributeCriteria[] attCriteria) {
			this.attCriteria = Arrays.asList(attCriteria);
		}
		
		public boolean isUseLike() {
		    return useLike;
		}

		public void setUseLike(boolean useLike) {
		   this.useLike = useLike;
		}
	}

	private UserSearchCriteria(){}

	public Collection<String> getIncludeIds() {
		return includeIds;
	}

	private void setIncludeIds(Collection<String> includeIds) {
		this.includeIds = includeIds;
	}

	public Collection<String> getExcludeIds() {
		return excludeIds;
	}

	private void setExcludeIds(Collection<String> excludeIds) {
		this.excludeIds = excludeIds;
	}

	public Collection<String> getIncludeGroupIds() {
		return includeGroupIds;
	}

	private void setIncludeGroupIds(Collection<String> includeGroupIds) {
		this.includeGroupIds = includeGroupIds;
	}

	public Collection<String> getExcludeGroupIds() {
		return excludeGroupIds;
	}

	private void setExcludeGroupIds(Collection<String> excludeGroupIds) {
		this.excludeGroupIds = excludeGroupIds;
	}

	public Collection<Byte> getIncludeStatuses() {
		return includeStatuses;
	}

	private void setIncludeStatuses(Collection<Byte> includeStatuses) {
		this.includeStatuses = includeStatuses;
	}

	public Collection<Byte> getExcludeStatuses() {
		return excludeStatuses;
	}

	private void setExcludeStatuses(Collection<Byte> excludeStatuses) {
		this.excludeStatuses = excludeStatuses;
	}

	public String getUsername() {
		return username;
	}

	private void setUsername(String username) {
		this.username = username;
	}

	public UserAttributeCriteria getAttributeCriteria() {
		return attributeCriteria;
	}

	private void setAttributeCriteria(UserAttributeCriteria attributeCriteria) {
		this.attributeCriteria = attributeCriteria;
	}

	public Boolean getSuperadmin() {
		return superadmin;
	}

	private void setSuperadmin(Boolean superadmin) {
		this.superadmin = superadmin;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	private void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	public String getSortAttribute() {
		return sortAttribute;
	}

	private void setSortAttribute(String sortAttribute) {
		this.sortAttribute = sortAttribute;
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

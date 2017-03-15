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
package com.eurodyn.qlack2.fuse.imaging.api.criteria;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.imaging.api.criteria.ImageSearchCriteria.ImageAttributeCriteria.Type;
import com.eurodyn.qlack2.fuse.imaging.api.dto.ImageAttributeDTO;

import java.util.Arrays;
import java.util.Collection;

public class ImageSearchCriteria {
	public enum SortColumn {
        NAME,
        FILENAME,
        ATTRIBUTE
    }
	public enum SortType {
		ASCENDING,
		DESCENDING
	}

	private Collection<String> includeFolderIds;
	private Collection<String> excludeFolderIds;
	private String name;
	private String filename;
	private ImageAttributeCriteria attributeCriteria;
    private String sortColumn;
    private String sortAttribute;
    private boolean ascending = true;
    private PagingParams paging;

	public static class ImageSearchCriteriaBuilder {
		private ImageSearchCriteria criteria;
		private PagingParams paging;

		private ImageSearchCriteriaBuilder() {
			criteria = new ImageSearchCriteria();
		}

		public static ImageSearchCriteriaBuilder createCriteria() {
			return new ImageSearchCriteriaBuilder();
		}

		public ImageSearchCriteria build() {
			// Default sorting if none was specified
			if ((criteria.getSortColumn() == null) && (criteria.getSortAttribute() == null)) {
				criteria.setSortColumn("name");
			}
			criteria.setPaging(paging);
			return criteria;
		}

		/**
		 * Specify a collection of IDs in which the IDs of the retrieved image folders should be contained.
		 * @param ids
		 * @return
		 */
		public ImageSearchCriteriaBuilder withFolderIdIn(Collection<String> ids) {
			criteria.setIncludeFolderIds(ids);
			return this;
		}

		/**
		 * Specify a collection of IDs in which the IDs of the retrieved image folders should not be contained.
		 * @param ids
		 * @return
		 */
		public ImageSearchCriteriaBuilder withFolderIdNotIn(Collection<String> ids) {
			criteria.setExcludeFolderIds(ids);
			return this;
		}

		/**
		 * Specify a name for which to check
		 * @param name
		 * @return
		 */
		public ImageSearchCriteriaBuilder withNameLike(String name) {
			criteria.setName(name);
			return this;
		}
		
		/**
		 * Specify a filename for which to check
		 * @param filename
		 * @return
		 */
		public ImageSearchCriteriaBuilder withFilenameLike(String filename) {
			criteria.setFilename(filename);
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
		public ImageSearchCriteriaBuilder withAttributes(ImageAttributeCriteria attCriteria) {
			criteria.setAttributeCriteria(attCriteria);
			return this;
		}

		public static ImageAttributeCriteria and(ImageAttributeDTO... attributes) {
			ImageAttributeCriteria retVal = new ImageAttributeCriteria();
			retVal.setAttributes(attributes);
			retVal.setType(Type.AND);
			return retVal;
		}

		public static ImageAttributeCriteria and(Collection<ImageAttributeDTO> attributes) {
			ImageAttributeCriteria retVal = new ImageAttributeCriteria();
			retVal.setAttributes(attributes);
			retVal.setType(Type.AND);
			return retVal;
		}

		public ImageAttributeCriteria and(ImageAttributeCriteria... attCriteria) {
			ImageAttributeCriteria retVal = new ImageAttributeCriteria();
			retVal.setAttCriteria(attCriteria);
			retVal.setType(Type.AND);
			return retVal;
		}

		public static ImageAttributeCriteria or(ImageAttributeDTO... attributes) {
			ImageAttributeCriteria retVal = new ImageAttributeCriteria();
			retVal.setAttributes(attributes);
			retVal.setType(Type.OR);
			return retVal;
		}

		public static ImageAttributeCriteria or(Collection<ImageAttributeDTO> attributes) {
			ImageAttributeCriteria retVal = new ImageAttributeCriteria();
			retVal.setAttributes(attributes);
			retVal.setType(Type.OR);
			return retVal;
		}

		public ImageAttributeCriteria or(ImageAttributeCriteria... attCriteria) {
			ImageAttributeCriteria retVal = new ImageAttributeCriteria();
			retVal.setAttCriteria(attCriteria);
			retVal.setType(Type.OR);
			return retVal;
		}

		public ImageSearchCriteriaBuilder sortByColumn(SortColumn column, SortType type) {
			switch(column) {
			case NAME:
				criteria.setSortColumn("name");
				break;
			case FILENAME:
				criteria.setSortColumn("filename");
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

		public ImageSearchCriteriaBuilder sortByAttribute(String attributeName, SortType type) {
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

		public ImageSearchCriteriaBuilder withPageSize(int pageSize) {
			if (paging == null) {
				paging = new PagingParams();
			}
			paging.setPageSize(pageSize);
			return this;
		}

		public ImageSearchCriteriaBuilder getPage(int page) {
			if (paging == null) {
				paging = new PagingParams();
			}
			paging.setCurrentPage(page);
			return this;
		}
	}

	public static class ImageAttributeCriteria {
		public enum Type {
			AND, OR
		}
		private Type type;
		private Collection<ImageAttributeDTO> attributes;
		private Collection<ImageAttributeCriteria> attCriteria;

		private ImageAttributeCriteria(){}

		public Type getType() {
			return type;
		}

		private void setType(Type type) {
			this.type = type;
		}

		public Collection<ImageAttributeDTO> getAttributes() {
			return attributes;
		}

		private void setAttributes(Collection<ImageAttributeDTO> attributes) {
			this.attributes = attributes;
		}

		private void setAttributes(ImageAttributeDTO[] attributes) {
			this.attributes = Arrays.asList(attributes);
		}

		public Collection<ImageAttributeCriteria> getAttCriteria() {
			return attCriteria;
		}

		private void setAttCriteria(ImageAttributeCriteria[] attCriteria) {
			this.attCriteria = Arrays.asList(attCriteria);
		}
	}

	private ImageSearchCriteria(){}

	public Collection<String> getIncludeFolderIds() {
		return includeFolderIds;
	}

	private void setIncludeFolderIds(Collection<String> includeFolderIds) {
		this.includeFolderIds = includeFolderIds;
	}

	public Collection<String> getExcludeFolderIds() {
		return excludeFolderIds;
	}

	private void setExcludeFolderIds(Collection<String> excludeFolderIds) {
		this.excludeFolderIds = excludeFolderIds;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	private void setFilename(String filename) {
		this.filename = filename;
	}

	public ImageAttributeCriteria getAttributeCriteria() {
		return attributeCriteria;
	}

	private void setAttributeCriteria(ImageAttributeCriteria attributeCriteria) {
		this.attributeCriteria = attributeCriteria;
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

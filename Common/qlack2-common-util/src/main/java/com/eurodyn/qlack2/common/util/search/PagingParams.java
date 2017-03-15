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
package com.eurodyn.qlack2.common.util.search;

import java.io.Serializable;

/**
 * This class is a paging utility.
 *
 * @author European Dynamics SA
 */
public class PagingParams implements Serializable {

	private static final long serialVersionUID = -5072605761125964814L;
	private int pageSize;
	private int currentPage;
	/**
	 * Default page size.
	 */
	public static final int DEFAULT_PAGE_SIZE = 20;

	/**
	 * Default Constructor.
	 */
	public PagingParams() {
		pageSize = DEFAULT_PAGE_SIZE;
		currentPage = 1;
	}

	/**
	 * Parameterized Constructor.
	 *
	 * @param pageSize
	 * @param currentPage
	 */
	public PagingParams(int pageSize, int currentPage) {
		this.pageSize = pageSize;
		this.currentPage = currentPage;
	}

	/**
	 *
	 * @param currentPage
	 */
	public PagingParams(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize
	 *            the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * @param currentPage
	 *            the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

}

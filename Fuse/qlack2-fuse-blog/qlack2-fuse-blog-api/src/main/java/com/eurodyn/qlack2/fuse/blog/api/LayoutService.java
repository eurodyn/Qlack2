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

package com.eurodyn.qlack2.fuse.blog.api;

import java.util.List;

import com.eurodyn.qlack2.fuse.blog.api.dto.LayoutDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogException;

/**
 *
 * @author European Dynamics SA
 */
public interface LayoutService {
	/**
	 * Creates a new blog layout
	 *
	 * @param layout
	 *            The information of the layout to create
	 * @return A LayoutDTO object representing the newly created layout
	 * @throws QBlogException
	 */
	String createLayout(LayoutDTO layout) throws QBlogException;

	/**
	 * Edits an existing blog layout
	 *
	 * @param layout
	 *            The blog to update. The blog id should be not null since it is
	 *            used to identify the blog.
	 * @throws QBlogException
	 */
	void editLayout(LayoutDTO layout) throws QBlogException;

	/**
	 * Deletes a layout
	 *
	 * @param layoutId
	 *            the id of the layout to remove
	 * @throws QBlogException
	 */
	void deleteLayout(String layoutId) throws QBlogException;

	/**
	 * Retrieves all the available layouts
	 *
	 * @return A list with all the layouts currently registered in the database
	 */
	List<LayoutDTO> getLayouts();

	/**
	 * Retrieves a layout with a specific id
	 *
	 * @param layoutId
	 *            The id of the layout to retrieve
	 * @return The layout with the specified id
	 * @throws QBlogException
	 */
	LayoutDTO getLayout(String layoutId) throws QBlogException;

	/**
	 * Retrieves a layout with a specific title
	 *
	 * @param layoutName
	 *            The title of the layout to retrieve
	 * @return The layout with the specified title or null if a layout with this
	 *         title does not exist.
	 */
	LayoutDTO getLayoutByName(String layoutName);
}

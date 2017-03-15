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
package com.eurodyn.qlack2.fuse.blog.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * @author European Dynamics SA
 */
public class QBlogException extends QException {
	private static final long serialVersionUID = -3859284551359054559L;

	// /**
	// * Error codes for Blog.
	// */
	// public static enum CODES implements ExceptionCode {
	//
	// ERR_BLOG_0001, // Blog object passed is not valid
	// ERR_BLOG_0002, // Blog object already exists
	// ERR_BLOG_0003, // FlagBlog object passed is not valid
	// ERR_BLOG_0004, // Layout object passed is not valid
	// ERR_BLOG_0005, // Layout object already exists
	// ERR_BLOG_0006, // Invalid Blog post,passed object could be null or
	// missing required fields.
	// ERR_BLOG_0007, // Invalid Blog post comment,passed object could be null
	// or missing required fields.
	// ERR_BLOG_0008, // Invalid Blog tag,passed object could be null or missing
	// required fields.
	// ERR_BLOG_0009, // A category with the specified name already exists for
	// this blog.
	// ERR_BLOG_0010, // Invalid Blog category,passed object could be null or
	// missing required fields.
	// ERR_BLOG_0011, // A tag with the specified name already exists for this
	// blog.
	// ERR_BLOG_0012, // A track-back post specified does not exist in DB.
	// ERR_BLOG_0013, // JMS error
	// ERR_BLOG_0014; // JAXB error
	// }

	public QBlogException(String message) {
		super(message);
	}

}
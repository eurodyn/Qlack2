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
package com.eurodyn.qlack2.fuse.simm.impl.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eurodyn.qlack2.fuse.simm.api.dto.CategoryDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QNullLookupEntry;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimFriends;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroupHasUser;

/**
 *
 * @author Shambhu
 */
public class SimValidationUtil {
	private static final Logger LOGGER = Logger.getLogger(SimValidationUtil.class
			.getName());

	/**
	 * This methods validates if provided groupingID is null.
	 *
	 * @param groupingID
	 * @throws QSIMMException
	 *             Throws exception if provided groupingID is null.
	 */
	public static void validateNullForGroupIDForGroup(String groupingID)
			throws QSIMMException {
		if (groupingID == null || groupingID.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided groupID is null.");
			throw new QNullLookupEntry("Provided groupID is null.");
		}
	}

	/**
	 * This methods validates if provided groupDTO is null.
	 *
	 * @param groupDTO
	 * @throws QSIMMException
	 *             Throws exception if provided groupDTO is null.
	 */
	public static void validateNullForGroupDTO(SocialGroupDTO groupDTO)
			throws QSIMMException {
		if (groupDTO == null) {
			LOGGER.log(Level.SEVERE, "Provided groupDTO is null.");
			throw new QNullLookupEntry("Provided groupDTO is null.");
		}
	}

	/**
	 * This methods validates if provided userID or groupID is null.
	 *
	 * @param userID
	 * @param groupID
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             groupID is null.
	 */
	public static void validateUserIdAndGroupId(String userID, String groupID)
			throws QSIMMException {
		if (userID == null || userID.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided userID is null.");
			throw new QNullLookupEntry("Provided userID is null.");
		}
		if (groupID == null || groupID.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided groupID is null.");
			throw new QNullLookupEntry("Provided groupID is null.");
		}
	}

	/**
	 * This methods validates if provided groupUser is null.
	 *
	 * @param groupUser
	 * @throws QSIMMException
	 *             Throws exception if provided groupUser is null.
	 */
	public static void validateGroupUserModelObject(SimGroupHasUser groupUser)
			throws QSIMMException {
		if (groupUser == null) {
			LOGGER.log(Level.SEVERE, "User has not been joined the group.");
			throw new QNullLookupEntry("User has not been joined the group.");
		}
	}

	/**
	 * This methods validates if provided userID is null.
	 *
	 * @param userID
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null.
	 */
	public static void validateNullForuserID(String userID)
			throws QSIMMException {
		if (userID == null || userID.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided userID is null.");
			throw new QNullLookupEntry("Provided userID is null.");
		}
	}

	/**
	 * This methods validates if provided searchTerm is null.
	 *
	 * @param searchTerm
	 * @throws QSIMMException
	 *             Throws exception if provided searchTerm is null.
	 */
	public static void validateNullForSearchTermForGroup(String searchTerm)
			throws QSIMMException {
		if (searchTerm == null || searchTerm.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided searchTerm/name is null.");
			throw new QNullLookupEntry("Provided searchTerm/name is null.");
		}
	}

	/**
	 * This methods validates if provided categoryName is null.
	 *
	 * @param categoryName
	 * @throws QSIMMException
	 *             Throws exception if provided categoryName is null.
	 */
	public static void validateNullForCategoryName(String categoryName)
			throws QSIMMException {
		if (categoryName == null || categoryName.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided categoryName is null.");
			throw new QNullLookupEntry("Provided categoryName is null.");
		}
	}

	/**
	 * This methods validates if provided categoryDTO is null.
	 *
	 * @param categoryDTO
	 * @throws QSIMMException
	 *             Throws exception if provided categoryDTO is null.
	 */
	public static void validateNullForCategoryDTO(CategoryDTO categoryDTO)
			throws QSIMMException {
		if (categoryDTO == null || categoryDTO.getId().trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided categoryDTO is null.");
			throw new QNullLookupEntry("Provided categoryDTO is null.");
		}
	}

	/**
	 * This methods validates if provided categoryID is null.
	 *
	 * @param categoryID
	 * @throws QSIMMException
	 *             Throws exception if provided categoryID is null.
	 */
	public static void validateNullForCategoryId(String categoryID)
			throws QSIMMException {
		if (categoryID == null || categoryID.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided categoryID is null.");
			throw new QNullLookupEntry("Provided categoryID is null.");
		}
	}

	/**
	 * This methods validates if provided searchTerm is null.
	 *
	 * @param searchTerm
	 * @throws QSIMMException
	 *             Throws exception if provided searchTerm is null.
	 */
	public static void validateNullForSearchTerm(String searchTerm)
			throws QSIMMException {
		if (searchTerm == null || searchTerm.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided searchTerm is null.");
			throw new QNullLookupEntry("Provided searchTerm is null.");
		}
	}

	/**
	 * This methods validates if provided groupDTO is null or does not contain
	 * Id.
	 *
	 * @param groupDTO
	 * @throws QSIMMException
	 *             Throws exception if provided groupDTO is null or does not
	 *             contain Id..
	 */
	public static void validateNullForIdInGroupDTO(SocialGroupDTO groupDTO)
			throws QSIMMException {
		if (groupDTO == null || groupDTO.getId() == null
				|| groupDTO.getId().trim().equals("")) {
			LOGGER.log(Level.SEVERE,
					"Provided groupDTO is null or does not contain Id.");
			throw new QNullLookupEntry(
					"Provided groupDTO is null or does not contain Id.");
		}
	}

	/**
	 *
	 * @param userID
	 * @param status
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             status is wrong
	 */
	public static void validateNullForUserIdAndStatus(String userID, byte status)
			throws QSIMMException {
		if (userID == null || userID.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided userID is null.");
			throw new QNullLookupEntry("Provided userID is null.");
		}
		if (!(status == SIMMConstants.GROUP_USER_STATUS_REQUESTED_NEW
				|| status == SIMMConstants.GROUP_USER_STATUS_ACCEPTED
				|| status == SIMMConstants.GROUP_USER_STATUS_BANNED || status == SIMMConstants.GROUP_USER_STATUS_INVITED)) {
			throw new QNullLookupEntry(
					"Provided status for group user is incorrect.");
		}
	}

	/**
	 * This methods validates if provided userID or groupID is null.
	 *
	 * @param userID
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             groupID is null.
	 */
	public static void validateUserId(String userID) throws QSIMMException {
		if (userID == null || userID.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided userID is null.");
			throw new QNullLookupEntry("Provided userID is null.");
		}
	}

	/**
	 * Validate userId and friendId
	 *
	 * @param userID
	 * @param friendId
	 * @throws QSIMMException
	 */
	public static void validateUserIdAndFriendId(String userID, String friendId)
			throws QSIMMException {
		validateUserId(userID);
		if (friendId == null || friendId.trim().equals("")) {
			LOGGER.log(Level.SEVERE, "Provided friendId is null.", Thread
					.currentThread().getStackTrace());
			throw new QNullLookupEntry("Provided groupID is null.");
		}
	}

	/**
	 * Validate friend object
	 *
	 * @param friend
	 * @throws QSIMMException
	 */
	public static void validateFriendObject(SimFriends friend)
			throws QSIMMException {
		if (friend == null) {
			LOGGER.log(Level.SEVERE, "Friend object is null.");
			throw new QNullLookupEntry("Friend object is null.");
		}
	}

	/**
	 * Validate a pair of friend objects
	 *
	 * @param friend
	 * @throws QSIMMException
	 */
	public static void validateFriendObjectPair(SimFriends friend,
			SimFriends inverseFriend) throws QSIMMException {
		if ((friend == null) && (inverseFriend == null)) {
			LOGGER.log(Level.SEVERE, "Friend object is null.");
			throw new QNullLookupEntry("Friend object is null.");
		}
	}
}

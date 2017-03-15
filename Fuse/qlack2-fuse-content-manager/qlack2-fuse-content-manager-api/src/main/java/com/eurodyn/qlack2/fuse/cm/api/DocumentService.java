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
package com.eurodyn.qlack2.fuse.cm.api;

import com.eurodyn.qlack2.fuse.cm.api.dto.*;
import com.eurodyn.qlack2.fuse.cm.api.exception.QFileNotFoundException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QNodeLockException;

import java.util.List;
import java.util.Map;

public interface DocumentService {
	// **********************
	// Folder functionalities
	// **********************

	/**
	 * Creates a new folder under a specific parent folder
	 * 
	 * 
	 * @param folder
	 *            The FolderDTO of the folder to be created.
	 * @param userId
	 *            The ID of the logged in user who is creating the folder.
	 * @param lockToken
	 *            A lock token id, which will used to examine whether the user
	 *            is allowed to create a folder under the specific hierarchy
	 *            (check for locked ascendant).
	 * @return The id of the newly created folder conflict
	 * @throws QNodeLockException
	 *             If the folder/node cannot be created under the specific
	 *             hierarchy since an ascendant is already locked
	 *
	 */
	String createFolder(FolderDTO folder, String userId, String lockToken)
			throws QNodeLockException;

	/**
	 * Deletes a folder 
	 * 
	 * 
	 * @param folderID
	 *            The ID of the folder to be deleted.
	 * @param lockToken
	 *            A lock token id, which will used to examine whether the user
	 *            is allowed to delete the folder under the specific hierarchy.
	 *            It checks for locked ascendant or if the folder has been locked by other user.
	 * @throws QNodeLockException
	 *             If the folder/node cannot be deleted under the specific
	 *             hierarchy since an ascendant or the folder itself is already locked
	 * @throws QFileNotFoundException
	 *             If the node to be deleted does not exist.
	 */
	void deleteFolder(String folderID, String lockToken)
			throws QNodeLockException, QFileNotFoundException;

	
	
	
	void renameFolder(String folderID, String newName, String userId,
			String lockToken) throws QNodeLockException, QFileNotFoundException;

	/**
	 * Finds an return a folder with the specific ID along with is children
	 * (optionally)
	 * 
	 * 
	 * @param folderID
	 *            The ID of the folder to be retrieved.
	 * @param lazyRelatives
	 *            When true it will not compute the relatives
	 *            (ancestors/descendats) of the required folder.
	 * @param findPath
	 *            When true the directory path until the required folder will be
	 *            computed.
	 * @return The FolderDTO which contains all the information about the folder.
	 */
	FolderDTO getFolderByID(String folderID, boolean lazyRelatives, boolean findPath);

	/**
	 * Gets the content of a folder node in a zip file. This method retrieves
	 * the binary contents of all files included in the folder node specified as
	 * well as their attributes if the includeAttributes argument is true. The
	 * contents of files included in other folders contained by the folder
	 * specified are also retrieved in case the isDeep argument is true.
	 * 
	 * @param folderID
	 *            The ID of the folder the content of which is to be retrieved
	 * @param includeProperties
	 *            If true then a separate properties file will be created inside
	 *            the final zip file for each node included in the result,
	 *            containing the nodes' properties in the form: propertyName =
	 *            propertyValue
	 * @param isDeep
	 *            If true then the whole tree commencing by the specified folder
	 *            will be traversed in order to be included in the final result.
	 *            Otherwise only the file nodes which are direct children of the
	 *            specified folder will be included in the result.
	 * @return The binary content (and properties if applicable) of the
	 *         specified folder as a byte array representing a zip file.
	 */
	byte[] getFolderAsZip(String folderID, boolean includeProperties,
			boolean isDeep);

	// **********************
	// File functionalities
	// **********************

	String createFile(FileDTO file, String userID, String lockToken)
			throws QNodeLockException;

	/**
	 * Deletes a file 
	 * 
	 * 
	 * @param fileID
	 *            The ID of the file to be deleted.
	 * @param lockToken
	 *            A lock token id, which will used to examine whether the user
	 *            is allowed to delete the file, under the specific hierarchy.
	 *            It checks for locked ascendant or if the file itself has been locked by other user.
	 * @throws QNodeLockException
	 *             If the node cannot be deleted under the specific
	 *             hierarchy since an ascendant or the folder itself is already locked
	 * @throws QFileNotFoundException
	 *             If the node to be deleted does not exist.
	 */
	void deleteFile(String fileID, String lockToken) throws QNodeLockException, QFileNotFoundException;

	void renameFile(String fileID, String newName, String userID,
			String lockToken) throws QNodeLockException, QFileNotFoundException;

	/**
	 * Finds an return a file with the specific ID along with its versions
	 * (optionally)
	 * 
	 * 
	 * @param fileID
	 *            The ID of the folder to be retrieved.
	 * @param findPath
	 *            When true the directory path until the required folder will be
	 *            computed.
	 * @return The FileDTO which contains all the information about the required file
	 */
	FileDTO getFileByID(String fileID, boolean includeVersions, boolean findPath);

	// **********************
	// Common functionalities
	// **********************

	NodeDTO getNodeByID(String nodeID);

	FolderDTO getParent(String nodeID, boolean lazyRelatives);

	List<FolderDTO> getAncestors(String nodeID);

	String createAttribute(String nodeId, String attributeName,
			String attributeValue, String userId, String lockToken)
			throws QNodeLockException, QFileNotFoundException;
	
	void updateAttribute(String nodeID, String attributeName,
			String attributeValue, String userID, String lockToken)
			throws QNodeLockException, QFileNotFoundException;

	void updateAttributes(String nodeID, Map<String, String> attributes,
			String userID, String lockToken) throws QNodeLockException;

	void deleteAttribute(String nodeID, String attributeName, String userID,
			String lockToken) throws QNodeLockException, QFileNotFoundException;

	String copy(String nodeID, String newParentID, String userID,
			String lockToken);

	void move(String nodeID, String newParentID, String userID, String lockToken);

	/**
	 * Checks whether a file or folder with the same name, already exists in a
	 * specified directory
	 *
	 * @param name
	 *            The name of the new file which should be checked to find out
	 *            if a duplicate name exists
	 * @param parentNodeID
	 *            The ID of the folder within which a file is a specified name
	 *            is searched
	 *
	 * @return true if the specified file name is unique in the folder.
	 */
	boolean isFileNameUnique(String name, String parentNodeID);
	
	
	/**
	 * Checks whether the folder or file names are unique in the specified
	 * directory and returns a lists on the duplicate
	 * 
	 * @param fileNames
	 *            The file names which will be checked id it is unique in a
	 *            provided directory.
	 * @param parentId
	 *            The id of the parent folder within which duplicate file and
	 *            folder names are searched.
	 * 
	 * @return a lists on the duplicates.
	 */
	List<String> duplicateFileNamesInDirectory(List<String> fileNames,
			String parentId);

	/**
	 * Creates a new file as well as a new version for the specific file
	 *
	 * @param cmFile
	 *            The FileDTO which contain all the new file information
	 * @param cmVersion
	 *            The new version.
	 * @param content
	 *            The binary content of the new version. It is optional, so null can be used instead.
	 * @param userID
	 *            The user ID of the creator.
	 * @param lockToken
	 *            The lock token to be used so as to avoid lock conflicts.
	 * 
	 * @return CreateFileAndVersionStatusDTO which contains the ids of the newly
	 *         created file and version.
	 */
	CreateFileAndVersionStatusDTO createFileAndVersion(FileDTO cmFile,
			VersionDTO cmVersion, byte[] content, String userID, String lockToken);
}

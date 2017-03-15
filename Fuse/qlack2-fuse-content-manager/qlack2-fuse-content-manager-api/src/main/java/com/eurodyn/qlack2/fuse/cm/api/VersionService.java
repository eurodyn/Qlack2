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

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.fuse.cm.api.dto.BinChunkDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.fuse.cm.api.exception.QNodeLockException;

public interface VersionService {

	String createVersion(String fileID, VersionDTO cmVersion,
			String filename,
			byte[] content, String userID, String lockToken)
			throws QNodeLockException;
	
	List<VersionDTO> getFileVersions(String fileID);
	
	VersionDTO getFileLatestVersion(String fileID);

	byte[] getBinContent(String fileID);
	
	byte[] getBinContent(String fileID, String versionName);
	
	byte[] getFileAsZip(String fileID, boolean includeProperties);

	byte[] getFileAsZip(String fileID, String versionName,
			boolean includeProperties);

	String setBinChunk(String versionID, byte[] content, int chunkIndex);
	
	BinChunkDTO getBinChunk(String versionID, int chunkIndex);
	
	void updateAttribute(String fileID, String attributeName, 
			String attributeValue, String userID, String lockToken)
			throws QNodeLockException;

	void updateAttribute(String fileID, String versionName,
			String attributeName, String attributeValue, String userID, String lockToken)
			throws QNodeLockException;
	
	void updateAttributes(String fileID, Map<String, String> attributes, 
			String userID, String lockToken)
			throws QNodeLockException;

	void updateAttributes(String fileID, String versionName,
			Map<String, String> attributes, String userID, String lockToken)
			throws QNodeLockException;
	
	void deleteAttribute(String fileID, String attributeName, String userID, String lockToken) 
			throws QNodeLockException;

	void deleteAttribute(String fileID, String versionName,
			String attributeName, String userID, String lockToken) throws QNodeLockException;

	void cleanupFS(int cycleLength);

	/**
	 * Transfers the binary content from the flu_file temporary table to the
	 * cm_version_bin.
	 * 
	 * @param attachmentID
	 *            the ID of the chunks in the flu_file table. All the chunks of
	 *            the same file have the same ID.
	 * 
	 * @param versionID
	 *            The ID of the version to which is related with the binary
	 *            content to be transfered.
	 * */
	void transferFromFluToVersionBin(String attachmentID, String versionID);

	/**Retrieves the mime type of a provided binary content.
	 * @param fileContent The  provided binary content.
	 * @return The mime type of the content.
	 * */
	public String getMimeType(byte[] fileContent);
}

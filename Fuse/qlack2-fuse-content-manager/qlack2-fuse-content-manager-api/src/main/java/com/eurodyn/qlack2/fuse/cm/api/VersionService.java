/*
 * Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
 *
 * Licensed under the EUPL, Version 1.1 only (the "License"). You may not use this work except in
 * compliance with the Licence. You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence
 * is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */
package com.eurodyn.qlack2.fuse.cm.api;

import java.util.List;
import java.util.Map;
import com.eurodyn.qlack2.fuse.cm.api.dto.BinChunkDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.fuse.cm.api.exception.QNodeLockException;

/**
 * The Interface VersionService.
 */
public interface VersionService {

  /**
   * Creates the version.
   *
   * @param fileID the file ID
   * @param cmVersion the cm version
   * @param filename the filename
   * @param content the content
   * @param userID the user ID
   * @param lockToken the lock token
   * @return the string
   * @throws QNodeLockException the q node lock exception
   */
  String createVersion(String fileID, VersionDTO cmVersion, String filename, byte[] content,
      String userID, String lockToken) throws QNodeLockException;

  /**
   * Gets the file versions.
   *
   * @param fileID the file ID
   * @return the file versions
   */
  List<VersionDTO> getFileVersions(String fileID);

  /**
   * Gets the file latest version.
   *
   * @param fileID the file ID
   * @return the file latest version
   */
  VersionDTO getFileLatestVersion(String fileID);


  /**
   * Gets the bin content.
   *
   * @param fileID the file ID
   * @return the bin content
   */
  byte[] getBinContent(String fileID);

  /**
   * Gets the bin content.
   *
   * @param fileID the file ID
   * @param versionName the version name
   * @return the bin content
   */
  byte[] getBinContent(String fileID, String versionName);

  /**
   * Gets the file as zip.
   *
   * @param fileID the file ID
   * @param includeProperties the include properties
   * @return the file as zip
   */
  byte[] getFileAsZip(String fileID, boolean includeProperties);

  /**
   * Gets the file as zip.
   *
   * @param fileID the file ID
   * @param versionName the version name
   * @param includeProperties the include properties
   * @return the file as zip
   */
  byte[] getFileAsZip(String fileID, String versionName, boolean includeProperties);

  /**
   * Sets the bin chunk.
   *
   * @param versionID the version ID
   * @param content the content
   * @param chunkIndex the chunk index
   * @return the string
   */
  String setBinChunk(String versionID, byte[] content, int chunkIndex);

  /**
   * Gets the bin chunk.
   *
   * @param versionID the version ID
   * @param chunkIndex the chunk index
   * @return the bin chunk
   */
  BinChunkDTO getBinChunk(String versionID, int chunkIndex);

  /**
   * Update attribute.
   *
   * @param fileID the file ID
   * @param attributeName the attribute name
   * @param attributeValue the attribute value
   * @param userID the user ID
   * @param lockToken the lock token
   * @throws QNodeLockException the q node lock exception
   */
  void updateAttribute(String fileID, String attributeName, String attributeValue, String userID,
      String lockToken) throws QNodeLockException;

  /**
   * Update attribute.
   *
   * @param fileID the file ID
   * @param versionName the version name
   * @param attributeName the attribute name
   * @param attributeValue the attribute value
   * @param userID the user ID
   * @param lockToken the lock token
   * @throws QNodeLockException the q node lock exception
   */
  void updateAttribute(String fileID, String versionName, String attributeName,
      String attributeValue, String userID, String lockToken) throws QNodeLockException;

  /**
   * Update attributes.
   *
   * @param fileID the file ID
   * @param attributes the attributes
   * @param userID the user ID
   * @param lockToken the lock token
   * @throws QNodeLockException the q node lock exception
   */
  void updateAttributes(String fileID, Map<String, String> attributes, String userID,
      String lockToken) throws QNodeLockException;

  /**
   * Update attributes.
   *
   * @param fileID the file ID
   * @param versionName the version name
   * @param attributes the attributes
   * @param userID the user ID
   * @param lockToken the lock token
   * @throws QNodeLockException the q node lock exception
   */
  void updateAttributes(String fileID, String versionName, Map<String, String> attributes,
      String userID, String lockToken) throws QNodeLockException;

  /**
   * Updates the values, content as also the attributes of a specific version.
   *
   * @param fileID the file ID
   * @param versionDTO the version DTO
   * @param content the content
   * @param userID the user ID
   * @param updateAllAttribtues A true value defines that all attributes should be updated/deleted.
   *        Only the custom created attributes are allowed to be deleted. A false value updates only
   *        the mandatory attribute values
   * @param lockToken the lock token
   */
  void updateVersion(String fileID, VersionDTO versionDTO, byte[] content, String userID,
      boolean updateAllAttribtues, String lockToken);

  /**
   * Delete attribute.
   *
   * @param fileID the file ID
   * @param attributeName the attribute name
   * @param userID the user ID
   * @param lockToken the lock token
   * @throws QNodeLockException the q node lock exception
   */
  void deleteAttribute(String fileID, String attributeName, String userID, String lockToken)
      throws QNodeLockException;

  /**
   * Delete attribute.
   *
   * @param fileID the file ID
   * @param versionName the version name
   * @param attributeName the attribute name
   * @param userID the user ID
   * @param lockToken the lock token
   * @throws QNodeLockException the q node lock exception
   */
  void deleteAttribute(String fileID, String versionName, String attributeName, String userID,
      String lockToken) throws QNodeLockException;

  /**
   * Cleanup FS.
   *
   * @param cycleLength the cycle length
   */
  void cleanupFS(int cycleLength);

  /**
   * Transfers the binary content from the flu_file temporary table to the cm_version_bin.
   * 
   * @param attachmentID the ID of the chunks in the flu_file table. All the chunks of the same file
   *        have the same ID.
   * 
   * @param versionID The ID of the version to which is related with the binary content to be
   *        transfered.
   */
  void transferFromFluToVersionBin(String attachmentID, String versionID);

  /**
   * Retrieves the mime type of a provided binary content.
   * 
   * @param fileContent The provided binary content.
   * @return The mime type of the content.
   */
  public String getMimeType(byte[] fileContent);
}

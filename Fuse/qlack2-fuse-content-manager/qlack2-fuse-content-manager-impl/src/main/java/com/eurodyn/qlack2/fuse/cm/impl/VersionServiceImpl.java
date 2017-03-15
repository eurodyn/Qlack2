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
package com.eurodyn.qlack2.fuse.cm.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.joda.time.DateTime;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.cm.api.ConcurrencyControlService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.dto.BinChunkDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.fuse.cm.api.exception.QAncestorFolderLockException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QIOException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QNodeLockException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QSelectedNodeLockException;
import com.eurodyn.qlack2.fuse.cm.api.storage.StorageEngine;
import com.eurodyn.qlack2.fuse.cm.impl.model.Node;
import com.eurodyn.qlack2.fuse.cm.impl.model.NodeAttribute;
import com.eurodyn.qlack2.fuse.cm.impl.model.QVersionDeleted;
import com.eurodyn.qlack2.fuse.cm.impl.model.Version;
import com.eurodyn.qlack2.fuse.cm.impl.model.VersionAttribute;
import com.eurodyn.qlack2.fuse.cm.impl.model.VersionDeleted;
import com.eurodyn.qlack2.fuse.cm.impl.storage.StorageEngineFactory;
import com.eurodyn.qlack2.fuse.cm.impl.util.Constants;
import com.eurodyn.qlack2.fuse.cm.impl.util.ConverterUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Transactional
@OsgiServiceProvider(classes = { VersionService.class })
@Singleton
public class VersionServiceImpl implements VersionService {
	private static final Logger LOGGER = Logger.getLogger(VersionServiceImpl.class.getName());

	@PersistenceContext(unitName = "fuse-contentmanager")
	private EntityManager em;

	private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
	private TikaConfig tika;

	@Inject
	private ConcurrencyControlService concurrencyControlService;

	@Inject
	private StorageEngineFactory storageEngineFactory;
	private StorageEngine storageEngine;

	@Override
	@Transactional(TxType.SUPPORTS)
	public String getMimeType(byte[] fileContent) {
		String retVal = DEFAULT_MIME_TYPE;
		InputStream stream = new ByteArrayInputStream(fileContent);
		try {
			String mimetype = tika.getDetector()
					.detect(TikaInputStream.get(stream), new Metadata())
					.toString();
			retVal = mimetype;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not detect content-type.", e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return retVal;
	}

	@PostConstruct
	public void init() throws TikaException, IOException {
		// If the storage engine has not been explicitly set, create one using
		// the default configuration from StorageEngineFactory.
		if (storageEngine == null) {
			storageEngine = storageEngineFactory.getEngine();
		}
		tika = new TikaConfig();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String createVersion(String fileID, VersionDTO cmVersion,
			String filename, byte[] content, String userID, String lockToken)
			throws QNodeLockException {
		Node file = Node.findFile(fileID, em);

		// Check whether there is a lock conflict with the current node.
		NodeDTO selConflict = concurrencyControlService
				.getSelectedNodeWithLockConflict(fileID, lockToken);
		if (selConflict != null && selConflict.getName() != null) {
			throw new QSelectedNodeLockException(
					"The selected file is locked"
							+ " and an"
							+ " invalid lock token was passed; A new version cannot"
							+ "be created for this file.",
					selConflict.getId(), selConflict.getName());
		}

		// Check for ancestor node (folder) lock conflicts.
		if (file.getParent() != null) {
			NodeDTO ancConflict = concurrencyControlService
					.getAncestorFolderWithLockConflict(
							file.getParent().getId(), lockToken);
			// In case a conflict was found an exception is thrown
			if (ancConflict != null && ancConflict.getId() != null) {
				throw new QAncestorFolderLockException(
						"An ancestor folder is locked"
								+ " and an"
								+ " invalid lock token was passed; the folder cannot be created.",
						ancConflict.getId(), ancConflict.getName());
			}
		}

		Version version = new Version();
		version.setName(cmVersion.getName());
		version.setNode(file);
		version.setFilename(filename);
		// The content is provided, so the mimetype is immediately computed
		if (content != null) {
			version.setMimetype(getMimeType(content));
		}
		// The mimeType is pre-computed
		else if (cmVersion.getMimetype() != null) {
			version.setMimetype(cmVersion.getMimetype());
		}
		// The entire content is provided as a binary as a result the size can be computed.
		if (content != null) {
			version.setSize(new Long(content.length));
		}
		// THe size is pre-computed (e.g Retrieved from the flu_file)
		else {
			version.setSize(cmVersion.getSize());
		}
		
		
		// Set created / last modified information
		version.setAttributes(new ArrayList<VersionAttribute>());
		DateTime now = DateTime.now();
		version.setCreatedOn(now.getMillis());
		version.getAttributes()
				.add(new VersionAttribute(Constants.ATTR_CREATED_BY, userID,
						version));
		version.getAttributes().add(
				new VersionAttribute(Constants.ATTR_LAST_MODIFIED_ON, String
						.valueOf(now.getMillis()), version));
		version.getAttributes().add(
				new VersionAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID,
						version));

		em.persist(version);

		// Persist binary content.
		if (content != null) {
			storageEngine.setVersionContent(version.getId(), content);
		}

		return version.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<VersionDTO> getFileVersions(String fileID) {
		Query query = em
				.createQuery("SELECT v FROM Version v WHERE v.node.id = :fileID ORDER BY v.createdOn ASC");
		query.setParameter("fileID", fileID);
		@SuppressWarnings("unchecked")
		List<Version> versions = query.getResultList();
		return ConverterUtil.versionToVersionDTOList(versions);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public VersionDTO getFileLatestVersion(String fileID) {
		return ConverterUtil.versionToVersionDTO(Version.findLatest(fileID, em));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public byte[] getBinContent(String fileID) {
		return getBinContent(fileID, null);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public byte[] getBinContent(String fileID, String versionName) {
		Version version = Version.find(fileID, versionName, em);

		byte[] retVal;
		try {
			retVal = storageEngine.getVersionContent(version.getId());
		} catch (IOException e) {
			throw new QIOException(
					MessageFormat.format("Could not obtain content for file "
							+ "{0}, version {1}", fileID, versionName));
		}

		return retVal;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public byte[] getFileAsZip(String fileID, boolean includeProperties) {
		return getFileAsZip(fileID, null, includeProperties);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public byte[] getFileAsZip(String fileID, String versionName,
			boolean includeProperties) {
		Node file = Node.findFile(fileID, em);
		Version version = Version.find(fileID, versionName, em);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ZipOutputStream zipFile = new ZipOutputStream(outStream);

		try {
			// Write binary content
			ZipEntry entry = new ZipEntry(version.getFilename());
			zipFile.putNextEntry(entry);
			zipFile.write(getBinContent(fileID, versionName));

			if (includeProperties) {
				// Write file properties
				entry = new ZipEntry(file.getAttribute(Constants.ATTR_NAME) + ".properties");
				zipFile.putNextEntry(entry);
				StringBuilder buf = new StringBuilder();
				// Include a created on property
				buf.append(Constants.CREATED_ON).append(" = ").append(file.getCreatedOn()).append("\n");
				for (NodeAttribute attribute : file.getAttributes()) {
					buf.append(attribute.getName());
					buf.append(" = ");
					buf.append(attribute.getValue());
					buf.append("\n");
				}

				// Write version properties - written in a separate file since
				// there are some properties which exist both in the file and in
				// the version (ex. last modified on/by)
				entry = new ZipEntry(version.getName() + ".properties");
				zipFile.putNextEntry(entry);
				buf = new StringBuilder();
				// Include a created on property
				buf.append(Constants.CREATED_ON).append(" = ").append(file.getCreatedOn()).append("\n");
				for (VersionAttribute attribute : version.getAttributes()) {
					buf.append(attribute.getName());
					buf.append(" = ");
					buf.append(attribute.getValue());
					buf.append("\n");
				}

				zipFile.write(buf.toString().getBytes());
			}
			zipFile.close();
			outStream.close();
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QIOException("Error writing ZIP for version " + versionName
					+ " of file  with ID " + fileID);
		}

		return outStream.toByteArray();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateAttribute(String fileID, String attributeName,
			String attributeValue, String userID, String lockToken)
			throws QNodeLockException {
		updateAttribute(fileID, null, attributeName, attributeValue, userID, lockToken);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateAttribute(String fileID, String versionName,
			String attributeName, String attributeValue, String userID, String lockToken)
			throws QNodeLockException {
		Version version = Version.find(fileID, versionName, em);
		Node file = version.getNode();

		if ((file.getLockToken() != null)
				&& (!file.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("File with ID " + file
					+ " is locked and an "
					+ "invalid lock token was passed; the file version "
					+ "attributes cannot be updated.");
		}

		version.setAttribute(attributeName, attributeValue, em);

		// Update last modified information
		if (userID != null) {
			DateTime now = DateTime.now();
			version.setAttribute(Constants.ATTR_LAST_MODIFIED_ON,
					String.valueOf(now.getMillis()), em);
			version.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, em);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateAttributes(String fileID, Map<String, String> attributes,
			String userID, String lockToken)
			throws QNodeLockException {
		updateAttributes(fileID, null, attributes, userID, lockToken);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateAttributes(String fileID, String versionName,
			Map<String, String> attributes, String userID, String lockToken)
			throws QNodeLockException {
		Version version = Version.find(fileID, versionName, em);
		Node file = version.getNode();

		if ((file.getLockToken() != null)
				&& (!file.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("File with ID " + file
					+ " is locked and an "
					+ "invalid lock token was passed; the file version"
					+ "attributes cannot be updated.");
		}

		for (String attributeName : attributes.keySet()) {
			version.setAttribute(attributeName, attributes.get(attributeName),
					em);
		}

		// Update last modified information
		if (userID != null) {
			DateTime now = DateTime.now();
			version.setAttribute(Constants.ATTR_LAST_MODIFIED_ON,
					String.valueOf(now.getMillis()), em);
			version.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, em);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteAttribute(String fileID, String attributeName, String userID, String lockToken)
			throws QNodeLockException {
		deleteAttribute(fileID, null, attributeName, userID, lockToken);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteAttribute(String fileID, String versionName,
			String attributeName, String userID, String lockToken) throws QNodeLockException {
		Version version = Version.find(fileID, versionName, em);
		Node file = version.getNode();

		if ((file.getLockToken() != null)
				&& (!file.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("File with ID " + file
					+ " is locked and an "
					+ "invalid lock token was passed; the file version"
					+ "attributes cannot be deleted.");
		}

		version.removeAttribute(attributeName, em);

		// Update last modified information
		if (userID != null) {
			DateTime now = DateTime.now();
			version.setAttribute(Constants.ATTR_LAST_MODIFIED_ON,
					String.valueOf(now.getMillis()), em);
			version.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, em);
		}
	}

	@Override
	public String setBinChunk(String versionID, byte[] content, int chunkIndex) {
		// Save the chunk.
		String binChunkID = storageEngine.setBinChunk(versionID, content, chunkIndex);

		
		// TODO -- Was 0 and now it is set to 1 since it will never get in here
		// due to the fact that the index begins from 1. Check it also works for FSDB
		// If this is the first chunk, try to find the mime-type.
		if (chunkIndex == 1) {
			String mimeType = getMimeType(content);
			if (StringUtils.isNotBlank(mimeType)) {
				Version version = Version.find(versionID, em);
				version.setMimetype(mimeType);
			}
		}

		return binChunkID;
	}

	@Override
	public BinChunkDTO getBinChunk(String versionID, int chunkIndex) {
		return storageEngine.getBinChunk(versionID, chunkIndex);
	}

	@Override
	public void cleanupFS(int cycleLength) {
		QVersionDeleted qVersionDeleted = QVersionDeleted.versionDeleted;
		List<VersionDeleted> vdList = new JPAQueryFactory(em)
				.selectFrom(qVersionDeleted)
				.limit(cycleLength)
				.setLockMode(LockModeType.PESSIMISTIC_WRITE)
				.fetch();
		
		for (VersionDeleted vd : vdList) {
			storageEngine.deleteVersion(vd.getId());
			em.remove(vd);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void transferFromFluToVersionBin(String attachmentID,
			String versionID) {
		StoredProcedureQuery query = em.createStoredProcedureQuery("flu_to_version_bin");
		query.registerStoredProcedureParameter("flu_file_ID", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("version_ID", String.class, ParameterMode.IN);
		
		query.setParameter("flu_file_ID", attachmentID);
		query.setParameter("version_ID", versionID);
		query.executeUpdate();
		
		
	}

}

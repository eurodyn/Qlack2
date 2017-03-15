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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.joda.time.DateTime;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.cm.api.ConcurrencyControlService;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.api.dto.CreateFileAndVersionStatusDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.FileDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.FolderDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.VersionDTO;
import com.eurodyn.qlack2.fuse.cm.api.exception.QAncestorFolderLockException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QDescendantNodeLockException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QFileNotFoundException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QIOException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QInvalidPathException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QNodeLockException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QSelectedNodeLockException;
import com.eurodyn.qlack2.fuse.cm.impl.model.Node;
import com.eurodyn.qlack2.fuse.cm.impl.model.NodeAttribute;
import com.eurodyn.qlack2.fuse.cm.impl.model.NodeType;
import com.eurodyn.qlack2.fuse.cm.impl.model.QNode;
import com.eurodyn.qlack2.fuse.cm.impl.model.QNodeAttribute;
import com.eurodyn.qlack2.fuse.cm.impl.util.Constants;
import com.eurodyn.qlack2.fuse.cm.impl.util.ConverterUtil;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Transactional
@Singleton
@OsgiServiceProvider(classes = { DocumentService.class })
public class DocumentServiceImpl implements DocumentService {
	private static final Logger LOGGER = Logger.getLogger(DocumentServiceImpl.class.getName());

	@PersistenceContext(unitName = "fuse-contentmanager")
	private EntityManager em;

	@Inject
	private VersionService versionService;

	@Inject
	private ConcurrencyControlService concurrencyControlService;

	@Override
	@Transactional(TxType.REQUIRED)
	public String createFolder(FolderDTO folder, String userID, String lockToken) throws QNodeLockException {

		Node parent = null;
		if (folder.getParentId() != null) {
			parent = Node.findFolder(folder.getParentId(), em);
		}

		// Check for ancestor node (folder) lock conflicts.
		if (parent != null) {
			NodeDTO ancConflict = concurrencyControlService.getAncestorFolderWithLockConflict(parent.getId(),
					lockToken);
			// In case a conflict was found an exception is thrown
			if (ancConflict != null && ancConflict.getId() != null) {
				throw new QAncestorFolderLockException(
						"An ancestor folder is locked" + " and an"
								+ " invalid lock token was passed; the folder cannot be created.",
						ancConflict.getId(), ancConflict.getName());
			}
		}

		Node folderEntity = ConverterUtil.nodeDTOToNode(folder);
		folderEntity.setParent(parent);
		// Set created / last modified information
		DateTime now = DateTime.now();
		folderEntity.setCreatedOn(now.getMillis());
		folderEntity.getAttributes().add(new NodeAttribute(Constants.ATTR_CREATED_BY, userID, folderEntity));
		folderEntity.getAttributes()
				.add(new NodeAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), folderEntity));
		folderEntity.getAttributes().add(new NodeAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, folderEntity));
		em.persist(folderEntity);
		return folderEntity.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteFolder(String folderID, String lockToken) throws QNodeLockException, QFileNotFoundException {
		Node node = Node.findNode(folderID, em);
		if (node == null) {
			throw new QFileNotFoundException("The folder you want to delete does not exist");
		}

		// Check whether there is a lock conflict with the current node.
		NodeDTO selConflict = concurrencyControlService.getSelectedNodeWithLockConflict(folderID, lockToken);
		if (selConflict != null && selConflict.getName() != null) {
			throw new QSelectedNodeLockException(
					"The selected folder is locked" + " and an"
							+ " invalid lock token was passed; the folder cannot be deleted.",
					selConflict.getId(), selConflict.getName());
		}

		// Check for ancestor node (folder) lock conflicts.
		if (node.getParent() != null) {
			NodeDTO ancConflict = concurrencyControlService.getAncestorFolderWithLockConflict(node.getParent().getId(),
					lockToken);
			// In case a conflict was found an exception is thrown
			if (ancConflict != null && ancConflict.getId() != null) {
				throw new QAncestorFolderLockException(
						"An ancestor folder is locked" + " and an"
								+ " invalid lock token was passed; the folder cannot be deleted.",
						ancConflict.getId(), ancConflict.getName());
			}
		}

		// Check for descendant node lock conflicts
		NodeDTO desConflict = concurrencyControlService.getDescendantNodeWithLockConflict(folderID, lockToken);
		// In case a conflict was found an exception is thrown
		if (desConflict != null && desConflict.getId() != null) {
			throw new QDescendantNodeLockException(
					"An descendant node is locked" + " and an"
							+ " invalid lock token was passed; the folder cannot be deleted.",
					desConflict.getId(), desConflict.getName());
		}
		em.remove(node);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void renameFolder(String folderID, String newName, String userID, String lockToken)
			throws QNodeLockException, QFileNotFoundException {
		Node folder = Node.findFolder(folderID, em);
		if (folder == null) {
			throw new QFileNotFoundException("The folder you want to rename does not exist");
		}

		if ((folder.getLockToken() != null) && (!folder.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("Folder with ID " + folderID + " is locked and an "
					+ "invalid lock token was passed; the folder cannot be renamed.");
		}
		folder.setAttribute(Constants.ATTR_NAME, newName, em);

		// Update last modified information
		if (userID != null) {
			DateTime now = DateTime.now();
			folder.setAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), em);
			folder.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, em);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public FolderDTO getFolderByID(String folderID, boolean lazyRelatives, boolean findPath) {
		return ConverterUtil.nodeToFolderDTO(Node.findFolder(folderID, em), lazyRelatives, findPath);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public byte[] getFolderAsZip(String folderID, boolean includeProperties, boolean isDeep)
			throws QFileNotFoundException {
		byte[] retVal = null;
		Node folder = Node.findFolder(folderID, em);
		if (folder == null) {
			throw new QFileNotFoundException("The folder you want to download does not exist");
		}

		String nodeName = folder.getAttribute(Constants.ATTR_NAME).getValue();

		boolean hasEntries = false;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(outStream);

		try {
			for (Node child : folder.getChildren()) {
				if (child.getType() == NodeType.FILE) {
					byte[] fileRetVal = versionService.getFileAsZip(child.getId(), includeProperties);
					if (fileRetVal != null) {
						hasEntries = true;
						ZipEntry entry = new ZipEntry(child.getAttribute(Constants.ATTR_NAME).getValue() + ".zip");
						out.putNextEntry(entry);
						out.write(fileRetVal, 0, fileRetVal.length);
					}
				} else if ((child.getType() == NodeType.FOLDER) && isDeep) {
					byte[] folderRetVal = getFolderAsZip(child.getId(), includeProperties, isDeep);
					if (folderRetVal != null) {
						hasEntries = true;
						ZipEntry entry = new ZipEntry(child.getAttribute(Constants.ATTR_NAME).getValue() + ".zip");
						out.putNextEntry(entry);
						out.write(folderRetVal, 0, folderRetVal.length);
					}
				}
			}

			if (includeProperties) {
				hasEntries = true;
				ZipEntry entry = new ZipEntry(nodeName + ".properties");
				out.putNextEntry(entry);
				StringBuilder buf = new StringBuilder();
				// Include a created on property
				buf.append(Constants.CREATED_ON).append(" = ").append(folder.getCreatedOn()).append("\n");
				for (NodeAttribute attribute : folder.getAttributes()) {
					buf.append(attribute.getName());
					buf.append(" = ");
					buf.append(attribute.getValue());
					buf.append("\n");
				}
				out.write(buf.toString().getBytes());
			}
			if (hasEntries) {
				out.close();
				retVal = outStream.toByteArray();
			}
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QIOException("Error writing ZIP for folder  with ID " + folderID);
		}

		return retVal;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String createFile(FileDTO file, String userID, String lockToken) throws QNodeLockException {
		Node parent = null;
		if (file.getParentId() != null) {
			parent = Node.findFolder(file.getParentId(), em);
		}

		// Check for ancestor node (folder) lock conflicts.
		if (parent != null) {
			NodeDTO ancConflict = concurrencyControlService.getAncestorFolderWithLockConflict(parent.getId(),
					lockToken);
			// In case a conflict was found an exception is thrown
			if (ancConflict != null && ancConflict.getId() != null) {
				throw new QAncestorFolderLockException(
						"An ancestor folder is locked" + " and an"
								+ " invalid lock token was passed; the file cannot be created.",
						ancConflict.getId(), ancConflict.getName());
			}
		}

		Node fileEntity = ConverterUtil.nodeDTOToNode(file);
		fileEntity.setParent(parent);

		//
		fileEntity.setMimetype(file.getMimetype());
		fileEntity.setSize(file.getSize());

		// Set created / last modified information
		DateTime now = DateTime.now();
		fileEntity.setCreatedOn(now.getMillis());
		fileEntity.getAttributes().add(new NodeAttribute(Constants.ATTR_CREATED_BY, userID, fileEntity));
		fileEntity.getAttributes()
				.add(new NodeAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), fileEntity));
		fileEntity.getAttributes().add(new NodeAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, fileEntity));
		em.persist(fileEntity);
		return fileEntity.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public CreateFileAndVersionStatusDTO createFileAndVersion(FileDTO file, VersionDTO cmVersion, byte[] content,
			String userID, String lockToken) throws QNodeLockException, QFileNotFoundException {
		// Prepare the returnVal
		CreateFileAndVersionStatusDTO status = new CreateFileAndVersionStatusDTO();
		String newFileID = this.createFile(file, userID, lockToken);
		status.setFileID(newFileID);
		// Create a new Version for the specific file
		status.setVersionID(
				versionService.createVersion(newFileID, cmVersion, file.getName(), content, userID, lockToken));
		em.flush();
		return status;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteFile(String fileID, String lockToken) throws QNodeLockException, QFileNotFoundException {
		Node node = Node.findFile(fileID, em);
		if (node == null) {
			throw new QFileNotFoundException("The file to delete does not exist");
		}
		// Check whether there is a lock conflict with the current node.
		NodeDTO selConflict = concurrencyControlService.getSelectedNodeWithLockConflict(fileID, lockToken);
		if (selConflict != null && selConflict.getName() != null) {
			throw new QSelectedNodeLockException(
					"The selected file is locked" + " and an"
							+ " invalid lock token was passed; the file cannot be deleted.",
					selConflict.getId(), selConflict.getName());
		}

		// Check for ancestor node (folder) lock conflicts.
		if (node.getParent() != null) {
			NodeDTO ancConflict = concurrencyControlService.getAncestorFolderWithLockConflict(node.getParent().getId(),
					lockToken);
			// In case a conflict was found an exception is thrown
			if (ancConflict != null && ancConflict.getId() != null) {
				throw new QAncestorFolderLockException(
						"An ancestor folder is locked" + " and an"
								+ " invalid lock token was passed; the file cannot be deleted.",
						ancConflict.getId(), ancConflict.getName());
			}
		}
		em.remove(node);
		em.flush();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void renameFile(String fileID, String newName, String userID, String lockToken)
			throws QNodeLockException, QFileNotFoundException {
		Node file = Node.findFile(fileID, em);
		if (file == null) {
			throw new QFileNotFoundException("The file to rename does not exist.");
		}

		if ((file.getLockToken() != null) && (!file.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("File with ID " + fileID + " is locked and an "
					+ "invalid lock token was passed; the file cannot be renamed.");
		}
		file.setAttribute(Constants.ATTR_NAME, newName, em);

		// Update last modified information
		if (userID != null) {
			DateTime now = DateTime.now();
			file.setAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), em);
			file.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, em);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public FileDTO getFileByID(String fileID, boolean includeVersions, boolean findPath) {
		FileDTO retVal = ConverterUtil.nodeToFileDTO(Node.findFile(fileID, em), false, findPath);
		if (includeVersions) {
			retVal.setVersions(versionService.getFileVersions(fileID));
		}

		return retVal;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public NodeDTO getNodeByID(String nodeID) {
		return ConverterUtil.nodeToNodeDTO(Node.findNode(nodeID, em));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public FolderDTO getParent(String nodeID, boolean lazyRelatives) {
		Node node = Node.findNode(nodeID, em);
		return ConverterUtil.nodeToFolderDTO(node.getParent(), lazyRelatives, false);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<FolderDTO> getAncestors(String nodeID) {
		List<FolderDTO> retVal = null;
		Node node = Node.findNode(nodeID, em);
		if (node.getParent() == null) {
			return new ArrayList<>();
		} else {
			retVal = getAncestors(node.getParent().getId());
		}
		retVal.add(ConverterUtil.nodeToFolderDTO(node.getParent(), true, false));
		return retVal;

	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String createAttribute(String nodeId, String attributeName, String attributeValue, String userId,
			String lockToken) throws QNodeLockException, QFileNotFoundException {

		Node node = Node.findNode(nodeId, em);
		if (node == null) {
			throw new QFileNotFoundException("The node, which attribute should be created does not exist.");
		}

		if ((node.getLockToken() != null) && (!node.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("Node with ID " + nodeId + " is locked and an "
					+ "invalid lock token was passed; the file attributes cannot be updated.");
		}

		NodeAttribute attribute =  new NodeAttribute(attributeName, attributeValue, node);
		
		node.getAttributes().add(attribute);

		// Set created / last modified information
		if (userId != null) {
			DateTime now = DateTime.now();
			node.setCreatedOn(now.getMillis());
			node.setAttribute(Constants.ATTR_CREATED_BY, userId, em);
			node.setAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), em);
			node.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userId, em);
		}

		return attribute.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateAttribute(String nodeID, String attributeName, String attributeValue, String userID,
			String lockToken) throws QNodeLockException, QFileNotFoundException {
		Node node = Node.findNode(nodeID, em);
		if (node == null) {
			throw new QFileNotFoundException("The node, which attribute should be updated does not exist.");
		}

		if ((node.getLockToken() != null) && (!node.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("Node with ID " + nodeID + " is locked and an "
					+ "invalid lock token was passed; the file attributes cannot be updated.");
		}
		node.setAttribute(attributeName, attributeValue, em);

		// Update last modified information
		if (userID != null) {
			DateTime now = DateTime.now();
			node.setAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), em);
			node.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, em);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateAttributes(String nodeID, Map<String, String> attributes, String userID, String lockToken)
			throws QNodeLockException, QFileNotFoundException {
		Node node = Node.findNode(nodeID, em);
		if (node == null) {
			throw new QFileNotFoundException("The node to update does not exist.");
		}

		if ((node.getLockToken() != null) && (!node.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("Node with ID " + nodeID + " is locked and an "
					+ "invalid lock token was passed; the file attributes cannot be updated.");
		}

		for (String attributeName : attributes.keySet()) {
			node.setAttribute(attributeName, attributes.get(attributeName), em);
		}

		// Update last modified information
		if (userID != null) {
			DateTime now = DateTime.now();
			node.setAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), em);
			node.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, em);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteAttribute(String nodeID, String attributeName, String userID, String lockToken)
			throws QNodeLockException, QFileNotFoundException {
		Node node = Node.findNode(nodeID, em);
		if (node == null) {
			throw new QFileNotFoundException("The node, which attributes should be deleted does not exist.");
		}

		if ((node.getLockToken() != null) && (!node.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("Node with ID " + nodeID + " is locked and an "
					+ "invalid lock token was passed; the file attributes cannot be deleted.");
		}
		node.removeAttribute(attributeName, em);

		// Update last modified information
		if (userID != null) {
			DateTime now = DateTime.now();
			node.setAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), em);
			node.setAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, em);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String copy(String nodeID, String newParentID, String userID, String lockToken)
			throws QFileNotFoundException {
		Node node = Node.findNode(nodeID, em);
		if (node == null) {
			throw new QFileNotFoundException("The node, to be copied does not exist.");
		}

		Node newParent = Node.findFolder(newParentID, em);

		if ((newParent.getLockToken() != null) && (!newParent.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("Node with ID " + newParentID + " is locked and an "
					+ "invalid lock token was passed; a new node cannot be copied into it.");
		}

		checkCyclicPath(nodeID, newParent);

		return copyNode(node, newParent, userID);
	}

	private String copyNode(Node node, Node newParent, String userID) {
		Node newNode = new Node();
		newNode.setType(node.getType());
		newNode.setParent(newParent);
		List<NodeAttribute> newAttributes = new ArrayList<NodeAttribute>();
		newNode.setAttributes(newAttributes);

		// Copy attributes except created/modified/locked information
		for (NodeAttribute attribute : node.getAttributes()) {
			switch (attribute.getName()) {
			case Constants.ATTR_CREATED_BY:
			case Constants.ATTR_LAST_MODIFIED_BY:
			case Constants.ATTR_LAST_MODIFIED_ON:
			case Constants.ATTR_LOCKED_BY:
			case Constants.ATTR_LOCKED_ON:
				break;
			default:
				newNode.getAttributes().add(new NodeAttribute(attribute.getName(), attribute.getValue(), newNode));
				break;

			}
		}

		// Set created / last modified information
		DateTime now = DateTime.now();
		newNode.setCreatedOn(now.getMillis());
		newNode.getAttributes().add(new NodeAttribute(Constants.ATTR_CREATED_BY, userID, newNode));
		newNode.getAttributes()
				.add(new NodeAttribute(Constants.ATTR_LAST_MODIFIED_ON, String.valueOf(now.getMillis()), newNode));
		newNode.getAttributes().add(new NodeAttribute(Constants.ATTR_LAST_MODIFIED_BY, userID, newNode));
		em.persist(newNode);

		for (Node child : node.getChildren()) {
			copyNode(child, newNode, userID);
		}

		return newNode.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void move(String nodeID, String newParentID, String userID, String lockToken) throws QFileNotFoundException {
		Node node = Node.findNode(nodeID, em);
		if (node == null) {
			throw new QFileNotFoundException("The node, to be moved does not exist.");
		}

		Node newParent = Node.findFolder(newParentID, em);

		if ((node.getLockToken() != null) && (!node.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("Node with ID " + nodeID + " is locked and an "
					+ "invalid lock token was passed; it cannot be moved.");
		}
		if ((newParent.getLockToken() != null) && (!newParent.getLockToken().equals(lockToken))) {
			throw new QNodeLockException("Node with ID " + newParentID + " is locked and an "
					+ "invalid lock token was passed; a new node cannot be moved into it.");
		}

		checkCyclicPath(nodeID, newParent);

		node.setParent(newParent);
	}

	private void checkCyclicPath(String nodeID, Node newParent) {
		Node checkedNode = newParent;
		while (checkedNode != null) {
			if (checkedNode.getId().equals(nodeID)) {
				throw new QInvalidPathException("Cannot move node with ID " + nodeID + " under node with ID "
						+ newParent.getId() + " since this will create a cyclic path.");
			}
			checkedNode = checkedNode.getParent();
		}
	}

	@Override
	public boolean isFileNameUnique(String name, String parentNodeID) {
		QNode qNode = QNode.node;
		QNodeAttribute qNodeAttribute = new QNodeAttribute("nodeAttribute");
		boolean isFileNameUnique = true;
		// Query retrieving the nodes (folder/file) which have a required
		// node attribute name.
		JPAQuery<Node> q = new JPAQueryFactory(em).selectFrom(qNode).innerJoin(qNode.attributes, qNodeAttribute)
				.where(qNode.parent.id.eq(parentNodeID).and(qNodeAttribute.name.eq(Constants.ATTR_NAME))
						.and(qNodeAttribute.value.eq(name)));
		// Get the count of nodes which the specific name in odrer to find out
		// whether the name is not unique
		long count = q.fetchCount();
		// In case the number is larger than zero it mean that the file name
		// already exist
		if (count > 0) {
			isFileNameUnique = false;
		}
		return isFileNameUnique;
	}

	@Override
	public List<String> duplicateFileNamesInDirectory(List<String> fileNames, String parentId) {
		QNode qNode = QNode.node;
		QNodeAttribute qNodeAttribute = new QNodeAttribute("nodeAttribute");
		// Selects all the nodes that their name is contained in a list of
		// strings
		JPAQuery<Node> q = new JPAQueryFactory(em).selectFrom(qNode).innerJoin(qNode.attributes, qNodeAttribute)
				.where(qNode.parent.id.eq(parentId).and(qNodeAttribute.name.eq(Constants.ATTR_NAME))
						.and(qNodeAttribute.value.in(fileNames)));

		List<Node> nodeResultList = q.fetchResults().getResults();
		List<String> namesList = new ArrayList<String>();
		// TODO Future enhancement
		for (Node node : nodeResultList) {
			NodeDTO nodeDTO = ConverterUtil.nodeToNodeDTO(node);
			namesList.add(nodeDTO.getName());
		}

		return namesList;
	}

}

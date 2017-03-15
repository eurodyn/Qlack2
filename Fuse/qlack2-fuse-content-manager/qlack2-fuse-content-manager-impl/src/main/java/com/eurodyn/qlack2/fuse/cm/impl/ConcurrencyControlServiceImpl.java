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

import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.joda.time.DateTime;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.cm.api.ConcurrencyControlService;
import com.eurodyn.qlack2.fuse.cm.api.dto.FolderDTO;
import com.eurodyn.qlack2.fuse.cm.api.dto.NodeDTO;
import com.eurodyn.qlack2.fuse.cm.api.exception.QAncestorFolderLockException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QDescendantNodeLockException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QFileNotFoundException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QNodeLockException;
import com.eurodyn.qlack2.fuse.cm.api.exception.QSelectedNodeLockException;
import com.eurodyn.qlack2.fuse.cm.api.util.CMConstants;
import com.eurodyn.qlack2.fuse.cm.impl.model.Node;
import com.eurodyn.qlack2.fuse.cm.impl.model.NodeType;
import com.eurodyn.qlack2.fuse.cm.impl.util.ConverterUtil;

@Transactional
@Singleton
@OsgiServiceProvider(classes = {ConcurrencyControlService.class})
public class ConcurrencyControlServiceImpl implements ConcurrencyControlService {
	private static final Logger LOGGER = Logger.getLogger(ConcurrencyControlServiceImpl.class.getName());
	
	@PersistenceContext(unitName = "fuse-contentmanager")
	private EntityManager em;
	
	@Override
	@Transactional(TxType.REQUIRED)
	public void lock(String nodeID, String lockToken, boolean lockChildren,
			String userID) throws QNodeLockException, QFileNotFoundException {
		Node node = Node.findNode(nodeID, em);
		if (node == null) {
			throw new QFileNotFoundException("The folder/file you want to lock does not exist");
		}
		// Check whether there is a lock conflict with the current node.
		NodeDTO selConflict = this.getSelectedNodeWithLockConflict(nodeID,
				lockToken);
		if (selConflict != null && selConflict.getName() != null) {
			throw new QSelectedNodeLockException(
					"The selected folder is locked"
							+ " and an"
							+ " invalid lock token was passed; the folder cannot be locked.",
					selConflict.getId(), selConflict.getName());
		}

		// Check for ancestor node (folder) lock conflicts.
		if (node.getParent() != null) {
			NodeDTO ancConflict = this.getAncestorFolderWithLockConflict(node
					.getParent().getId(), lockToken);
			// In case a conflict was found an exception is thrown
			if (ancConflict != null && ancConflict.getId() != null) {
				throw new QAncestorFolderLockException(
						"An ancestor folder is locked"
								+ " and an"
								+ " invalid lock token was passed; the folder cannot be locked.",
						ancConflict.getId(), ancConflict.getName());
			}
		}

		// Check for descendant node lock conflicts
		NodeDTO desConflict = this.getDescendantNodeWithLockConflict(nodeID,
				lockToken);
		// In case a conflict was found an exception is thrown
		if (desConflict != null && desConflict.getId() != null) {
			throw new QDescendantNodeLockException(
					"An descendant node is locked"
							+ " and an"
							+ " invalid lock token was passed; the folder cannot be locked.", desConflict.getId(), desConflict.getName());
		}
		node.setLockToken(lockToken);
		node.setAttribute(CMConstants.ATTR_LOCKED_ON,
				String.valueOf(DateTime.now().getMillis()), em);
		node.setAttribute(CMConstants.ATTR_LOCKED_BY, userID, em);
		// In case the lockChilden variable is true all the children of the
		// folder will recursively be locked
		if (lockChildren) {
			for (Node child : node.getChildren()) {
				lock(child.getId(), lockToken, true, userID);
			}
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void unlock(String nodeID, String lockToken, boolean overrideLock,
			String userID) throws QNodeLockException, QFileNotFoundException  {
		Node node = Node.findNode(nodeID, em);
		if (node == null) {
			throw new QFileNotFoundException("The folder/file you want to unlock does not exist");
		}
		
		// Check whether there is a lock conflict with the current node.
		NodeDTO selConflict = this.getSelectedNodeWithLockConflict(nodeID,
				lockToken);
		if (selConflict !=null && selConflict.getName() != null) {
			throw new QSelectedNodeLockException(
					"The selected folder is locked"
							+ " and an"
							+ " invalid lock token was passed; the folder cannot be unlocked.", selConflict.getId(), selConflict.getName());
		}
		node.setLockToken(null);
		// remove the attribute itself
		node.removeAttribute(CMConstants.ATTR_LOCKED_ON, em);
		node.removeAttribute(CMConstants.ATTR_LOCKED_BY, em);

		// In case an override lock is requested the node will be locked again
		// using the
		// provided lock token
		if (overrideLock) {
			this.lock(nodeID, lockToken, false, userID);
		}

	}
	
	@Override
	@Transactional(TxType.SUPPORTS)
	public FolderDTO getAncestorFolderWithLockConflict(String nodeID,
			String lockToken) {
		// Find the current node.
		Node node = Node.findNode(nodeID, em);
		FolderDTO folderWithConflict = null;

		// Check whether the current node has lock and is conflicting.
		// Returns the first folder it will find with conflict.
		if (node.getLockToken() != null
				&& !node.getLockToken().equals(lockToken)) {
			folderWithConflict = ConverterUtil.nodeToFolderDTO(node, true, false);
			return folderWithConflict;
		}

		// In case the current node does not have conflict but it does have
		// parents call the recursive function again.
		else if (node.getParent() != null) {
			return getAncestorFolderWithLockConflict(node.getParent().getId(),
					lockToken);
		} else {
			return null;
		}
	}
	
	@Override
	@Transactional(TxType.SUPPORTS)
	public NodeDTO getDescendantNodeWithLockConflict(String nodeID, String lockToken) {
		// Find the current node.
		Node node = Node.findNode(nodeID, em);
		NodeDTO nodeWithConflict = null;
		for (Node child : node.getChildren()) {
			// Check whether the current node has a conflicting lock.
			// Returns the first node it will find with conflict.
			if (child.getLockToken() != null
					&& !child.getLockToken().equals(lockToken)) {
				if (node.getType().equals(NodeType.FOLDER)) {
					nodeWithConflict = ConverterUtil
							.nodeToFolderDTO(node, true, false);
				} else if (node.getType().equals(NodeType.FILE)) {
					nodeWithConflict = ConverterUtil.nodeToFileDTO(node, true, false);
				}
				return nodeWithConflict;
			} else {
				// Recursively look for conflicts with descendant nodes.
				return getDescendantNodeWithLockConflict(child.getId(), lockToken);
			}
		}
		return nodeWithConflict;
	}

	@Override
	@Transactional(TxType.SUPPORTS)
	public NodeDTO getSelectedNodeWithLockConflict(String nodeID, String lockToken) {
		Node node = Node.findNode(nodeID, em);
		
		NodeDTO nodeWithConflict = null;
		// Check whether the node is locked and the provided lock token is different,
		//leading to a conflict.
		if (node.getLockToken()!= null && !node.getLockToken().equals(lockToken)) {
			if (node.getType().equals(NodeType.FOLDER)) {
				nodeWithConflict = ConverterUtil
						.nodeToFolderDTO(node, true, false);
			} else if (node.getType().equals(NodeType.FILE)) {
				nodeWithConflict = ConverterUtil.nodeToFileDTO(node, true, false);
			}
		}
		return nodeWithConflict;
	}
	
	

}

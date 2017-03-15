package com.eurodyn.qlack2.be.rules.impl.util;

import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelVersion;
import com.eurodyn.qlack2.be.rules.impl.model.LibraryVersion;
import com.eurodyn.qlack2.be.rules.impl.model.RuleVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;

public class VersionStateUtils {

	// -- Working Sets

	public void checkWorkingSetVersionNotFinalized(WorkingSetVersion version) throws QInvalidActionException {
		if (version.getState() == VersionState.FINAL) {
			throw new QInvalidActionException("Version is finalized.");
		}
	}

	public void checkCanModifyWorkingSetVersion(String userId, WorkingSetVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			return;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return;
			}
			else {
				// locked by other user
				throw new QInvalidActionException("Version is locked by other user.");
			}
		}
	}

	public void checkCanLockWorkingSetVersion(String userId, WorkingSetVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			return;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				throw new QInvalidActionException("Version is already locked by current user.");
			}
			else {
				// locked by other user
				throw new QInvalidActionException("Version is locked by other user.");
			}
		}
	}

	public void checkCanUnlockWorkingSetVersion(String userId, boolean canUnlockAny, WorkingSetVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			throw new QInvalidActionException("Version is unlocked.");
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return;
			}
			else {
				// locked by other user
				if (canUnlockAny) {
					return;
				}
				else {
					throw new QInvalidActionException("Version is locked by other user.");
				}
			}
		}
	}

	// -- Rules

	public void checkRuleVersionNotFinalized(RuleVersion version) throws QInvalidActionException {
		if (version.getState() == VersionState.FINAL) {
			throw new QInvalidActionException("Version is finalized.");
		}
	}

	public void checkCanModifyRule(String userId, List<RuleVersion> versions) throws QInvalidActionException {
		for (RuleVersion version : versions) {
			checkCanModifyRuleVersion(userId, version);
		}
	}

	public boolean canModifyRuleVersion(String userId, RuleVersion version) {
		if (version.getLockedBy() == null) {
			// unlocked
			return true;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return true;
			}
			else {
				// locked by other user
				return false;
			}
		}
	}

	public void checkCanModifyRuleVersion(String userId, RuleVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			return;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return;
			}
			else {
				// locked by other user
				throw new QInvalidActionException("Version is locked by other user.");
			}
		}
	}

	public void checkCanLockRuleVersion(String userId, RuleVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			return;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				throw new QInvalidActionException("Version is already locked by current user.");
			}
			else {
				// locked by other user
				throw new QInvalidActionException("Version is locked by other user.");
			}
		}
	}

	public void checkCanUnlockRuleVersion(String userId, boolean canUnlockAny, RuleVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			throw new QInvalidActionException("Version is unlocked.");
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return;
			}
			else {
				// locked by other user
				if (canUnlockAny) {
					return;
				}
				else {
					throw new QInvalidActionException("Version is locked by other user.");
				}
			}
		}
	}

	// -- Data Models

	public void checkDataModelVersionNotFinalized(DataModelVersion version) throws QInvalidActionException {
		if (version.getState() == VersionState.FINAL) {
			throw new QInvalidActionException("Version is finalized.");
		}
	}

	public void checkCanModifyDataModel(String userId, List<DataModelVersion> versions) throws QInvalidActionException {
		for (DataModelVersion version : versions) {
			checkCanModifyDataModelVersion(userId, version);
		}
	}

	public boolean canModifyDataModelVersion(String userId, DataModelVersion version) {
		if (version.getLockedBy() == null) {
			// unlocked
			return true;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return true;
			}
			else {
				// locked by other user
				return false;
			}
		}
	}

	public void checkCanModifyDataModelVersion(String userId, DataModelVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			return;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return;
			}
			else {
				// locked by other user
				throw new QInvalidActionException("Version is locked by other user.");
			}
		}
	}

	public void checkCanLockDataModelVersion(String userId, DataModelVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			return;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				throw new QInvalidActionException("Version is already locked by current user.");
			}
			else {
				// locked by other user
				throw new QInvalidActionException("Version is locked by other user.");
			}
		}
	}

	public void checkCanUnlockDataModelVersion(String userId, boolean canUnlockAny, DataModelVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			throw new QInvalidActionException("Version is unlocked.");
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return;
			}
			else {
				// locked by other user
				if (canUnlockAny) {
					return;
				}
				else {
					throw new QInvalidActionException("Version is locked by other user.");
				}
			}
		}
	}

	// -- Libraries

	public void checkLibraryVersionNotFinalized(LibraryVersion version) throws QInvalidActionException {
		if (version.getState() == VersionState.FINAL) {
			throw new QInvalidActionException("Version is finalized.");
		}
	}

	public boolean canModifyLibraryVersion(String userId, LibraryVersion version) {
		if (version.getLockedBy() == null) {
			// unlocked
			return true;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return true;
			}
			else {
				// locked by other user
				return false;
			}
		}
	}

	public void checkCanModifyLibraryVersion(String userId, LibraryVersion version) throws QInvalidActionException {
		if (version.getLockedBy() == null) {
			// unlocked
			return;
		}
		else {
			if (version.getLockedBy().equals(userId)) {
				// locked by current user
				return;
			}
			else {
				// locked by other user
				throw new QInvalidActionException("Version is locked by other user.");
			}
		}
	}

}

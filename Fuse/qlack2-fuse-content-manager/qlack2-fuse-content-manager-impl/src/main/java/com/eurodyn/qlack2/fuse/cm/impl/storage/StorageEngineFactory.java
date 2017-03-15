package com.eurodyn.qlack2.fuse.cm.impl.storage;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.springframework.beans.factory.annotation.Value;

import com.eurodyn.qlack2.fuse.cm.api.storage.StorageEngine;
import com.eurodyn.qlack2.fuse.cm.api.storage.StorageEngineType;

@Singleton
public class StorageEngineFactory {
	@Inject
	private DBStorage dbStorage;

	@Inject
	private FSStorage fsStorage;

	@Value("${storageStrategy}")
	private String defaultStorageStrategy;

	/**
	 * @param dbStorage
	 *            the dbStorage to set
	 */
	public void setDbStorage(DBStorage dbStorage) {
		this.dbStorage = dbStorage;
	}

	/**
	 * @param fsStorage
	 *            the fsStorage to set
	 */
	public void setFsStorage(FSStorage fsStorage) {
		this.fsStorage = fsStorage;
	}

	public StorageEngine getEngine(StorageEngineType type) {
		switch (type) {
		case DBStorage:
			return dbStorage;
		case FSStorage:
			return fsStorage;
		default:
			return dbStorage;
		}
	}

	public StorageEngine getEngine() {
		return getEngine(StorageEngineType.valueOf(defaultStorageStrategy));
	}
}

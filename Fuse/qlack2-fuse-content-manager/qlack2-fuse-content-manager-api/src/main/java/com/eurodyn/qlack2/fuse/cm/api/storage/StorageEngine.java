package com.eurodyn.qlack2.fuse.cm.api.storage;

import java.io.IOException;

import com.eurodyn.qlack2.fuse.cm.api.dto.BinChunkDTO;

public interface StorageEngine {
	void setVersionContent(String versionID, byte[] content);

	byte[] getVersionContent(String versionID) throws IOException;

	String setBinChunk(String versionID, byte[] content, int chunkIndex);

	BinChunkDTO getBinChunk(String versionID, int chunkIndex);

	boolean deleteVersion(String versionID);

}

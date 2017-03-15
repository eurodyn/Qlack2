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
package com.eurodyn.qlack2.fuse.fileupload.api;

import com.eurodyn.qlack2.fuse.fileupload.api.request.CheckChunkRequest;
import com.eurodyn.qlack2.fuse.fileupload.api.request.FileUploadRequest;
import com.eurodyn.qlack2.fuse.fileupload.api.request.VirusScanRequest;
import com.eurodyn.qlack2.fuse.fileupload.api.response.CheckChunkResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.response.ChunkGetResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileDeleteResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileListResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileUploadResponse;
import com.eurodyn.qlack2.fuse.fileupload.api.response.VirusScanResponse;

public interface FileUpload {
	CheckChunkResponse checkChunk(CheckChunkRequest req);

	FileUploadResponse upload(FileUploadRequest req);

	FileDeleteResponse deleteByID(String fileID);

	FileGetResponse getByID(String fileID);
	/**
	 * Retrieves a specific chunk of a required file
	 * @param fileID the ID of the file from which a chunk will be retrieved
	 * @param chunkNbr The number of the chunk
	 * @return ChunkGetResponse The response which will contain the retrieved chunk
	 * */
	ChunkGetResponse getByIDAndChunk(String fileID, long chunkNbr);

	FileListResponse listFiles(boolean includeBinaryContent);

	VirusScanResponse virusScan(VirusScanRequest req);

	/**
	 * Cleans up file-chunks which have been uploaded but never
	 * reclaimed/deleted.
	 * 
	 * @param deleteBefore
	 *            The EPOCH before which all files get deleted.
	 */
	void cleanupExpired(long deleteBefore);

	FileDeleteResponse deleteByIDForConsole(String fileID);

	FileGetResponse getByIDForConsole(String fileID);

	FileListResponse listFilesForConsole(boolean includeBinary);
}

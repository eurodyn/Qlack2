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
package com.eurodyn.qlack2.fuse.fileupload.rest;

import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.request.CheckChunkRequest;
import com.eurodyn.qlack2.fuse.fileupload.api.request.FileUploadRequest;
import com.eurodyn.qlack2.fuse.fileupload.api.response.CheckChunkResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FileUploadRestTemplate {

  private static final Logger LOGGER = Logger.getLogger(FileUploadRestTemplate.class.getName());

  private byte[] getBin(String fieldName, MultipartBody body)
    throws IOException {
    Attachment attachment = body.getAttachment(fieldName);
    if (attachment != null) {
      return IOUtils.readBytesFromStream(attachment.getDataHandler().getInputStream());
    } else {
      return null;
    }
  }

  private String getString(String fieldName, MultipartBody body)
    throws IOException {
    Attachment attachment = body.getAttachment(fieldName);
    if (attachment != null) {
      return IOUtils.toString(attachment.getDataHandler()
        .getInputStream());
    } else {
      return null;
    }
  }

  private Long getLong(String fieldName, MultipartBody body)
    throws IOException {
    Attachment attachment = body.getAttachment(fieldName);
    if (attachment != null) {
      return Long.valueOf(IOUtils.toString(attachment.getDataHandler().getInputStream()));
    } else {
      return null;
    }
  }

  public Response checkChunk(FileUpload fileUpload, long chunkNumber, long chunkSize,
    long totalSize, String alias, String filename, long totalChunks) {
    CheckChunkRequest req = new CheckChunkRequest();
    req.setChunkNumber(chunkNumber);
    req.setFileAlias(alias);
    CheckChunkResponse res = fileUpload.checkChunk(req);

    if (!res.isChunkExists()) {
      return Response.status(Status.NO_CONTENT).build();
    } else {
      return Response.ok().build();
    }
  }

  public String upload(FileUpload fileUpload, MultipartBody body) {
    String retVal = "";

    try {
      FileUploadRequest fur = new FileUploadRequest();
      fur.setAlias(getString("flowIdentifier", body));
      fur.setAutoDelete(true);
      if (body.getAttachment("flowChunkNumber") != null) {
        fur.setChunkNumber(getLong("flowChunkNumber", body).longValue());
      } else {
        fur.setChunkNumber(1); // Support for older browsers, where there is always one chunk.
      }
      if (body.getAttachment("flowChunkSize") != null) {
        fur.setChunkSize(getLong("flowChunkSize", body).longValue());
      }
      fur.setFilename(getString("flowFilename", body));
      if (body.getAttachment("flowTotalChunks") != null) {
        fur.setTotalChunks(getLong("flowTotalChunks", body).longValue());
      } else {
        fur.setTotalChunks(1); // Support for older browsers, where there is always one chunk.
      }
      if (body.getAttachment("flowTotalSize") != null) {
        fur.setTotalSize(getLong("flowTotalSize", body).longValue());
      }

      fur.setData(getBin("file", body));

      fileUpload.upload(fur);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not process file upload.", e);
      retVal = "ERROR";
    }

    return retVal;
  }

}

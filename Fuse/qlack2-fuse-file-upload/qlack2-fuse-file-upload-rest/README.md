These are sample REST endpoints that you can re-use in your application
in order to interface with the file upload component. We intentionally
do not annotate these endpoints with JAXWS-RS annotations, as you should
create your own, application-specific endpoints wrapping the endpoints
provided here in order to inject your security mechanism first.

Your application's wrapper could extend `FileUploadRestTemplate` and
be similar to:

```
  @GET
  @Path("/upload")
  @Produces(MediaType.APPLICATION_JSON)
  public Response checkChunk(
      @QueryParam("flowChunkNumber") long chunkNumber,
      @QueryParam("flowCurrentChunkSize") long chunkSize,
      @QueryParam("flowTotalSize") long totalSize,
      @QueryParam("flowIdentifier") String alias,
      @QueryParam("flowFilename") String filename,
      @QueryParam("flowTotalChunks") long totalChunks) {
    return super.checkChunk(fileUpload, chunkNumber, chunkSize, chunkSize, alias, filename, totalChunks);
  }

  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_HTML)
  /**
   * Uploads a chunk of the to be uploaded file.
   * @param body
   * @param headers
   * @return
   */
  public String upload(MultipartBody body, @Context HttpHeaders headers) {
    return super.upload(fileUpload, body);
  }
```

Please note that before calling the methods from your superclass, you should
perform any kind of security checks your application requires. Otherwise,
you are allowing unrestricted, unauthenticated access via your REST API
to anyone to upload content.
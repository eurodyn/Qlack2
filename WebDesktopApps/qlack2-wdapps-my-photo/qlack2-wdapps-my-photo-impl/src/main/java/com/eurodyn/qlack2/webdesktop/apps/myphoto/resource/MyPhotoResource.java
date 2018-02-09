package com.eurodyn.qlack2.webdesktop.apps.myphoto.resource;

import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
import com.eurodyn.qlack2.fuse.imaging.api.ImagingService;
import com.eurodyn.qlack2.fuse.imaging.api.util.ResamplingAlgorithm;
import com.eurodyn.qlack2.webdesktop.apps.myphoto.api.Constants;
import com.eurodyn.qlack2.webdesktop.apps.myphoto.api.MyPhotoService;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.ops4j.pax.cdi.api.OsgiService;

@Path("/myphoto")
@Singleton
public class MyPhotoResource {

  @Inject
  @OsgiService
  FileUpload fileUpload;

  @Inject
  MyPhotoService myPhotoService;

  @Inject
  @OsgiService
  private ImagingService imagingService;

  // Target format for image conversion.
  private static final String IMAGE_FORMAT = "PNG";

  @PUT
  public Response updatePhoto(@QueryParam("fileID") String fileID) {
    // Get the photo that was uploaded.
    FileGetResponse file = fileUpload.getByID(fileID);

    // Resize the logo and make it PNG.
    byte[] png = imagingService.convert(file.getFile().getFileData(), IMAGE_FORMAT);
    byte[] scaledPNG = imagingService.resample(png, Constants.PHOTO_WIDTH, Constants.PHOTO_HEIGHT,
      ResamplingAlgorithm.FILTER_CUBIC);

    myPhotoService.updateMyPhoto(scaledPNG);

    fileUpload.deleteByID(fileID);

    return Response.ok().build();
  }

  @GET
  @Produces("image/png")
  public Response getPhoto() {
    byte[] photo = myPhotoService.getMyPhoto();

    return Response.ok(photo).build();
  }
}

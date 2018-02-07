package com.eurodyn.qlack2.fuse.imaging.api;

import com.eurodyn.qlack2.fuse.imaging.api.dto.ImageFormatHandler;
import com.eurodyn.qlack2.fuse.imaging.api.dto.ImageInfo;
import com.eurodyn.qlack2.fuse.imaging.api.util.ICCProfile;
import com.eurodyn.qlack2.fuse.imaging.api.util.ResamplingAlgorithm;
import com.eurodyn.qlack2.fuse.imaging.api.util.TIFFCompression;

import java.util.List;

/**
 * Image manipulation functionality based on Java ImageIO. You should consider using the
 * `qlack2-util-repack-twelvemonkey` Karaf feature in order to support additional formats.
 */
public interface ImagingService {

  /**
   * Returns all supported image formats that can be read.
   *
   * @return The list of supported image formats that can be read.
   */
  List<ImageFormatHandler> getSupportedReadFormats();

  /**
   * Returns all supported image formats that can be written.
   *
   * @return The list of supported image formats that can be written.
   */
  List<ImageFormatHandler> getSupportedWriteFormats();

  /**
   * A convenient method to check if a format is supported for read.
   *
   * @param format The format to check.
   * @return Returns true if the format is supported or false otherwise.
   */
  boolean isFormatSupportedForRead(String format);

  /**
   * A convenient method to check if a format is supported for write.
   *
   * @param format The format to check.
   * @return Returns true if the format is supported or false otherwise.
   */
  boolean isFormatSupportedForWrite(String format);

  /**
   * Obtains various information about the image.
   *
   * @param image The image to check.
   * @return Returns the list of information for this image.
   */
  ImageInfo getInfo(byte[] image);

  /**
   * Removes the alpha channel from an image. Removing the alpha channel is necessary, so that
   * certain conversions can take place. For example, converting from a PNG to JPEG is not possible
   * unless the alpha channel in the PNG has been removed.
   *
   * @param image The image to remove the alpha channel.
   * @return Returns an image with the alpha channel removed in the original format of the image.
   */
  byte[] removeAlphaChannel(byte[] image);

  /**
   * Converts an image from one format to another, optionally changing the colorspace.
   * In order to make sure that the source image can be succesfully read as well as the target image
   * format can be successfully written, you should check prior to calling this method the
   * `getSupportedReadFormats` and `getSupportedWriteFormats` methods respecetively.
   * This is a generic converter which does not allow to specify target format-specific parameters.
   * If you need to tweak the conversion output based on attributes found only on the specific
   * target-format you should use the specialised converted methods (e.g. convertToTIFF, etc.).
   *
   * @param image The image to convert.
   * @param dstFormat A destination format as returned by getSupportedWriteFormats.
   * @param dstColorspace The destination colorspace using an ICC profile.
   * @return Returns the converted image.
   */
  byte[] convert(byte[] image, String dstFormat, ICCProfile dstColorspace);

  /**
   * Converts an image from one format to another.
   * In order to make sure that the source image can be succesfully read as well as the target image
   * format can be successfully written, you should check prior to calling this method the
   * `getSupportedReadFormats` and `getSupportedWriteFormats` methods respecetively.
   * This is a generic converter which does not allow to specify target format-specific parameters.
   * If you need to tweak the conversion output based on attributes found only on the specific
   * target-format you should use the specialised converted methods (e.g. convertToTIFF, etc.).
   *
   * @param image The image to convert.
   * @param dstFormat A destination format as returned by getSupportedWriteFormats.
   * @return Returns the converted image.
   */
  byte[] convert(byte[] image, String dstFormat);

  /**
   * A specilised version of the conversion functionality suitable for TIFF output, allowing to
   * set output parameters for the produced TIFF such as comperssion.
   *
   * @param image The image to convert.
   * @param tiffCompression The compression algorithm to use. Make sure you check these with your
   * JDK first.
   * @return Returns the converted image.
   */
  byte[] convertToTIFF(byte[] image, TIFFCompression tiffCompression);

  /**
   * A specilised version of the conversion functionality suitable for TIFF output, allowing to
   * set output parameters for the produced TIFF such as comperssion.
   *
   * @param image The image to convert.
   * @param dstColorspace The colorspace to use while converting.
   * @param tiffCompression The compression algorithm to use. Make sure you check these with your
   * JDK first.
   * @return Returns the converted image.
   */
  byte[] convertToTIFF(byte[] image, ICCProfile dstColorspace,
    TIFFCompression tiffCompression);

  /**
   * Resamples (scales) an image by a given percentage in both dimensions.
   *
   * @param image The image to scle.
   * @param percent The percent to scale by.
   * @param resamplingAlgorithm The resampling algorithm to use.
   * @return Returns the resampled image.
   */
  byte[] resampleByPercent(byte[] image, int percent,
    ResamplingAlgorithm resamplingAlgorithm);

  /**
   * Resamples (scales) an image by a given factor in both dimensions.
   *
   * @param image The image to scle.
   * @param factor The factor to scale by.
   * @param resamplingAlgorithm The resampling algorithm to use.
   * @return Returns the resampled image.
   */
  byte[] resampleByFactor(byte[] image, float factor,
    ResamplingAlgorithm resamplingAlgorithm);

  /**
   * Resamples (scales) an image to the given width while adjusting the height keeping the original
   * aspect ratio.
   *
   * @param image The image to scle.
   * @param width The target width.
   * @param resamplingAlgorithm The resampling algorithm to use.
   * @return Returns the resampled image.
   */
  byte[] resampleByWidth(byte[] image, int width, ResamplingAlgorithm resamplingAlgorithm);

  /**
   * Resamples (scales) an image to the given height while adjusting the width keeping the original
   * aspect ratio.
   *
   * @param image The image to scle.
   * @param height The target height.
   * @param resamplingAlgorithm The resampling algorithm to use.
   * @return Returns the resampled image.
   */
  byte[] resampleByHeight(byte[] image, int height,
    ResamplingAlgorithm resamplingAlgorithm);

  /**
   * Resamples (scales) an image to specific width and height.
   *
   * @param image The image to scle.
   * @param width The target width.
   * @param height The target height.
   * @param resamplingAlgorithm The resampling algorithm to use.
   * @return Returns the resampled image.
   */
  byte[] resample(byte[] image, int width, int height,
    ResamplingAlgorithm resamplingAlgorithm);
}

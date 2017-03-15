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
package com.eurodyn.qlack2.fuse.imaging.api;

import java.awt.Image;

import com.eurodyn.qlack2.fuse.imaging.api.dto.ImageInfo;
import com.eurodyn.qlack2.fuse.imaging.api.dto.OverlayTextDTO;
import com.eurodyn.qlack2.fuse.imaging.api.exception.QImageCannotBeRetrievedException;

/**
 * @author European Dynamics SA
 */
public interface ImageService {
	/**
	 * Returns information relative to a given image
	 *
	 * @param image
	 *            The image whose information to retrieve
	 * @return The image's information wrapped in an ImageInfo object
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public ImageInfo getImageInfo(byte[] image)
			throws QImageCannotBeRetrievedException;

	/**
	 * Converts an image to a different format
	 *
	 * @param image
	 *            The image to convert
	 * @param format
	 *            The format to convert the image to. The supported formats are
	 *            those defined in class ImageFormats.
	 * @return The converted image as a byte array.
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] convertImage(byte[] image, String format)
			throws QImageCannotBeRetrievedException;

	/**
	 * Scales an image by the same factor on x and y axes
	 *
	 * @param image
	 *            The image to scale
	 * @param scaleFactor
	 *            The factor to use for scaling the image (this factor will be
	 *            used for both the x and y axis)
	 * @return A byte array containing the scaled image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] scaleImage(byte[] image, float scaleFactor)
			throws QImageCannotBeRetrievedException;

	/**
	 * Scales an image by different factors on x and y axes. This method does
	 * not preserve the image's aspect ratio.
	 *
	 * @param image
	 *            The image to scale
	 * @param xFactor
	 *            The factor to use for scaling the image on the x axis
	 * @param yFactor
	 *            The factor to use for scaling the image on the y axis
	 * @return A byte array containing the scaled image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] scaleImage(byte[] image, float xFactor, float yFactor)
			throws QImageCannotBeRetrievedException;

	/**
	 * Scales an image by the same factor on x and y axes
	 *
	 * @param image
	 *            The image to scale
	 * @param width
	 *            The desired width of the image in pixels.
	 * @return A byte array containing the scaled image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] scaleImage(byte[] image, int width)
			throws QImageCannotBeRetrievedException;

	/**
	 * Scales an image by different factors on x and y axes. This method does
	 * not preserve the image's aspect ratio.
	 *
	 * @param image
	 *            The image to scale
	 * @param width
	 *            The desired width of the image in pixels.
	 * @param height
	 *            The desired height of the image in pixels.
	 * @return A byte array containing the scaled image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] scaleImage(byte[] image, int width, int height)
			throws QImageCannotBeRetrievedException;

	/**
	 * Inverts the colors of an image
	 *
	 * @param image
	 *            The image to invert
	 * @return A byte array containing the inverted image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] invertImage(byte[] image)
			throws QImageCannotBeRetrievedException;

	/**
	 * Crops an image
	 *
	 * @param image
	 *            The image to crop
	 * @param xCoordinate
	 *            The coordinate on the x axis where the crop will begin
	 * @param yCoordinate
	 *            The coordinate on the x axis where the crop will begin
	 * @param xSize
	 *            The size of the area to keep on the x axis
	 * @param ySize
	 *            The size of the area to keep on the y axis
	 * @return A byte array containing the cropped image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] cropImage(byte[] image, float xCoordinate, float yCoordinate,
			float xSize, float ySize) throws QImageCannotBeRetrievedException;

	/**
	 * Rotates an image
	 *
	 * @param image
	 *            The image to rotate
	 * @param xCoordinate
	 *            The coordinate on the x axis of the rotation center
	 * @param yCoordinate
	 *            The coordinate on the y axis of the rotation center
	 * @param angle
	 *            The rotation angle in radians
	 * @return A byte array containing the rotated image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] rotateImage(byte[] image, float xCoordinate,
			float yCoordinate, float angle)
			throws QImageCannotBeRetrievedException;

	/**
	 * Adjusts the contrast of an image
	 *
	 * @param image
	 *            The image whose contrast will be adjusted
	 * @param contrastFactor
	 *            The factor specifying how much the contrast will be modified.
	 *            A contrastFactor equal to 1 corresponds to no contrast
	 *            modification, a contrastFactor greater than 1 corresponds to
	 *            contrast increase while a contrastFactor less than 1
	 *            corresponds to contrastDecrease. Keep in mind that
	 *            constrastFactors equal or less than 0 will result to an all
	 *            black image (image without any contrast).
	 * @return A byte array containing the adjusted image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] adjustContrast(byte[] image, double contrastFactor)
			throws QImageCannotBeRetrievedException;

	/**
	 * Adjusts the brightness of an image
	 *
	 * @param image
	 *            The image whose brightness will be adjusted
	 * @param brightnessFactor
	 *            The factor specifying how much the brightness will be
	 *            adjusted. A positive brightnessFactor corresponds to
	 *            brightness increase, while a negative brightnessFactor
	 *            corresponds to brightness decrease.
	 * @return A byte array containing the adjusted image. The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] adjustBrightness(byte[] image, double brightnessFactor)
			throws QImageCannotBeRetrievedException;

	/**
	 * Adjusts the color of an image.
	 *
	 * @param image
	 *            The image whose color will be adjusted.
	 * @param bits
	 *            array containing RGB values.
	 * @return A byte array containing the adjusted image.The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument.
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read.
	 */
	public byte[] adjustColor(byte[] image, int[] bits)
			throws QImageCannotBeRetrievedException;

	/**
	 * Converts the image to grayscale.
	 *
	 * @param image
	 *            The image which needs to be converted to grayscale.
	 * @return byte array containing the adjusted image.The format of the
	 *         returned image is the same as that of the image passed as
	 *         argument.
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read.
	 */
	public byte[] convertToGrayscale(byte[] image)
			throws QImageCannotBeRetrievedException;

	/**
	 * Adds border to an image
	 *
	 * @param image
	 *            The image to add the border to
	 * @param size
	 *            The size (width) of the border to add, in pixels
	 * @param borderColor
	 *            The color of the border to add. It is an array having one
	 *            value for each band of the image. For example for an RGB image
	 *            these values correspond to RED, GREEN, BLUE.
	 * @return A byte array containing the new image. The format of the returned
	 *         image is the same as that of the image passed as argument. In
	 *         case the format of the image passed as argument cannot be
	 *         recognised a default format is used (see application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read or if the
	 *             borderColor's size is different than the number of image
	 *             bands.
	 */
	public byte[] addBorder(byte[] image, int size, double[] borderColor)
			throws QImageCannotBeRetrievedException;

	/**
	 * Overlays a text to an image
	 *
	 * @param image
	 *            The image to overlay the text to
	 * @param overlay
	 *            An OverlayTextDTO object containing information on the text to
	 *            overlay
	 * @return A byte array containing the new image. The format of the returned
	 *         image is the same as that of the image passed as argument. In
	 *         case the format of the image passed as argument cannot be
	 *         recognised a default format is used (see application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] overlayText(byte[] image, OverlayTextDTO overlay)
			throws QImageCannotBeRetrievedException;

	/**
	 * Overlays an java.awt.Image to an image. This method can be used to
	 * overlay custom graphics to an image, which can be painted to the
	 * java.awt.Image through a Graphics object.
	 *
	 * @param image
	 *            The image to overlay the image to
	 * @param overlay
	 *            The image to overlay.
	 * @param xCoordinate
	 * @param yCoordinate
	 * @return A byte array containing the new image. The format of the returned
	 *         image is the same as that of the image passed as argument. In
	 *         case the format of the image passed as argument cannot be
	 *         recognised a default format is used (see application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] overlayImage(byte[] image, Image overlay, int xCoordinate,
			int yCoordinate) throws QImageCannotBeRetrievedException;

	/**
	 * Overlays an image to another image. Both images are passed to this method
	 * as byte arrays. The images to be overlayed should have the same number of
	 * bands and data types
	 *
	 * @param image
	 *            The image to overlay the image to
	 * @param overlay
	 *            The image to overlay.
	 * @param xCoordinate
	 * @param yCoordinate
	 * @return A byte array containing the new image. The format of the returned
	 *         image is the same as that of the image passed as the first
	 *         argument. In case the format of the image passed as argument
	 *         cannot be recognised a default format is used (see
	 *         application.properties).
	 * @throws QImageCannotBeRetrievedException
	 *             If the image passed to this method cannot be read
	 */
	public byte[] overlayImage(byte[] image, byte[] overlay, int xCoordinate,
			int yCoordinate) throws QImageCannotBeRetrievedException;
}

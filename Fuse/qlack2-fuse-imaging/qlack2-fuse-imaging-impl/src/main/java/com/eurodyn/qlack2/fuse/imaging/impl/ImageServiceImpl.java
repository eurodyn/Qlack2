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
package com.eurodyn.qlack2.fuse.imaging.impl;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.jai.BorderExtenderConstant;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

import com.eurodyn.qlack2.fuse.imaging.api.ImageService;
import com.eurodyn.qlack2.fuse.imaging.api.dto.ImageInfo;
import com.eurodyn.qlack2.fuse.imaging.api.dto.OverlayTextDTO;
import com.eurodyn.qlack2.fuse.imaging.api.exception.QImageCannotBeRetrievedException;
import com.eurodyn.qlack2.fuse.imaging.impl.util.ImageIOUtil;

/**
 *
 * @author European Dynamics SA
 */
public class ImageServiceImpl implements ImageService {
	private static final Logger LOGGER = Logger
			.getLogger(ImageServiceImpl.class.getName());

	private PlanarImage rescale(PlanarImage pi, double scaleFactor,
			double offset) {
		// contrast adjusting factor
		double[] scaleFactorArray = new double[] { scaleFactor };
		// brightness adjusting factor
		double[] offsetArray = new double[] { offset };
		return JAI.create("rescale", pi, scaleFactorArray, offsetArray);
	}

	@Override
	public ImageInfo getImageInfo(byte[] image)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Getting image information");

		PlanarImage pi = null;
		ImageInfo info = new ImageInfo();

		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}
		info.setHeight(pi.getHeight());
		info.setWidth(pi.getWidth());
		info.setMinX(pi.getMinX());
		info.setMinY(pi.getMinY());
		info.setMaxX(pi.getMaxY());
		info.setMaxY(pi.getMaxY());
		info.setBands(pi.getSampleModel().getNumBands());
		info.setDataType(pi.getSampleModel().getDataType());
		info.setFormat(ImageIOUtil.getFormat(image));
		ColorModel cm = pi.getColorModel();
		if (cm != null) {
			info.setColorComponents(cm.getNumComponents());
			info.setBitsPerPixel(cm.getPixelSize());
			info.setTransparency(cm.getTransparency());
		}

		return info;
	}

	@Override
	public byte[] convertImage(byte[] image, String format)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Converting an image to {0} format", format);

		PlanarImage pi = null;

		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		return ImageIOUtil.encodeImage(pi, format);
	}

	@Override
	public byte[] scaleImage(byte[] image, float scaleFactor)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Scaling an image by {0}.", scaleFactor);
		return scaleImage(image, scaleFactor, scaleFactor);
	}

	@Override
	public byte[] scaleImage(byte[] image, float xFactor, float yFactor)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Scaling an image by {0}x{1}", new String[] {
				String.valueOf(xFactor), String.valueOf(yFactor) });

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(xFactor);
		pb.add(yFactor);
		pb.add(0.0F);
		pb.add(0.0F);
		pb.add(new InterpolationNearest());
		pi = JAI.create("scale", pb, null);

		return ImageIOUtil.encodeImage(pi, format);
	}

	@Override
	public byte[] scaleImage(byte[] image, int width)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Scaling an image to a width of {0} pixels.",
				width);

		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		int imageWidth = pi.getWidth();
		float scaleFactor = (float) width / (float) imageWidth;
		return scaleImage(image, scaleFactor, scaleFactor);
	}

	@Override
	public byte[] scaleImage(byte[] image, int width, int height)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Scaling an image to {0}x{1} pixels.",
				new String[] { String.valueOf(width), String.valueOf(height) });

		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		int imageWidth = pi.getWidth();
		int imageHeight = pi.getHeight();

		float xFactor = (float) width / (float) imageWidth;
		float yFactor = (float) height / (float) imageHeight;
		return scaleImage(image, xFactor, yFactor);
	}

	@Override
	public byte[] invertImage(byte[] image) throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Inverting an image");

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		pi = JAI.create("invert", pi);

		return ImageIOUtil.encodeImage(pi, format);
	}

	@Override
	public byte[] cropImage(byte[] image, float xCoordinate, float yCoordinate,
			float xSize, float ySize) throws QImageCannotBeRetrievedException {
		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		byte[] retVal = image;
		try {
			pi = ImageIOUtil.decodeImage(image);
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(pi);
			pb.add(xCoordinate);
			pb.add(yCoordinate);
			pb.add(xSize);
			pb.add(ySize);
			pi = JAI.create("crop", pb);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(pi.getAsBufferedImage(), format, bos);
			retVal = bos.toByteArray();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		return retVal;
	}

	@Override
	public byte[] rotateImage(byte[] image, float xCoordinate,
			float yCoordinate, float angle) throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Rotating an image");

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(xCoordinate);
		pb.add(yCoordinate);
		pb.add(angle);
		pi = JAI.create("rotate", pb);

		return ImageIOUtil.encodeImage(pi, format);
	}

	@Override
	public byte[] adjustContrast(byte[] image, double contrastFactor)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST,
				"Adjusting the contrast of an image. Contrast factor is {0}.",
				new String[] { String.valueOf(contrastFactor) });

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		pi = rescale(pi, contrastFactor, 0);
		return ImageIOUtil.encodeImage(pi, format);
	}

	@Override
	public byte[] adjustBrightness(byte[] image, double brightnessFactor)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(
				Level.FINEST,
				"Adjusting the brightness of an image. Brightness factor is {0}.",
				new String[] { String.valueOf(brightnessFactor) });

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		pi = rescale(pi, 1, brightnessFactor);
		return ImageIOUtil.encodeImage(pi, format);
	}

	public byte[] adjustColor(byte[] image, int[] bits)
			throws QImageCannotBeRetrievedException {

		LOGGER.log(Level.FINEST, "Adjusting the color of an image. ");

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}
		ColorModel cm = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), bits, false, false,
				Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		pb.add(cm);
		pi = JAI.create("ColorConvert", pb);

		return ImageIOUtil.encodeImage(pi, format);
	}

	public byte[] convertToGrayscale(byte[] image)
			throws QImageCannotBeRetrievedException {

		LOGGER.log(Level.FINEST, "converting the image to grayscale. ");

		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}
		String format = ImageIOUtil.getFormat(image);

		// Create BufferedImage from bytes[]
		BufferedImage inImage = pi.getAsBufferedImage();

		// Convert to grayscale
		ColorConvertOp op = new ColorConvertOp(
				ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

		BufferedImage outImage = op.filter(inImage, null);

		// Back to bytes[] from BufferedImage
		return ImageIOUtil.encodeImage(PlanarImage.wrapRenderedImage(outImage),
				format);
	}

	@Override
	public byte[] addBorder(byte[] image, int size, double[] borderColor)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Adding border to an image.");

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		if (borderColor.length != pi.getSampleModel().getNumBands()) {
			throw new QImageCannotBeRetrievedException(
					"The size of the borderColor "
							+ "array is not equal to the number of bands in the image");
		}

		ParameterBlock pb = new ParameterBlock();
		pb.addSource(pi);
		for (int i = 0; i < 4; i++) {
			pb.add(size);
		}
		pb.add(new BorderExtenderConstant(borderColor));
		pi = JAI.create("border", pb);
		return ImageIOUtil.encodeImage(pi, format);
	}

	@Override
	public byte[] overlayText(byte[] image, OverlayTextDTO overlay)
			throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Overlaying text ''{0}'' to an image", overlay);

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		TiledImage ti = new TiledImage(pi, false);
		Graphics g = ti.createGraphics();
		g.setFont(overlay.getFont());
		g.setColor(overlay.getColor());
		g.drawString(overlay.getText(), overlay.getxCoordinate(),
				overlay.getyCoordinate());
		return ImageIOUtil.encodeImage(ti, format);
	}

	@Override
	public byte[] overlayImage(byte[] image, Image overlay, int xCoordinate,
			int yCoordinate) throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Overlaying a java.awt.Image onto an image");

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		TiledImage ti = new TiledImage(pi, false);
		Graphics g = ti.createGraphics();
		g.drawImage(overlay, xCoordinate, yCoordinate, null);
		return ImageIOUtil.encodeImage(ti, format);
	}

	@Override
	public byte[] overlayImage(byte[] image, byte[] overlay, int xCoordinate,
			int yCoordinate) throws QImageCannotBeRetrievedException {
		LOGGER.log(Level.FINEST, "Overlaying an onto another image");

		String format = ImageIOUtil.getFormat(image);
		PlanarImage pi = null;
		try {
			pi = ImageIOUtil.decodeImage(image);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		PlanarImage oi = null;
		try {
			oi = ImageIOUtil.decodeImage(overlay);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			throw new QImageCannotBeRetrievedException(e.getLocalizedMessage());
		}

		oi = JAI.create("translate", oi, Float.valueOf(xCoordinate),
				Float.valueOf(yCoordinate));
		pi = JAI.create("overlay", pi, oi);

		return ImageIOUtil.encodeImage(pi, format);
	}

}

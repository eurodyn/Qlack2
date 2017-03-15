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
package com.eurodyn.qlack2.fuse.imaging.impl.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

import com.eurodyn.qlack2.fuse.imaging.api.util.ImageFormats;
import com.sun.media.jai.codec.ByteArraySeekableStream;

/**
 * 
 * @author European Dynamics SA
 */
public class ImageIOUtil {
	private static final Logger LOGGER = Logger.getLogger(ImageIOUtil.class
			.getName());
	private static final String default_format = "jpeg";
	private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
	private static final int thumbnail_width = 70;
	private static final int thumbnail_height = 70;

	/**
	 * 
	 * @param imageArray
	 * @return
	 * @throws IOException
	 */
	public static PlanarImage decodeImage(byte[] imageArray) throws IOException {
		LOGGER.log(Level.FINEST, "Decoding image");
		ByteArraySeekableStream stream = new ByteArraySeekableStream(imageArray);
		return JAI.create("stream", stream);
	}

	/**
	 * 
	 * @param pi
	 * @param format
	 * @return
	 */
	public static byte[] encodeImage(PlanarImage pi, String format) {
		LOGGER.log(Level.FINEST, "Encoding image");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		String disableMediaLib = System
				.getProperty("com.sun.media.jai.disableMediaLib");
		if ((disableMediaLib == null) || (disableMediaLib.equals("false"))) {
			System.setProperty("com.sun.media.jai.disableMediaLib", "true");
		}
		JAI.create("encode", pi, stream, format);
		return stream.toByteArray();
	}

	/**
	 * 
	 * @param image
	 * @return
	 */
	public static String getFormat(byte[] image) {
		String format = default_format;
		Collection mimeTypeCollection = MimeTypeFinder.findMimeTypes(image);
		if (mimeTypeCollection.size() == 0) {
			LOGGER.log(Level.WARNING,
					"Image mimetype not found - returning default format");
			return format;
		}

		String mimeType = mimeTypeCollection.iterator().next().toString();

		if ((mimeType.equals("image/bmp") || mimeType
				.equals("image/x-windows-bmp"))
				|| mimeType.equals("image/x-ms-bmp")) {
			format = ImageFormats.BMP;
		} else if ((mimeType.equals("image/jpeg"))
				|| ((mimeType.equals("image/pjpeg")))) {
			format = ImageFormats.JPEG;
		} else if ((mimeType.equals("image/tiff"))
				|| mimeType.equals("image/x-tiff")) {
			format = ImageFormats.TIFF;
		} else if (mimeType.equals("image/png")) {
			format = ImageFormats.PNG;
		} else {
			LOGGER.log(
					Level.WARNING,
					"Format for mimetype {0} not recognised - returning default format {1}",
					new String[] { mimeType, format });
		}

		return format;
	}

	public static String getMimeType(byte[] image) {
		String mimeType = DEFAULT_MIME_TYPE;
		Collection mimeTypeCollection = MimeTypeFinder.findMimeTypes(image);
		if (mimeTypeCollection.size() == 0) {
			LOGGER.log(Level.WARNING,
					"Image mimetype not found - returning default");
			return mimeType;
		}

		mimeType = mimeTypeCollection.iterator().next().toString();

		return mimeType;
	}
}

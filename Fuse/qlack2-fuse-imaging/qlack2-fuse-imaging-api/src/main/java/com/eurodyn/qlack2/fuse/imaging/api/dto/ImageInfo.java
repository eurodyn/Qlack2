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
package com.eurodyn.qlack2.fuse.imaging.api.dto;

import java.awt.Transparency;
import java.awt.image.DataBuffer;
import java.io.Serializable;

/**
 * A class representing the information available for an image.
 *
 * @author European Dynamics SA.
 */
public class ImageInfo implements Serializable {

	public static final int TRANSPARENCY_BITMASK = Transparency.BITMASK;
	public static final int TRANSPARENCY_OPAQUE = Transparency.OPAQUE;
	public static final int TRANSPARENCY_TRANSLUCENT = Transparency.TRANSLUCENT;
	public static final int TYPE_BYTE = DataBuffer.TYPE_BYTE;
	public static final int TYPE_SHORT = DataBuffer.TYPE_SHORT;
	public static final int TYPE_USHORT = DataBuffer.TYPE_USHORT;
	public static final int TYPE_INT = DataBuffer.TYPE_INT;
	public static final int TYPE_FLOAT = DataBuffer.TYPE_FLOAT;
	public static final int TYPE_DOUBLE = DataBuffer.TYPE_DOUBLE;
	public static final int TYPE_UNDEFINED = DataBuffer.TYPE_UNDEFINED;
	public static final long serialVersionUID = -2431255659207335081L;
	private int width;
	private int height;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private int bands;
	private int colorComponents;
	private int bitsPerPixel;
	private int transparency;
	private String format;
	private int dataType;

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the minX
	 */
	public int getMinX() {
		return minX;
	}

	/**
	 * @param minX
	 *            the minX to set
	 */
	public void setMinX(int minX) {
		this.minX = minX;
	}

	/**
	 * @return the minY
	 */
	public int getMinY() {
		return minY;
	}

	/**
	 * @param minY
	 *            the minY to set
	 */
	public void setMinY(int minY) {
		this.minY = minY;
	}

	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX
	 *            the maxX to set
	 */
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the maxY
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * @param maxY
	 *            the maxY to set
	 */
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

	/**
	 * @return the bands
	 */
	public int getBands() {
		return bands;
	}

	/**
	 * @param bands
	 *            the bands to set
	 */
	public void setBands(int bands) {
		this.bands = bands;
	}

	/**
	 * @return the colorComponents
	 */
	public int getColorComponents() {
		return colorComponents;
	}

	/**
	 * @param colorComponents
	 *            the colorComponents to set
	 */
	public void setColorComponents(int colorComponents) {
		this.colorComponents = colorComponents;
	}

	/**
	 * @return the bitsPerPixel
	 */
	public int getBitsPerPixel() {
		return bitsPerPixel;
	}

	/**
	 * @param bitsPerPixel
	 *            the bitsPerPixel to set
	 */
	public void setBitsPerPixel(int bitsPerPixel) {
		this.bitsPerPixel = bitsPerPixel;
	}

	/**
	 * @return the transparency
	 */
	public int getTransparency() {
		return transparency;
	}

	/**
	 * @param transparency
	 *            the transparency to set
	 */
	public void setTransparency(int transparency) {
		this.transparency = transparency;
	}

	/**
	 * @return the dataType
	 */
	public int getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
}

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


import java.io.Serializable;

/**
 *
 * @author European Dynamics SA
 */
public class OCRImage implements Serializable {
    private static final long serialVersionUID = 8711326630317915641L;

    private byte[] image;
    private float minCharBreakWidthAsFractionOfRowHeight = (float)0.05;
    private int whiteThreshold = 127;


    public OCRImage() {}


    public OCRImage(byte[] image) {
        this.image = image;
    }


    public OCRImage(byte[] image, float minCharBreakWidthAsFractionOfRowHeight, int whiteThreshold) {
        this.image = image;
        this.minCharBreakWidthAsFractionOfRowHeight = minCharBreakWidthAsFractionOfRowHeight;
        this.whiteThreshold = whiteThreshold;
    }


    public byte[] getImage() {
        return image;
    }


    public void setImage(byte[] image) {
        this.image = image;
    }


    public float getMinCharBreakWidthAsFractionOfRowHeight() {
        return minCharBreakWidthAsFractionOfRowHeight;
    }


    /**
     * Sets the minimum break between characters included in the image as a fraction
     * of the characters' height. The default value is 0.05.
     * @param minCharBreakWidthAsFractionOfRowHeight
     */
    public void setMinCharBreakWidthAsFractionOfRowHeight(float minCharBreakWidthAsFractionOfRowHeight) {
        this.minCharBreakWidthAsFractionOfRowHeight = minCharBreakWidthAsFractionOfRowHeight;
    }


    public int getWhiteThreshold() {
        return whiteThreshold;
    }


    /**
     * Sets the threshold used to determine whether something is a white area or
     * a character. This can range from 0 to 255 (inclusive), while the default value is 127
     * @param whiteThreshold
     */
    public void setWhiteThreshold(int whiteThreshold) {
        this.whiteThreshold = whiteThreshold;
    }
}

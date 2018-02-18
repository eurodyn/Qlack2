package com.eurodyn.qlack2.fuse.imaging.api;

import java.awt.Color;

/**
 * A service to generate QR codes.
 */
public interface QRCodeService {

  /**
   * Generate a QR code using default parameters.
   *
   * @param text The text to encode.
   * @return Returns the QR code with the given text encoded.
   */
  byte[] generateQRCode(String text);

  /**
   * Generates a customised QR code.
   *
   * @param text The text to encode.
   * @param width The width of the resulting image holding the QR code.
   * @param height The height of the resulting image holding the QR code.
   * @param imageFormat The image format of the resulting image holding the QR code.
   * @param background The background color of the resulting image holding the QR code.
   * @param foreground The foreground color of the resulting image holding the QR code.
   * @return Returns the QR code with the given text encoded.
   */
  byte[] generateQRCode(String text, int width, int height, String imageFormat,
    Color background, Color foreground);
}

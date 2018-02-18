package com.eurodyn.qlack2.fuse.imaging.impl;

import com.eurodyn.qlack2.fuse.imaging.api.QRCodeService;
import com.eurodyn.qlack2.fuse.imaging.api.exception.QImagingException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javax.imageio.ImageIO;
import javax.inject.Singleton;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Singleton
@OsgiServiceProvider(classes = QRCodeService.class)
public class QRCodeServiceImpl implements QRCodeService {

  // Default width for QR code.
  public static final int DEFAULT_WIDTH = 125;

  // Default height for QR code.
  public static final int DEFAULT_HEIGHT = 125;

  // Default image format for QR code.
  public static final String DEFAULT_FORMAT = "PNG";

  // Default background color for QR code.
  public static final Color DEFAULT_BACKGROUND = Color.WHITE;

  // Default foreground color for QR code.
  public static final Color DEFAULT_FOREGROUND = Color.BLACK;

  @Override
  public byte[] generateQRCode(String text) {
    return generateQRCode(text, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FORMAT, DEFAULT_BACKGROUND,
      DEFAULT_FOREGROUND);
  }

  @Override
  public byte[] generateQRCode(String text, int width, int height, String imageFormat,
    Color background, Color foreground) {
    byte[] qrCode = null;

    try {
      // Prepare the QRCode writer.
      HashMap hintMap = new HashMap();
      hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix byteMatrix = qrCodeWriter
        .encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);

      // Create the BufferedImage to hold the QRCode.
      int matrixWidth = byteMatrix.getWidth();
      BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);

      // Paint and save the image using the ByteMatrix.
      image.createGraphics();
      Graphics2D graphics = (Graphics2D) image.getGraphics();
      graphics.setColor(background);
      graphics.fillRect(0, 0, matrixWidth, matrixWidth);
      graphics.setColor(foreground);
      for (int i = 0; i < matrixWidth; i++) {
        for (int j = 0; j < matrixWidth; j++) {
          if (byteMatrix.get(i, j)) {
            graphics.fillRect(i, j, 1, 1);
          }
        }
      }

      // Get the image into a byte[].
      try (ByteArrayOutputStream dstImage = new ByteArrayOutputStream()) {
        ImageIO.write(image, imageFormat, dstImage);
        qrCode = dstImage.toByteArray();
      }
    } catch (WriterException | IOException e) {
      throw new QImagingException("Could not generate QR code.", e);
    }

    return qrCode;
  }
}

package com.eurodyn.qlack2.fuse.imaging.tests;

import com.eurodyn.qlack2.fuse.imaging.api.ImageService;
import com.eurodyn.qlack2.fuse.imaging.api.dto.OverlayTextDTO;
import com.eurodyn.qlack2.fuse.imaging.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.imaging.util.TestConst;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class ImageServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  ImageService imageService;

  private byte[] getTestImage() throws IOException {
    return IOUtils.toByteArray(this.getClass().getResource("/image.jpg"));
  }

  @Test
  public void getImageInfo() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.getImageInfo(testImage));
    Assert.assertNotNull(imageService.getImageInfo(testImage).getHeight() != 0);
  }

  @Test
  public void convertImage() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.convertImage(testImage, TestConst.format));
    //Assert.assertNotNull(imageService.getImageInfo(imageInByte).getHeight() != 0);
  }

  @Test
  public void scaleImage() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.scaleImage(testImage, TestConst.scaleFactor));
  }

  @Test
  public void scaleImageXY() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(
      imageService.scaleImage(testImage, TestConst.scaleFactorX, TestConst.scaleFactorY));

  }

  @Test
  public void scaleImageWidth() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(
      imageService.scaleImage(testImage, TestConst.scaleFactorX, TestConst.scaleFactorInt));
  }

  @Test
  public void scaleImageWidthHeight() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(
      imageService.scaleImage(testImage, TestConst.scaleFactorIntX, TestConst.scaleFactorIntY));

  }

  @Test
  public void invertImage() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.invertImage(testImage));
  }

  @Test
  public void cropImage() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService
      .cropImage(testImage, TestConst.scaleFactorX, TestConst.scaleFactorY, TestConst.sizeX,
        TestConst.sizeY));
  }

  @Test
  public void rotateImage() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService
      .rotateImage(testImage, TestConst.sizeX, TestConst.sizeY, TestConst.scaleFactor));
  }

  @Test
  public void adjustContrast() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.adjustContrast(testImage, TestConst.contrastFactor));
  }

  @Test
  public void adjustBrightness() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.adjustBrightness(testImage, TestConst.contrastFactor));
  }

  @Test
  public void adjustColor() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.adjustColor(testImage, new int[]{8, 8, 8, 8}));
  }

  @Test
  public void convertToGrayscale() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.convertToGrayscale(testImage));
  }

  @Test
  public void addBorder() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService.addBorder(testImage, TestConst.scaleFactorInt,
      new double[]{1d, 2d, 3d}));
  }

  @Test
  public void overlayText() throws IOException {
    byte[] testImage = getTestImage();

    Color color = new Color(255, 0, 255);
    Font font = new Font("font", 10, 20);

    OverlayTextDTO overlayTextDTO = new OverlayTextDTO();
    overlayTextDTO.setColor(color);
    overlayTextDTO.setFont(font);
    overlayTextDTO.setxCoordinate(TestConst.scaleFactorIntX);
    overlayTextDTO.setyCoordinate(TestConst.scaleFactorIntY);
    overlayTextDTO.setText(TestConst.generateRandomString());

    Assert.assertNotNull(imageService.overlayText(testImage, overlayTextDTO));
  }

  @Test
  public void overlayImage() throws IOException {
    byte[] testImage = getTestImage();
    Toolkit t = Toolkit.getDefaultToolkit();
    Image i = t.getImage(TestConst.createLocalImagePath());

    Assert.assertNotNull(imageService
      .overlayImage(testImage, i, TestConst.scaleFactorIntX, TestConst.scaleFactorIntY));
  }

  @Test
  public void overlayImageArgs() throws IOException {
    byte[] testImage = getTestImage();
    Assert.assertNotNull(imageService
        .overlayImage(testImage, testImage, TestConst.scaleFactorIntX,
          TestConst.scaleFactorIntY));
  }

}


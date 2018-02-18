package com.eurodyn.qlack2.fuse.imaging.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.eurodyn.qlack2.fuse.imaging.api.ImagingService;
import com.eurodyn.qlack2.fuse.imaging.api.dto.ImageFormatHandler;
import com.eurodyn.qlack2.fuse.imaging.api.dto.ImageInfo;
import com.eurodyn.qlack2.fuse.imaging.api.util.ColorSpaceType;
import com.eurodyn.qlack2.fuse.imaging.api.util.ICCProfile;
import com.eurodyn.qlack2.fuse.imaging.api.util.ResamplingAlgorithm;
import com.eurodyn.qlack2.fuse.imaging.api.util.TIFFCompression;
import com.eurodyn.qlack2.fuse.imaging.impl.conf.ITTestConf;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class ImagingServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  ImagingService imagingService;

  @Inject
  BundleContext bundleContext;

  private byte[] getResource(String resource) throws IOException {
    return IOUtils.toByteArray(bundleContext.getBundle().getResource(resource));
  }

  @Before
  public void init() {
    System.out.println("Waiting for Image readers to become available...");
    Awaitility.await().atMost(10, TimeUnit.SECONDS).pollDelay(1, TimeUnit.SECONDS).untilTrue(
      new AtomicBoolean(imagingService.isFormatSupportedForRead("SVG")
        && imagingService.isFormatSupportedForRead("TIFF")));
    System.out.println("Image readers are available now.");
  }

  @Test
  public void getInfo() throws IOException {
    String testFile = "/samples/png/8bit.png";
    ImageInfo info = imagingService.getInfo(getResource(testFile));
    assertEquals(24, info.getBitsPerPixel());
    assertEquals("png", info.getFormat());
    assertEquals(300, info.getHeight());
    assertEquals(300, info.getWidth());
    assertEquals("image/png", info.getMimeType());
    assertEquals(ColorSpaceType.TYPE_RGB, info.getColorType());
    assertEquals(0, info.getDotsPerInch().getHorizontal());
    assertEquals(0, info.getDotsPerInch().getVertical());

    testFile = "/samples/png/16bit.png";
    info = imagingService.getInfo(getResource(testFile));
    assertEquals(48, info.getBitsPerPixel());
    assertEquals("png", info.getFormat());
    assertEquals(600, info.getHeight());
    assertEquals(600, info.getWidth());
    assertEquals("image/png", info.getMimeType());
    assertEquals(ColorSpaceType.TYPE_RGB, info.getColorType());
    assertEquals(0, info.getDotsPerInch().getHorizontal());
    assertEquals(0, info.getDotsPerInch().getVertical());

    testFile = "/samples/png/72dpi.png";
    info = imagingService.getInfo(getResource(testFile));
    assertEquals(48, info.getBitsPerPixel());
    assertEquals("png", info.getFormat());
    assertEquals(600, info.getHeight());
    assertEquals(600, info.getWidth());
    assertEquals("image/png", info.getMimeType());
    assertEquals(ColorSpaceType.TYPE_RGB, info.getColorType());
    assertEquals(72, info.getDotsPerInch().getHorizontal());
    assertEquals(72, info.getDotsPerInch().getVertical());

    testFile = "/samples/png/300dpi.png";
    info = imagingService.getInfo(getResource(testFile));
    assertEquals(48, info.getBitsPerPixel());
    assertEquals("png", info.getFormat());
    assertEquals(2500, info.getHeight());
    assertEquals(2500, info.getWidth());
    assertEquals("image/png", info.getMimeType());
    assertEquals(ColorSpaceType.TYPE_RGB, info.getColorType());
    assertEquals(300, info.getDotsPerInch().getHorizontal());
    assertEquals(300, info.getDotsPerInch().getVertical());
  }

  @Test
  public void convert() throws IOException {
    String outDir = System.getProperty("java.io.tmpdir");
    System.out.println("Writing sample output files to: " + outDir);

    // Generic PNG to JPEG
    String testFile = "/samples/png/16bit.png";
    byte[] srcFile = getResource(testFile);
    srcFile = imagingService.removeAlphaChannel(srcFile);
    byte[] dstFile = imagingService.convert(srcFile, "JPEG", null);
    assertTrue(dstFile.length > 0);
    FileUtils.writeByteArrayToFile(new File(outDir + File.separator + "generic-png-to-jpeg.jpeg"),
      dstFile);

    // Generic PNG to TIFF
    testFile = "/samples/png/16bit.png";
    srcFile = getResource(testFile);
    dstFile = imagingService.convert(srcFile, "TIFF", ICCProfile.Coated_Fogra39L_VIGC_300);
    assertTrue(dstFile.length > 0);
    FileUtils.writeByteArrayToFile(new File(outDir + File.separator + "generic-png-to-tiff.tiff"),
      dstFile);

    // Generic PNG to TIFF (custom profile CoatedFOGRA27)
    testFile = "/samples/png/16bit.png";
    srcFile = getResource(testFile);
    dstFile = imagingService.convert(srcFile, "TIFF", ICCProfile.CoatedFOGRA27);
    assertTrue(dstFile.length > 0);
    FileUtils.writeByteArrayToFile(new File(outDir + File.separator + "generic-png-to-tiff-CoatedFOGRA27.tiff"),
      dstFile);

    // Generic JPEG to PNG
    testFile = "/samples/jpeg/sample.jpg";
    srcFile = getResource(testFile);
    dstFile = imagingService.convert(srcFile, "PNG");
    assertTrue(dstFile.length > 0);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "generic-jpeg-to-png.png"), dstFile);

    // Generic JPEG to TIFF
    testFile = "/samples/jpeg/sample.jpg";
    srcFile = getResource(testFile);
    dstFile = imagingService.convert(srcFile, "TIFF");
    assertTrue(dstFile.length > 0);
    FileUtils.writeByteArrayToFile(new File(outDir + File.separator + "generic-jpeg-to-TIFF.tiff"),
      dstFile);

    // Generic TIFF to PNG
    testFile = "/samples/tiff/sample.tiff";
    srcFile = getResource(testFile);
    dstFile = imagingService.convert(srcFile, "PNG");
    assertTrue(dstFile.length > 0);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "generic-tiff-to-png.png"), dstFile);

    // Generic TIFF to JPEG
    testFile = "/samples/tiff/sample.tiff";
    srcFile = getResource(testFile);
    dstFile = imagingService.convert(srcFile, "JPEG");
    assertTrue(dstFile.length > 0);
    FileUtils.writeByteArrayToFile(new File(outDir + File.separator + "generic-tiff-to-jpeg.jpg"),
      dstFile);

    // Specialised PNG to TIFF
    testFile = "/samples/png/16bit.png";
    srcFile = getResource(testFile);
    dstFile = imagingService
      .convertToTIFF(srcFile, ICCProfile.Coated_Fogra39L_VIGC_300, TIFFCompression.LZW);
    assertTrue(dstFile.length > 0);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "specialised-png-to-tiff-icc.tiff"),
        dstFile);

    // Specialised JPEG to TIFF
    testFile = "/samples/jpeg/sample.jpg";
    srcFile = getResource(testFile);
    dstFile = imagingService
      .convertToTIFF(srcFile, ICCProfile.Coated_Fogra39L_VIGC_300, TIFFCompression.LZW);
    assertTrue(dstFile.length > 0);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "specialised-jpeg-to-TIFF-icc.tiff"),
        dstFile);
    testFile = "/samples/jpeg/sample.jpg";
    srcFile = getResource(testFile);
    dstFile = imagingService.convertToTIFF(srcFile, TIFFCompression.LZW);
    assertTrue(dstFile.length > 0);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "specialised-jpeg-to-TIFF.tiff"),
        dstFile);

    // Generic SVG to TIFF
    testFile = "/samples/svg/sample.svg";
    srcFile = getResource(testFile);
    dstFile = imagingService.convert(srcFile, "TIFF");
    assertTrue(dstFile.length > 0);
    FileUtils.writeByteArrayToFile(new File(outDir + File.separator + "generic-svg-to-tiff.tiff"),
      dstFile);

    // Generic SVG to PNG
    testFile = "/samples/svg/sample.svg";
    srcFile = getResource(testFile);
    dstFile = imagingService.convert(srcFile, "PNG");
    assertTrue(dstFile.length > 0);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "generic-svg-to-png.png"), dstFile);

    // Specialised SVG to TIFF
    testFile = "/samples/svg/sample.svg";
    srcFile = getResource(testFile);
    dstFile = imagingService
      .convertToTIFF(srcFile, ICCProfile.Coated_Fogra39L_VIGC_260, TIFFCompression.LZW);
    assertTrue(dstFile.length > 0);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "specialised-svg-to-tiff-icc.tiff"),
        dstFile);
    testFile = "/samples/svg/sample.svg";
    srcFile = getResource(testFile);
    dstFile = imagingService.convertToTIFF(srcFile, TIFFCompression.LZW);
    assertTrue(dstFile.length > 0);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "specialised-svg-to-tiff.tiff"),
        dstFile);
  }

  @Test
  public void testGetSupportedWriteFormats() {
    final List<ImageFormatHandler> supportedWriteFormats = imagingService
      .getSupportedWriteFormats();
    for (ImageFormatHandler imageFormatHandler : supportedWriteFormats) {
      System.out.println(imageFormatHandler.getFormat());
      for (String handlerClass : imageFormatHandler.getHandlerClasses()) {
        System.out.println("\t" + handlerClass);
      }
    }
  }

  @Test
  public void testGetSupportedReadFormats() {
    final List<ImageFormatHandler> supportedReadFormats = imagingService
      .getSupportedReadFormats();
    for (ImageFormatHandler imageFormatHandler : supportedReadFormats) {
      System.out.println(imageFormatHandler.getFormat());
      for (String handlerClass : imageFormatHandler.getHandlerClasses()) {
        System.out.println("\t" + handlerClass);
      }
    }
  }

  @Test
  public void testResample() throws IOException {
    // Setup output params.
    String outDir = System.getProperty("java.io.tmpdir");
    System.out.println("Writing sample output files to: " + outDir);

    // Read test file.
    String testFile = "/samples/png/8bit.png";
    byte[] srcFile = getResource(testFile);

    // Perform conversions. Chosen filters do not constitute suggestions as to which filter you
    // should use for each type of resampling operation.
    byte[] dstFile = imagingService
      .resampleByFactor(srcFile, 2f, ResamplingAlgorithm.FILTER_CUBIC);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "resample-by-factor.png"),
        dstFile);
    assertEquals(600, imagingService.getInfo(dstFile).getWidth());
    assertEquals(600, imagingService.getInfo(dstFile).getHeight());

    dstFile = imagingService
      .resampleByHeight(srcFile, 150, ResamplingAlgorithm.FILTER_BLACKMAN_SINC);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "resample-by-height.png"),
        dstFile);
    assertEquals(150, imagingService.getInfo(dstFile).getWidth());
    assertEquals(150, imagingService.getInfo(dstFile).getHeight());

    dstFile = imagingService
      .resampleByWidth(srcFile, 150, ResamplingAlgorithm.FILTER_HAMMING);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "resample-by-width.png"),
        dstFile);
    assertEquals(150, imagingService.getInfo(dstFile).getWidth());
    assertEquals(150, imagingService.getInfo(dstFile).getHeight());

    dstFile = imagingService
      .resampleByPercent(srcFile, 50, ResamplingAlgorithm.FILTER_MITCHELL);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "resample-by-percent.png"),
        dstFile);
    assertEquals(150, imagingService.getInfo(dstFile).getWidth());
    assertEquals(150, imagingService.getInfo(dstFile).getHeight());

    dstFile = imagingService.resample(srcFile, 50, 50, ResamplingAlgorithm.FILTER_QUADRATIC);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "resample.png"),
        dstFile);
    assertEquals(50, imagingService.getInfo(dstFile).getWidth());
    assertEquals(50, imagingService.getInfo(dstFile).getHeight());
  }

}
package com.eurodyn.qlack2.fuse.imaging.impl.tests;

import com.eurodyn.qlack2.fuse.imaging.api.QRCodeService;
import com.eurodyn.qlack2.fuse.imaging.impl.conf.ITTestConf;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class QRCodeServiceImplTest extends ITTestConf {

  private static final String qrText = "http://www.eurodyn.com";

  @Inject
  @Filter(timeout = 1200000)
  QRCodeService qrCodeService;

  @Test
  public void testGenerateQRCode() throws IOException {
    String outDir = System.getProperty("java.io.tmpdir");
    System.out.println("Writing sample output files to: " + outDir);

    byte[] qrCode = qrCodeService.generateQRCode(qrText);
    FileUtils.writeByteArrayToFile(new File(outDir + File.separator + "qrcode-simple.png"), qrCode);
  }

  @Test
  public void testGenerateQRCodeCustom() throws IOException {
    String outDir = System.getProperty("java.io.tmpdir");
    System.out.println("Writing sample output files to: " + outDir);

    byte[] qrCode = qrCodeService.generateQRCode(qrText, 200, 200, "PNG", Color.WHITE, Color.BLACK);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "qrcode-custom1.png"), qrCode);

    qrCode = qrCodeService.generateQRCode(qrText, 250, 250, "PNG", Color.BLACK, Color.WHITE);
    FileUtils
      .writeByteArrayToFile(new File(outDir + File.separator + "qrcode-custom2.png"), qrCode);
  }
}
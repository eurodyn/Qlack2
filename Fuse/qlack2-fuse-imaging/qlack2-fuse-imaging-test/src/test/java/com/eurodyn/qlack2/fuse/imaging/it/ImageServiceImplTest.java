package com.eurodyn.qlack2.fuse.imaging.it;

import com.eurodyn.qlack2.fuse.imaging.api.ImageService;
import com.eurodyn.qlack2.fuse.imaging.api.dto.OverlayTextDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class ImageServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    ImageService imageService;

    @Test
    public void getImageInfo(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            Assert.assertNotNull(imageService.getImageInfo(imageInByte));
            Assert.assertNotNull(imageService.getImageInfo(imageInByte).getHeight() != 0);
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }

    }

    @Test
    public void convertImage(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            Assert.assertNotNull(imageService.convertImage(imageInByte,TestConst.format));
            //Assert.assertNotNull(imageService.getImageInfo(imageInByte).getHeight() != 0);
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }

    }

    @Test
    public void scaleImage(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.scaleImage(imageInByte,TestConst.scaleFactor));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void scaleImageXY(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.scaleImage(imageInByte,TestConst.scaleFactorX,TestConst.scaleFactorY));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void scaleImageWidth(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.scaleImage(imageInByte,TestConst.scaleFactorX,TestConst.scaleFactorInt));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void scaleImageWidthHeight(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.scaleImage(imageInByte,TestConst.scaleFactorIntX,TestConst.scaleFactorIntY));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void invertImage(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.invertImage(imageInByte));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void cropImage(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.cropImage(imageInByte,TestConst.scaleFactorX,TestConst.scaleFactorY,TestConst.sizeX,TestConst.sizeY));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void rotateImage(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.rotateImage(imageInByte,TestConst.sizeX,TestConst.sizeY,TestConst.scaleFactor));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void adjustContrast(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.adjustContrast(imageInByte,TestConst.contrastFactor));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void adjustBrightness(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.adjustBrightness(imageInByte,TestConst.contrastFactor));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void adjustColor(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.adjustColor(imageInByte,new int[] {8,8,8,8}));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void convertToGrayscale(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.convertToGrayscale(imageInByte));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void addBorder(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            double[] border = new double[bimg.getRaster().getNumBands()];
            Assert.assertNotNull(imageService.addBorder(imageInByte,TestConst.scaleFactorInt,border));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void overlayText(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Color color =  new Color(255,0,255);
            Font font = new Font("font", 10, 20);

            OverlayTextDTO overlayTextDTO = new OverlayTextDTO();
            overlayTextDTO.setColor(color);
            overlayTextDTO.setFont(font);
            overlayTextDTO.setxCoordinate(TestConst.scaleFactorIntX);
            overlayTextDTO.setyCoordinate(TestConst.scaleFactorIntY);
            overlayTextDTO.setText(TestConst.generateRandomString());

            Assert.assertNotNull(imageService.overlayText(imageInByte,overlayTextDTO));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void overlayImage(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Toolkit t=Toolkit.getDefaultToolkit();
            Image i=t.getImage(TestConst.createLocalImagePath());

            Assert.assertNotNull(imageService.overlayImage(imageInByte,i,TestConst.scaleFactorIntX,TestConst.scaleFactorIntY));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

    @Test
    public void overlayImageArgs(){
        try{
            BufferedImage bimg = ImageIO.read(new File(TestConst.createLocalImagePath()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bimg, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();

            Assert.assertNotNull(imageService.overlayImage(imageInByte,imageInByte,TestConst.scaleFactorIntX,TestConst.scaleFactorIntY));
        }catch(Exception e){
            Assert.assertEquals(e,"Exception");
        }
    }

}


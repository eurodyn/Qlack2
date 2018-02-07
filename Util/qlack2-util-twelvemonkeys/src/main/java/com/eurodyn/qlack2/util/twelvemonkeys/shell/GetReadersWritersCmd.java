package com.eurodyn.qlack2.util.twelvemonkeys.shell;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.io.IOException;
import java.util.Iterator;

@Command(scope = "qlack", name = "twelvemonkeys-formats", description =
  "Returns a list with all registered image readers and writers.")
@Service
@SuppressWarnings("squid:S106")
public final class GetReadersWritersCmd implements Action {

  @Override
  public Object execute() throws IOException {
    // Refresh registry.
    IIORegistry registry = IIORegistry.getDefaultInstance();
    registry.registerServiceProviders(ServiceRegistry.lookupProviders(ImageReaderSpi.class));
    registry.registerServiceProviders(ServiceRegistry.lookupProviders(ImageWriterSpi.class));

    System.out.println("Available image readers:");
    for (String reader : ImageIO.getReaderFormatNames()) {
      System.out.println("\t" + reader);
      Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByFormatName(reader);
      while (imageReaders.hasNext()) {
        System.out.println("\t\t" + imageReaders.next());

      }
    }

    System.out.println();

    System.out.println("Available image writers:");
    for (String writer : ImageIO.getWriterFormatNames()) {
      System.out.println("\t" + writer);
      Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName(writer);
      while (imageWriters.hasNext()) {
        System.out.println("\t\t" + imageWriters.next());

      }
    }

    return null;
  }

}

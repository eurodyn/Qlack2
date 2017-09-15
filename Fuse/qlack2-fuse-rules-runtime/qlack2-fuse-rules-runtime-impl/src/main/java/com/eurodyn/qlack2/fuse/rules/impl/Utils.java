package com.eurodyn.qlack2.fuse.rules.impl;

import com.eurodyn.qlack2.fuse.rules.api.QRulesRuntimeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

  private static final Logger logger = Logger.getLogger(Utils.class.getName());

  private Utils() {

  }

  static byte[] serializeObject(Object object) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(
      baos)) {

      oos.writeObject(object);
      return baos.toByteArray();

    } catch (IOException e) {
      throw new QRulesRuntimeException(e);
    }
  }

  static Object deserializeObject(final ClassLoader classLoader, byte[] bytes) {
    try (
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bais) {
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc)
          throws IOException, ClassNotFoundException {
          String name = desc.getName();
          logger.log(Level.FINER, "Input stream is trying to resolve {0}", name);
          // XXX check if {@link ClassLoader#loadClass(String)} is the right method to call here
          Class<?> clazz = classLoader.loadClass(name);
          if (clazz != null) {
            return clazz;
          } else {
            return super.resolveClass(desc);
          }
        }
      };) {

      return ois.readObject();

    } catch (IOException e) {
      throw new QRulesRuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new QRulesRuntimeException(e);
    } catch (Exception e) {
      throw new QRulesRuntimeException(e);
    }
  }

}
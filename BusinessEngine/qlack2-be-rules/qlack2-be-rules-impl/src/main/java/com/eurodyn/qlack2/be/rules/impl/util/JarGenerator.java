package com.eurodyn.qlack2.be.rules.impl.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarGenerator {

	public JarGenerator() {
	}

	public byte[] generate(List<JarDataModelVersion> classes) {
		Manifest man = new Manifest();
		man.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			JarOutputStream jos = new JarOutputStream(baos, man);

			for (JarDataModelVersion clazz : classes) {
				String name = clazz.getClassName();
				byte[] bytecode = clazz.getBytes();
				long lastModified = clazz.getLastModified();

				String path = name.replace('.', '/') + ".class";

				JarEntry entry = new JarEntry(path);
				entry.setTime(lastModified);
				jos.putNextEntry(entry);
				jos.write(bytecode);
				jos.closeEntry();
			}

			jos.close();

			byte[] bytes = baos.toByteArray();

			return bytes;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

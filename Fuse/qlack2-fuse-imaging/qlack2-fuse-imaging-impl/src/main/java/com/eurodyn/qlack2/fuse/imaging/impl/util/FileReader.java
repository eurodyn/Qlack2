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
package com.eurodyn.qlack2.fuse.imaging.impl.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 *  #### CONSIDER REPLACING THIS CLASS WITH APACHE COMMONS I/O #####
 * @author European Dynamics SA
 */
public class FileReader {

    private static int defaultBinaryFileBufferSize = 8192;

    /**
     * Read binary file.
     *
     * @param filename
     * @return byte array
     * @throws Exception
     */
    public static byte[] readBinaryFile(String filename) throws Exception {
        return readBinaryFile(new File(filename));
    }

    /**
     * Read binary file.
     *
     * @param file
     * @return byte array
     * @throws Exception
     */
    public static byte[] readBinaryFile(File file) throws Exception {

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new Exception("Unsupported file size [" + fileSize + "]");
        }

        byte[] retVal = new byte[(int) fileSize];
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        int readFileSize = in.read(retVal, 0, (int) fileSize);
        in.close();

        if (readFileSize != fileSize) {
            throw new Exception("Incomplete file read [filesize=" + fileSize + ", read=" + readFileSize);
        }

        return retVal;
    }

    /**
     * Loads properties file
     * @param clazz
     * @param fileName
     * @return properties
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Properties loadProperties(Class clazz, String fileName) throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = clazz.getResourceAsStream(fileName);
        if (inputStream == null) {
            inputStream = clazz.getClassLoader().getResourceAsStream(fileName);
        }
        properties.load(inputStream);
        return properties;
    }

}
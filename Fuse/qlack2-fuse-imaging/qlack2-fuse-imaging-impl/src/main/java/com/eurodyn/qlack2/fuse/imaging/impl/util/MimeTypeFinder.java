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

import java.io.File;
import java.util.Collection;

import eu.medsea.mimeutil.MimeUtil2;

/**
 *
 * @author European Dynamics SA
 */
public class MimeTypeFinder {

    /**
     * Find  mime types of a file.
     *
     * @param f - file
     * @return Collection of mime types
     * @throws Exception
     */
    public static Collection findMimeTypes(File f) throws Exception {
        return (findMimeTypes(FileReader.readBinaryFile(f)));
    }

    /**
     * Find mime types of a file.
     * @param fileAsByteArray the file as a byte array
     * @return Collection of mime types
     */
    public static Collection findMimeTypes(byte[] fileAsByteArray) {
        Collection retVal = null;

        try {
            MimeUtil2 mimeUtil = new MimeUtil2();
            mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
            retVal = mimeUtil.getMimeTypes(fileAsByteArray);
            mimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        } catch (Exception e){
            System.out.println(e.toString());

        }
        return retVal;
    }
}


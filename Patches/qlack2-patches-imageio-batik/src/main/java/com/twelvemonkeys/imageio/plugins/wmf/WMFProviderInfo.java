/*
 * Copyright (c) 2015, Harald Kuhr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name "TwelveMonkeys" nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.twelvemonkeys.imageio.plugins.wmf;

import com.twelvemonkeys.imageio.spi.ReaderWriterProviderInfo;

/**
 * SVGProviderInfo.
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 */
final class WMFProviderInfo extends ReaderWriterProviderInfo {
  // This patch removes the following line to make the reader available in OSGi.
  //final static boolean WMF_READER_AVAILABLE = SystemUtil.isClassAvailable("com.twelvemonkeys.imageio.plugins.svg.SVGImageReader");
  final static boolean WMF_READER_AVAILABLE = true;

  protected WMFProviderInfo() {
    super(
      WMFProviderInfo.class,
      WMF_READER_AVAILABLE ? new String[]{"wmf", "WMF"} : new String[]{""}, // Names
      WMF_READER_AVAILABLE ? new String[]{"wmf"} : null, // Suffixes
      WMF_READER_AVAILABLE ? new String[]{"application/x-msmetafile", "image/x-wmf"} : null, // Mime-types
      "com.twelvemonkeys.imageio.plugins.wmf.WMFImageReader", // Reader class name..?
      new String[] {"com.twelvemonkeys.imageio.plugins.wmf.WMFImageReaderSpi"},
      null,
      null,
      false, null, null, null, null,
      true, null, null, null, null
    );
  }
}
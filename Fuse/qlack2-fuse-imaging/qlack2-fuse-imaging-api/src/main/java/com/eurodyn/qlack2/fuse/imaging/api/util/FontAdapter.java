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
package com.eurodyn.qlack2.fuse.imaging.api.util;



import java.awt.Font;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author European Dynamics SA
 */
public class FontAdapter extends XmlAdapter<QlackFont, Font> {

    public QlackFont marshal(Font f) {
        return new QlackFont(f.getFontName(), f.getStyle(), f.getSize());
    }

    public Font unmarshal(QlackFont f) {
        return new Font(f.getName(), f.getStyle(), f.getSize());
    }
}

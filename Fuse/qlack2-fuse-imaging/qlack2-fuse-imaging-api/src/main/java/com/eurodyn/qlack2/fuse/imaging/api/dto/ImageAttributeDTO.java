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
package com.eurodyn.qlack2.fuse.imaging.api.dto;



import javax.xml.bind.annotation.XmlType;

/**
 * Data Transfer object for Attribute.
 * @author European Dynamics SA.
 */
@XmlType(name="ImagingAttributeDTO")
public class ImageAttributeDTO extends BaseDTO {

    private static final long serialVersionUID = -5174562024033309778L;
    private String name;
    private String value;

    /**
     * Default costructor.
     */
    public ImageAttributeDTO() {
    }

    /**
     * Parameterized constructor.
     * @param name
     * @param value
     */
    public ImageAttributeDTO(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}

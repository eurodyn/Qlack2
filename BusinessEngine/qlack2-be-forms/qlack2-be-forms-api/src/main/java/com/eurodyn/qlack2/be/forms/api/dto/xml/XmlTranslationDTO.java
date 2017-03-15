package com.eurodyn.qlack2.be.forms.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;

public class XmlTranslationDTO {

	private String key;

	private XmlTranslationValuesDTO values;

	@XmlElement
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlElement
	public XmlTranslationValuesDTO getValues() {
		return values;
	}

	public void setValues(XmlTranslationValuesDTO values) {
		this.values = values;
	}




}

package com.eurodyn.qlack2.be.forms.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class XmlTranslationsDTO {

	private List<XmlTranslationDTO> translations;

	@XmlElement(name = "translation")
	public List<XmlTranslationDTO> getTranslations() {
		return translations;
	}

	public void setTranslations(List<XmlTranslationDTO> translations) {
		this.translations = translations;
	}

}

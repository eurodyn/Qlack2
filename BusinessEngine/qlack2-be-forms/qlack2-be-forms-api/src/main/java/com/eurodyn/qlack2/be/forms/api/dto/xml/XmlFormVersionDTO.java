package com.eurodyn.qlack2.be.forms.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "formVersion", propOrder = { "name", "description", "content",
		"conditions", "translations" })
public class XmlFormVersionDTO {
	private String name;
	private String description;
	private String content;
	private XmlConditionsDTO conditions;
	private XmlTranslationsDTO translations;

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@XmlElement
	public XmlConditionsDTO getConditions() {
		return conditions;
	}

	public void setConditions(XmlConditionsDTO conditions) {
		this.conditions = conditions;
	}

	@XmlElement
	public XmlTranslationsDTO getTranslations() {
		return translations;
	}

	public void setTranslations(XmlTranslationsDTO translations) {
		this.translations = translations;
	}

}

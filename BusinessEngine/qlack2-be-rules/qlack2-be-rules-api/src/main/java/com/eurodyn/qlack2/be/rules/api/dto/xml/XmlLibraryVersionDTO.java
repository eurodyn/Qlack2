package com.eurodyn.qlack2.be.rules.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "libraryVersion", propOrder = {
		"libraryName",
		"name",
		"description",
		"contentJar"
})
public class XmlLibraryVersionDTO {

	private String libraryName;
	private String name;
	private String description;
	private byte[] contentJar;

	// -- Accessors

	@XmlElement
	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

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
	public byte[] getContentJar() {
		return contentJar;
	}

	public void setContentJar(byte[] contentJar) {
		this.contentJar = contentJar;
	}

}

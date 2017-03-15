package com.eurodyn.qlack2.be.rules.api.dto.xml;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class XmlDataModelVersionsDTO {
	private List<XmlDataModelVersionDTO> dataModelVersions;

	@XmlElement(name = "dataModelVersion")
	public List<XmlDataModelVersionDTO> getDataModelVersions() {
		return dataModelVersions;
	}

	public void setDataModelVersions(List<XmlDataModelVersionDTO> dataModelVersions) {
		this.dataModelVersions = dataModelVersions;
	}

}

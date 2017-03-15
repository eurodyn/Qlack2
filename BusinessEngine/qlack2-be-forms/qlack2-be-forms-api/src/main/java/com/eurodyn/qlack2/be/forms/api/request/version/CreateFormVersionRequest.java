package com.eurodyn.qlack2.be.forms.api.request.version;

import java.util.List;

import com.eurodyn.qlack2.be.forms.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.forms.api.dto.TranslationDTO;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class CreateFormVersionRequest extends QSignedRequest {

	private String name;
	private String description;
	private String basedOn;
	private String content;
	private List<ConditionDTO> conditions;
	private List<TranslationDTO> translations;
	private String formId;

	/**
	 * Whether to use the orbeon template as content or not
	 */
	private boolean useTemplateContent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBasedOn() {
		return basedOn;
	}

	public void setBasedOn(String basedOn) {
		this.basedOn = basedOn;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<ConditionDTO> getConditions() {
		return conditions;
	}

	public void setConditions(List<ConditionDTO> conditions) {
		this.conditions = conditions;
	}

	public List<TranslationDTO> getTranslations() {
		return translations;
	}

	public void setTranslations(List<TranslationDTO> translations) {
		this.translations = translations;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public boolean isUseTemplateContent() {
		return useTemplateContent;
	}

	public void setUseTemplateContent(boolean useTemplateContent) {
		this.useTemplateContent = useTemplateContent;
	}

}

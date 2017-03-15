package com.eurodyn.qlack2.webdesktop.api;

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.webdesktop.api.dto.LexiconLanguageDTO;

public interface I18NService {
	List<LexiconLanguageDTO> getActiveLanguages();
	Map<String, String> getModuleTranslations(String groupName, String locale);
	Map<String, Map<String, String>> getTranslations(String locale);
}

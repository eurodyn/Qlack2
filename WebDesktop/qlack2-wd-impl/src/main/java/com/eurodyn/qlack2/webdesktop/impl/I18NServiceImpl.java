package com.eurodyn.qlack2.webdesktop.impl;

import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.KeyService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.webdesktop.api.I18NService;
import com.eurodyn.qlack2.webdesktop.api.dto.LexiconLanguageDTO;
import com.eurodyn.qlack2.webdesktop.impl.mappers.LanguageDTOMapperImpl;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@OsgiServiceProvider(classes = { I18NService.class })
@Transactional
public class I18NServiceImpl extends BaseService implements I18NService {
	private static final Logger LOGGER = Logger.getLogger(I18NServiceImpl.class.getName());
	@OsgiService @Inject
	private LanguageService languageService;
	@OsgiService @Inject
	private KeyService keyService;
	@OsgiService @Inject
	private GroupService groupService;
	
	// Mapstruct refs.
	private LanguageDTOMapperImpl languageDTOMapper = new LanguageDTOMapperImpl();
	
	@Override
	public List<LexiconLanguageDTO> getActiveLanguages() {
		return languageDTOMapper.toLexiconLanguageDTO(
				languageService.getLanguages(false));
	}
	@Override
	public Map<String, String> getModuleTranslations(String groupName, String locale) {
		GroupDTO group = groupService.getGroupByName(groupName);
		if (group == null) {
			LOGGER.log(Level.SEVERE, "Error retrieving translations for group {0}"
					+ ". The group does not exist.", groupName);
			return null;
		}

		Map<String, String> translations = keyService.getTranslationsForGroupAndLocale(group.getId(), locale);
		return translations;
	}
	@Override
	public Map<String, Map<String, String>> getTranslations(String locale) {
		Map<String, Map<String, String>> retVal = new HashMap<>();
		Set<GroupDTO> groups = groupService.getGroups();
		for (GroupDTO group : groups) {
			Map<String, String> translations = keyService.getTranslationsForGroupAndLocale(group.getId(), locale);
			retVal.put(group.getTitle(), translations);
		}
		return retVal;
	}
}

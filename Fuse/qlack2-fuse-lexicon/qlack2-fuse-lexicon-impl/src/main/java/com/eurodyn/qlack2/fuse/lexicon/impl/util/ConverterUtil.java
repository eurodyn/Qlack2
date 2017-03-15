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
package com.eurodyn.qlack2.fuse.lexicon.impl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.TemplateDTO;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.Data;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.Group;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.Key;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.Language;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.Template;

public class ConverterUtil {
	public static Group groupDTOToGroup(GroupDTO dto) {
		if (dto == null) {
			return null;
		}
		Group entity = new Group();
		entity.setTitle(dto.getTitle());
		entity.setDescription(dto.getDescription());
		return entity;
	}
	
	public static GroupDTO groupToGroupDTO(Group entity) {
		if (entity == null) {
			return null;
		}
		GroupDTO dto = new GroupDTO();
		dto.setId(entity.getId());
		dto.setTitle(entity.getTitle());
		dto.setDescription(entity.getDescription());
		return dto;
	}
	
	public static Set<GroupDTO> groupToGroupDTOSet(Collection<Group> entities) {
		if (entities == null) {
			return null;
		}
		Set<GroupDTO> dtos = new HashSet<>();
		for (Group entity : entities) {
			dtos.add(groupToGroupDTO(entity));
		}
		return dtos;
	}
	
	public static TemplateDTO templateToTemplateDTO(Template entity) {
		if (entity == null) {
			return null;
		}
		TemplateDTO dto = new TemplateDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setContent(entity.getContent());
		dto.setLanguageId(entity.getLanguage().getId());
		return dto;
	}

	public static Language languageDTOToLanguage(LanguageDTO dto) {
		if (dto == null) {
			return null;
		}
		Language entity = new Language();
		entity.setName(dto.getName());
		entity.setLocale(dto.getLocale());
		entity.setActive(dto.isActive());
		return entity;
	}

	public static LanguageDTO languageToLanguageDTO(Language entity) {
		if (entity == null) {
			return null;
		}
		LanguageDTO dto = new LanguageDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setLocale(entity.getLocale());
		dto.setActive(entity.isActive());
		return dto;
	}
	
	public static List<LanguageDTO> languageToLanguageDTOList(Collection<Language> entities) {
		if (entities == null) {
			return null;
		}
		List<LanguageDTO> dtos = new ArrayList<>(entities.size());
		for (Language entity : entities) {
			dtos.add(languageToLanguageDTO(entity));
		}
		return dtos;
	}
	
	public static KeyDTO keyToKeyDTO(Key entity, boolean includeTranslations) {
		if (entity == null) {
			return null;
		}
		KeyDTO dto = new KeyDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setGroupId(entity.getGroup().getId());
		if (includeTranslations) {
			Map<String, String> translations = new HashMap<>();
			if (entity.getData() != null) {
				for (Data data : entity.getData()) {
					translations.put(data.getLanguage().getId(), data.getValue());
				}
			}
			dto.setTranslations(translations);
		}
		return dto;
	}
	
	public static List<KeyDTO> keyToKeyDTOList(Collection<Key> entities, boolean includeTranslations) {
		if (entities == null) {
			return null;
		}
		List<KeyDTO> dtos = new ArrayList<>();
		for (Key entity : entities) {
			dtos.add(keyToKeyDTO(entity, includeTranslations));
		}
		return dtos;
	}
}

package com.eurodyn.qlack2.webdesktop.impl.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.eurodyn.qlack2.webdesktop.api.dto.LexiconLanguageDTO;

@Mapper
public interface LanguageDTOMapper {
	LexiconLanguageDTO toLexiconLanguageDTO(com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO dto);
	List<LexiconLanguageDTO> toLexiconLanguageDTO(List<com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO> dto);
	
}

package com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.util;

import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.dto.ApplicationRDTO;

public class ConverterUtil {
	public static ApplicationRDTO applicationInfoToApplicationRDTO(ApplicationInfo info) {
		if (info == null) {
			return null;
		}
		
		ApplicationRDTO dto = new ApplicationRDTO();
		dto.setId(info.getIdentification().getUniqueId());
		dto.setTitleKey(info.getInstantiation().getTranslationsGroup() + "." + info.getIdentification().getTitleKey());
		dto.setDescriptionKey(info.getInstantiation().getTranslationsGroup() + "." + info.getIdentification().getDescriptionKey());
//		dto.setGroupKey(info.getMenu().getType().getGroupKey());
		dto.setVersion(info.getIdentification().getVersion());
		dto.setActive(info.isActive());
		dto.setRestricted(info.getInstantiation().getRestrictAccess());
		return dto;
	}
}

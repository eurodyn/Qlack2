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
package com.eurodyn.qlack2.fuse.componentlibrary.impl.util;

import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentPermissionDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadget;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadgetHasPermission;

public class ConverterUtil {
	public static ComponentDTO gadgetToComponentDTO(ApiGadget entity) {
		if (entity == null) {
			return null;
		}
		
        ComponentDTO dto = new ComponentDTO();
        dto.setId(entity.getId());
        dto.setAuthor(entity.getAuthor());
        dto.setBoxLink(entity.getBoxLink());
        dto.setConfigPage(entity.getConfigPage());
        dto.setDescription(entity.getDescription());
        dto.setIconLink(entity.getIconLink());
        dto.setInfoLink(entity.getInfoLink());
        dto.setOwnerUserID(entity.getOwner());
        dto.setPageLink(entity.getPageLink());
        dto.setPrivateKey(entity.getPrivateKey());
        dto.setRegisteredOn(entity.getRegisteredOn());
        dto.setTitle(entity.getTitle());

        return dto;
    }
	
	
	public static ComponentPermissionDTO apiGadgetHasPermissionToComponentPermissionDTO(ApiGadgetHasPermission entity) {
		if (entity == null) {
			return null;
		}
		
		
		ComponentPermissionDTO dto = new ComponentPermissionDTO();
		dto.setId(entity.getId());
		dto.setEnabled(entity.getEnabled());
		dto.setPermission(entity.getPermission());
		dto.setUserID(entity.getUserId());

        return dto;
    }
}

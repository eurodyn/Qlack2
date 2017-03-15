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
package com.eurodyn.qlack2.fuse.simm.impl.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupAttributeDTO;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroup;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroupAttribute;

@Mapper
public abstract class SocialGroupAttributeDTOMapper {

	public SimGroupAttribute toSocialGroupAttribute(SocialGroupAttributeDTO src, SimGroup group) {
		SimGroupAttribute retVal = new SimGroupAttribute();
		retVal.setBindata(src.getBinData());
		retVal.setContentType(src.getContentType());
		retVal.setData(src.getData());
		retVal.setName(src.getName());
		retVal.setGroup(group);
		return retVal;
	}

	public List<SimGroupAttribute> toSocialGroupAttributeList(Collection<SocialGroupAttributeDTO> src, SimGroup group){
		if (src == null){
			return null;
		}
		List<SimGroupAttribute> entities = new ArrayList<SimGroupAttribute>(src.size());
		for (SocialGroupAttributeDTO entity : src){
			entities.add(toSocialGroupAttribute(entity, group));
		}
		return entities;
	}
	

}

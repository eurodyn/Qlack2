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
package com.eurodyn.qlack2.fuse.lexicon.impl;

import com.eurodyn.qlack2.fuse.lexicon.api.BundleUpdateService;
import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.*;
import com.eurodyn.qlack2.fuse.lexicon.impl.util.ConverterUtil;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Singleton
@Transactional
@OsgiServiceProvider(classes = {GroupService.class})
public class GroupServiceImpl implements GroupService {
	@PersistenceContext(unitName = "fuse-lexicon")
	private EntityManager em;

	// Querydsl fields.
	private QKey qKey = QKey.key;
	private QData qData = QData.data;
	private QLanguage qLanguage = QLanguage.language;
	private QGroup qGroup = QGroup.group;
	
	@Override
	@Transactional(TxType.REQUIRED)
	public String createGroup(GroupDTO group) {
		Group entity = ConverterUtil.groupDTOToGroup(group);
		em.persist(entity);
		return entity.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateGroup(GroupDTO group) {
		Group entity = Group.find(group.getId(), em);
		entity.setTitle(group.getTitle());
		entity.setDescription(group.getDescription());
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteGroup(String groupID) {
		em.remove(Group.find(groupID, em));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public GroupDTO getGroup(String groupID) {
		return ConverterUtil.groupToGroupDTO(Group.find(groupID, em));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public GroupDTO getGroupByName(String groupName) {
		return ConverterUtil.groupToGroupDTO(Group.findByName(groupName, em));
	}
	
	@Override
	public Set<GroupDTO> getRemainingGroups(List<String> excludedGroupNames){
		List<Group> groups = new JPAQueryFactory(em).selectFrom(qGroup).where(qGroup.title.notIn(excludedGroupNames)).fetch();
		return ConverterUtil.groupToGroupDTOSet(groups);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public Set<GroupDTO> getGroups() {
		Query q = em.createQuery("SELECT g FROM Group g");
		return ConverterUtil.groupToGroupDTOSet(q.getResultList());
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteLanguageTranslations(String groupID, String languageID) {
		Language language = Language.find(languageID, em);

		List<Data> dataList = Data.findByGroupIDAndLocale(groupID, language.getLocale(), em);
		for (Data data : dataList) {
			em.remove(data);
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteLanguageTranslationsByLocale(String groupID, String locale) {
		List<Data> dataList = Data.findByGroupIDAndLocale(groupID, locale, em);
		for (Data data : dataList) {
			em.remove(data);
		}
	}

	@Override
	@Transactional(TxType.SUPPORTS)
	public long getLastUpdateDateForLocale(String groupID, String locale) {
		// The default return value is 'now'.
		long retVal = Instant.now().toEpochMilli();

		// Find when was the last update of any keys on the requested group and
		// locale.		
		Data data = new JPAQueryFactory(em).selectFrom(qData)
				.innerJoin(qData.key, qKey)
				.where(qKey.group.id.eq(groupID), 
						qData.language.id.eq(
							JPAExpressions.select(qLanguage.id).from(qLanguage).where(qLanguage.locale.eq(locale))
						)
				).orderBy(qData.lastUpdatedOn.desc())
				.fetchFirst();
		
		if (data != null) {
			retVal = data.getLastUpdatedOn();
		}
		
		return retVal;
	}

}

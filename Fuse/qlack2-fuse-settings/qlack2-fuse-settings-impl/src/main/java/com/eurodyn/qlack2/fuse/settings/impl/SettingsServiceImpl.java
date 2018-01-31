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
package com.eurodyn.qlack2.fuse.settings.impl;

import com.eurodyn.qlack2.common.util.exception.QAlreadyExistsException;
import com.eurodyn.qlack2.common.util.exception.QDoesNotExistException;
import com.eurodyn.qlack2.fuse.settings.api.SettingsService;
import com.eurodyn.qlack2.fuse.settings.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;
import com.eurodyn.qlack2.fuse.settings.impl.mappers.SettingMapperImpl;
import com.eurodyn.qlack2.fuse.settings.impl.model.QSetting;
import com.eurodyn.qlack2.fuse.settings.impl.model.Setting;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

@Transactional
@Singleton
@OsgiServiceProvider(classes = { SettingsService.class })
public class SettingsServiceImpl implements SettingsService {
  // Logger ref.
  public static final Logger LOGGER = Logger.getLogger(SettingsServiceImpl.class.getName());
  public static final SettingMapperImpl mapper = new SettingMapperImpl();

  // An injected Entity Manager.
  @PersistenceContext(unitName = "fuse-settings")
  EntityManager em;

  @Override
  public List<SettingDTO> getSettings(String owner, boolean includeSensitive) {
    QSetting qsetting = QSetting.setting;
    JPAQuery<Setting> q = new JPAQueryFactory(em).selectFrom(qsetting)
      .where(qsetting.owner.eq(owner));
    if (!includeSensitive) {
      q.where(qsetting.sensitive.ne(true));
    }
    List<Setting> l = q.fetch();

    return mapper.map(l);
  }

  @Override
  public List<GroupDTO> getGroupNames(String owner) {
    QSetting qsetting = QSetting.setting;
    List<Setting> l = new JPAQueryFactory(em).selectFrom(qsetting)
      .where(qsetting.owner.eq(owner))
      .distinct()
      .orderBy(qsetting.group.asc())
      .fetch();

    return mapper.mapToGroupDTO(l);
  }

  @Override
  public SettingDTO getSetting(String owner, String key, String group) {
    SettingDTO retVal;

    QSetting qsetting = QSetting.setting;
    Setting setting = new JPAQueryFactory(em).selectFrom(qsetting)
      .where(qsetting.owner.eq(owner)
        .and(qsetting.key.eq(key))
        .and(qsetting.group.eq(group)))
      .fetchOne();

    if (setting == null) {
      throw new QDoesNotExistException(MessageFormat.format(
        "Did not find a setting with key: {0}.", key));
    } else {
      retVal = mapper.map(setting);
    }

    return retVal;
  }

  @Override
  public List<SettingDTO> getGroupSettings(String owner, String group) {
    QSetting qsetting = QSetting.setting;
    List<Setting> l = new JPAQueryFactory(em).selectFrom(qsetting)
      .where(qsetting.owner.eq(owner)
        .and(qsetting.group.eq(group)))
      .fetch();

    return mapper.map(l);
  }

  @Override
  public void createSetting(String owner, String group, String key, String val, boolean sensitive,
    boolean password) {
    try {
      getSetting(owner, key, group);
      throw new QAlreadyExistsException(MessageFormat.format(
        "A setting already exists with key: {0}.", key));
    } catch (QDoesNotExistException e) {
      Setting setting = new Setting();
      setting.setGroup(group);
      setting.setKey(key);
      setting.setOwner(owner);
      setting.setVal(val);
      setting.setSensitive(sensitive);
      setting.setPassword(password);

      em.persist(setting);
    }
  }

  @Override
  public void setVal(String owner, String key, String val, String group) {
    QSetting qsetting = QSetting.setting;
    Setting setting = new JPAQueryFactory(em).selectFrom(qsetting)
      .where(qsetting.owner.eq(owner)
        .and(qsetting.key.eq(key)
          .and(qsetting.group.eq(group))))
      .fetchOne();
    if (setting != null) {
      setting.setVal(val);
    } else {
      throw new QDoesNotExistException(MessageFormat.format(
        "Did not find a setting with key: {0}.", key));
    }
  }
}

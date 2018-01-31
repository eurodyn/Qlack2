/*
 * Copyright EUROPEAN DYNAMICS SA <info@eurodyn.com>
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
package com.eurodyn.qlack2.fuse.settings.api;

import com.eurodyn.qlack2.common.util.exception.QAlreadyExistsException;
import com.eurodyn.qlack2.common.util.exception.QDoesNotExistException;
import com.eurodyn.qlack2.fuse.settings.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;

import java.util.List;

/**
 * Provides functionality to set and retrieve settings held in the database.
 *
 * @author European Dynamics SA.
 */
public interface SettingsService {

  /**
   * Get the full list of settings.
   *
   * @param owner The owner for which the settings are retrieved.
   * @param includeSensitive Whether to include settings marked as sensitive.
   */
  List<SettingDTO> getSettings(String owner, boolean includeSensitive);

  /**
   * Returns the list of available groups of settings for a particular owner.
   *
   * @param owner The owner for which the groups are to be retrieved.
   */
  List<GroupDTO> getGroupNames(String owner);

  /**
   * Retrieve a specific setting.
   *
   * @param owner The owner of the setting.
   * @param key The key/name of the setting.
   * @param group The group on which this key/name belongs to.
   */
  SettingDTO getSetting(String owner, String key, String group) throws QDoesNotExistException;

  /**
   * Retrieves all settings of a specific group.
   *
   * @param owner The owner of the settings.
   * @param group The group to retrieve the settings for.
   */
  List<SettingDTO> getGroupSettings(String owner, String group);

  /**
   * Creates a new entry in settings.
   *
   * @param owner The owner of the setting.
   * @param group The group to which this setting belongs to.
   * @param key The name of the setting.
   * @param val The value of the setting.
   * @param sensitive Whether this setting is regarded as sensitive or not.
   * @param password Indicating this setting contains a password value.
   */
  void createSetting(String owner, String group, String key, String val, boolean sensitive,
    boolean password) throws QAlreadyExistsException;

  /**
   * Updates an existing setting.
   *
   * @param owner The owner of the setting.
   * @param key The name of the setting to update.
   * @param val The value to update the setting with.
   * @param group The group to which this setting belongs to.
   */
  void setVal(String owner, String key, String val, String group) throws QDoesNotExistException;
}

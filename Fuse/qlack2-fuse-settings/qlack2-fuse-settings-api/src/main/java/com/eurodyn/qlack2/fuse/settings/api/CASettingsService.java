package com.eurodyn.qlack2.fuse.settings.api;

import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;

import java.util.List;
import java.util.Optional;

/**
 * Provides functionality to set and retrieve settings held in OSGi Config Admin. Note that there
 * is no concept of 'groups', 'owner', and 'sensitive' settings in Config Admin as such settings are
 * regarded system-level (i.e. you should use the database-backed version of the settings if you
 * need to store such settings).
 *
 * @author European Dynamics SA.
 */
public interface CASettingsService {

  /**
   * Get the full list of settings.
   *
   * @param pid The name of the PID (i.e. .cfg file without the .cfg extension).
   */
  List<SettingDTO> getSettings(String pid);

  /**
   * Retrieve a specific setting.
   *
   * @param pid The name of the PID (i.e. .cfg file without the .cfg extension).
   * @param key The key/name of the setting.
   */
  Optional<SettingDTO> getSetting(String pid, String key);

  /**
   * Creates a new entry in settings.
   *
   * @param pid The name of the PID (i.e. .cfg file without the .cfg extension).
   * @param key The name of the setting.
   * @param val The value of the setting.
   */
  void createSetting(String pid, String key, String val);

  /**
   * Updates an existing setting.
   *
   * @param pid The name of the PID (i.e. .cfg file without the .cfg extension).
   * @param key The name of the setting to update.
   * @param val The value to update the setting with.
   */
  void setVal(String pid, String key, String val);

}
